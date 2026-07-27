using System.Threading.Tasks;
using Microsoft.Playwright;
using Newtonsoft.Json.Linq;
using Step.Handlers.NetHandler;

namespace Step.Examples.E2ETesting.Playwright
{
    public class WebmailPlaywrightKeywords : AbstractKeyword
    {
        private const string WebmailUrl = "https://demo-webmail.exense.ch/";

        [Keyword(name = "Read order confirmation in Webmail")]
        public void ReadOrderConfirmationInWebmail()
        {
            bool headless = input.Value<bool?>("Headless") ?? true;

            PlaywrightBrowsers.EnsureReady();


            ReadOrderConfirmationAsync(headless).GetAwaiter().GetResult();
        }

        private async Task ReadOrderConfirmationAsync(bool headless)
        {
            using var playwright = await Microsoft.Playwright.Playwright.CreateAsync();
            await using var browser = await playwright.Chromium.LaunchAsync(new BrowserTypeLaunchOptions { Headless = headless });
            var page = await browser.NewPageAsync();

            await page.GotoAsync(WebmailUrl);
            await page.WaitForLoadStateAsync(LoadState.DOMContentLoaded);
            await page.Locator("#rcmloginuser").FillAsync("customer@opencart.demo");
            await page.Locator("#rcmloginpwd").FillAsync("8Fm#%GzdSocv3o");
            await page.Locator("#rcmloginsubmit").ClickAsync();

            await page.Locator("#messagelist").WaitForAsync();
            await page.Locator("#rcmcountdisplay").WaitForAsync();

            // Click the first email sent by "Your Store"
            await page.Locator("text=Your Store").Nth(0).ClickAsync();

            var iframe = page.FrameLocator("#messagecontframe");
            string title = await iframe.Locator("//h2[@class='subject']").InnerTextAsync();
            string date = await iframe.Locator("//div[@class='header-summary']//span[@class='text-nowrap']").InnerTextAsync();

            output.Add("EmailTitle", title);
            output.Add("EmailDate", date);
        }
    }
}