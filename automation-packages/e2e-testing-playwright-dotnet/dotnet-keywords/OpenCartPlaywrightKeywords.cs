using System;
using System.IO;
using System.Threading.Tasks;
using Microsoft.Playwright;
using Newtonsoft.Json.Linq;
using Step.Grid.IO;
using Step.Handlers.NetHandler;

namespace Step.Examples.E2ETesting.Playwright
{
    public class OpenCartPlaywrightKeywords : AbstractKeyword
    {
        private const string DefaultShopUrl = "https://opencart-prf.stepcloud.ch/";

        [Keyword(name = "Purchase product in OpenCart", properties = new string[] { "targetUrl" })]
        public void PurchaseProductInOpenCart()
        {
            string product = input.Value<string>("Product") ?? "MacBook";
            string shopUrl = properties.ContainsKey("targetUrl") ? properties["targetUrl"] : DefaultShopUrl;
            // Step agents have no display, so the keyword runs headless unless asked otherwise.
            bool headless = input.Value<bool?>("Headless") ?? true;

            PlaywrightBrowsers.EnsureReady();

            // The Step .NET handler invokes keywords synchronously, so the async Playwright
            // API is driven to completion here.
            PurchaseProductAsync(shopUrl, product, headless).GetAwaiter().GetResult();
        }

        private async Task PurchaseProductAsync(string shopUrl, string product, bool headless)
        {
            using var playwright = await Microsoft.Playwright.Playwright.CreateAsync();
            await using var browser = await playwright.Chromium.LaunchAsync(new BrowserTypeLaunchOptions { Headless = headless });
            await using var context = await browser.NewContextAsync();
            await context.Tracing.StartAsync(new TracingStartOptions { Screenshots = true, Snapshots = true });

            try
            {
                var page = await context.NewPageAsync();
                await page.GotoAsync(shopUrl);
                // Type the product name into the search bar and submit
                var searchBar = page.Locator("input[name='search']");
                await searchBar.FillAsync(product);
                await searchBar.PressAsync("Enter");
                // Open the detail page of the first matching search result
                await page.GetByRole(AriaRole.Link, new PageGetByRoleOptions { Name = product }).First.ClickAsync();
                // The previous click loads quite a few resources such as jQuery etc.;
                // If we don't include this wait, the next click may hang forever.
                await page.WaitForLoadStateAsync(LoadState.DOMContentLoaded);
                await page.Locator("text=Add to Cart").ClickAsync();
                await page.Locator("text=1 item").ClickAsync();
                await page.Locator("text=View Cart").ClickAsync();
                await page.Locator("//a[text()='Checkout']").ClickAsync();
                await page.Locator("text=Guest Checkout").ClickAsync();
                await page.WaitForLoadStateAsync(LoadState.DOMContentLoaded);
                // Another timing issue potentially hanging the script
                await Task.Delay(500);
                await page.Locator("#button-account").ClickAsync();
                await page.Locator("#input-payment-firstname").FillAsync("Gustav");
                await page.Locator("#input-payment-lastname").FillAsync("Muster");
                await page.Locator("#input-payment-email").FillAsync("customer@opencart.demo");
                await page.Locator("#input-payment-telephone").FillAsync("+41777777777");
                await page.Locator("#input-payment-address-1").FillAsync("Bahnhofstrasse 1");
                await page.Locator("#input-payment-city").FillAsync("Zurich");
                await page.Locator("#input-payment-postcode").FillAsync("8001");
                await page.Locator("#input-payment-country").SelectOptionAsync(new SelectOptionValue { Label = "Switzerland" });
                await page.Locator("#input-payment-zone").SelectOptionAsync(new SelectOptionValue { Label = "Zürich" });
                await page.Locator("#button-guest").ClickAsync();
                await page.Locator("#button-shipping-method").ClickAsync();
                await page.Locator("//input[@name='agree']").SetCheckedAsync(true);
                await page.Locator("#button-payment-method").ClickAsync();
                await page.Locator("#button-confirm").ClickAsync();
                await page.Locator("text=Your order has been placed!").WaitForAsync();

                output.Add("Product", product);
                output.Add("OrderPlaced", "true");
            }
            finally
            {
                // The trace is attached to the Step report node, where it can be replayed
                // with the built-in Playwright trace viewer.
                string tracePath = Path.Combine(Path.GetTempPath(), "opencart-trace-" + Guid.NewGuid() + ".zip");
                await context.Tracing.StopAsync(new TracingStopOptions { Path = tracePath });
                output.AddAttachment(AttachmentHelper.GenerateAttachmentFromByteArray(File.ReadAllBytes(tracePath), "trace.zip"));
                File.Delete(tracePath);
            }
        }
    }
}