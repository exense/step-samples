import {chromium} from 'playwright';
import * as fs from 'fs';

export async function findProduct(input, output, session, properties) {
    // Launch a visible browser (headless: false) so the test can be observed
    const browser = await chromium.launch({
        headless: false
    });
    // Store the browser in the Step session so subsequent keywords can reuse it
    session.set("browser", browser);

    // Create a new browser context (isolated cookies, storage, etc.)
    const context = await browser.newContext();
    session.set("context", context);

    // Start a Playwright trace to capture screenshots and DOM snapshots for debugging
    await context.tracing.start({screenshots: true, snapshots: true});

    // Open a new page and store it in session for use by follow-up keywords (e.g. addToCart)
    const page = await context.newPage();
    session.set("page", page);

    // Set a global timeout of 5 seconds for all Playwright actions on this page
    page.setDefaultTimeout(5000);

    // Navigate to the OpenCart store URL provided as keyword input
    await page.goto(input.url);

    // Type the product name into the search bar and submit
    await page.locator('input[name="search"]').fill(input.productName);
    await page.locator('input[name="search"]').press('Enter');

    // Click the first search result matching the product name to open its detail page
    await page.getByRole('link', {name: input.productName}).first().click();

    // Read the displayed price and send it as a keyword output so Step can use it downstream
    const price = await page.getByRole('heading', {name: '$'}).textContent();
    output.send({"price": price})

    // Stop the trace, then attach the zip file to the Step execution report
    const tracePath = 'buyMacBookInOpenCart-trace.zip';
    await context.tracing.stop({path: tracePath});
    output.attach({name: tracePath, hexContent: fs.readFileSync(tracePath).toString('base64'), mimeType: "application/vnd.step.playwright-trace+zip"});
}

export async function checkout(input, output, session, properties) {
    // Retrieve the page and context opened by buyMacBookInOpenCart
    let page = session.get("page");
    let context = session.get("context");

    // Start a new trace segment for this keyword's actions
    await context.tracing.start({screenshots: true, snapshots: true});

    // Add the iMac product (currently shown on the page) to the cart
    await page.getByRole('button', {name: 'Add to Cart', exact: true}).click();

    // Navigate to cart
    await page.locator('#cart > button').click();
    await page.getByRole('link', {name: 'View Cart'}).click();

    // Proceed to checkout
    await page.getByRole('link', {name: 'Checkout', exact: true}).click();

    // Step 1: Select guest checkout
    await page.waitForTimeout(1000);
    await page.getByRole('radio', {name: 'Guest Checkout'}).check();
    await page.locator('#button-account').click();
    await page.waitForLoadState('networkidle');

    // Step 2: Billing details
    await page.locator('#input-payment-firstname').fill('John');
    await page.locator('#input-payment-lastname').fill('Doe');
    await page.locator('#input-payment-email').fill('john.doe@example.com');
    await page.locator('#input-payment-telephone').fill('0123456789');
    await page.locator('#input-payment-address-1').fill('123 Main Street');
    await page.locator('#input-payment-city').fill('New York');
    await page.locator('#input-payment-postcode').fill('10001');
    await page.locator('#input-payment-country').selectOption({label: 'United States'});
    await page.locator('#input-payment-zone').selectOption({label: 'New York'});
    await page.locator('#button-guest').click();

    // Step 3: Delivery method - continue with default
    await page.locator('#button-shipping-method').click();

    // Step 4: Payment method - agree to terms and continue
    await page.locator('input[name="agree"]').check();
    await page.locator('#button-payment-method').click();
    await page.waitForLoadState('networkidle');

    // Step 5: Confirm order
    await page.locator('#button-confirm').click();
    // Stop the trace and attach the zip to the Step execution report
    const tracePath = 'checkout-trace.zip';
    await context.tracing.stop({path: tracePath});
    output.attach({name: tracePath, hexContent: fs.readFileSync(tracePath).toString('base64'), mimeType: "application/vnd.step.playwright-trace+zip"});
}

export function onError(exception, input, output, session, properties) {
    return true
}

