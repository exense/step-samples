import { chromium } from 'playwright';
import { mkdtemp, readFile, rm } from 'node:fs/promises';
import { tmpdir } from 'node:os';
import { join } from 'node:path';

const DEFAULT_SHOP_URL = 'https://opencart-prf.stepcloud.ch/';

/**
 * Buys a product in the OpenCart demo shop as a guest, from the product page
 * down to the order confirmation.
 *
 * Inputs carry the business data of the call:
 *   Product   - product to buy, e.g. "MacBook"
 *
 * Properties carry the technical configuration:
 *   targetUrl - base URL of the OpenCart store
 *   headless  - "true" to run the browser without a UI
 *
 * The order confirmation mail this purchase triggers is what the
 * "Read order confirmation in Webmail" keyword verifies afterwards.
 */
async function purchaseProductInOpenCart(input, output, session, properties) {
    const product = input.Product ?? 'MacBook';

    // Technical settings belong in the properties rather than in the inputs: a
    // property can be defined once as a Parameter in Step and then applies to
    // every plan and execution, while inputs are provided per keyword call.
    const shopUrl = properties['targetUrl'] ?? DEFAULT_SHOP_URL;
    // Property values are always strings, so "false" has to be compared
    // explicitly - a truthiness check would read it as true.
    const headless = (properties['headless'] ?? 'true') === 'true';

    const browser = await chromium.launch({ headless });
    try {
        // A context isolates cookies, storage, etc. from other executions
        const context = await browser.newContext();

        // Capture screenshots and DOM snapshots so failures can be replayed
        await context.tracing.start({ screenshots: true, snapshots: true });
        try {
            const page = await context.newPage();
            await page.goto(shopUrl);
            await page.locator(`text=${product}`).click();
            // The previous click loads quite a few resources such as jQuery etc.;
            // If we don't include this wait, the next click may hang forever.
            await page.waitForLoadState('domcontentloaded');
            await page.locator('text=Add to Cart').click();
            await page.locator('text=1 item').click();
            await page.locator('text=View Cart').click();
            await page.locator("//a[text()='Checkout']").click();
            await page.locator('text=Guest Checkout').click();
            await page.waitForLoadState('domcontentloaded');
            // Another timing issue potentially hanging the script
            await page.waitForTimeout(500);
            await page.locator('#button-account').click();
            await page.locator('#input-payment-firstname').fill('Gustav');
            await page.locator('#input-payment-lastname').fill('Muster');
            await page.locator('#input-payment-email').fill('customer@opencart.demo');
            await page.locator('#input-payment-telephone').fill('+41777777777');
            await page.locator('#input-payment-address-1').fill('Bahnhofstrasse 1');
            await page.locator('#input-payment-city').fill('Zurich');
            await page.locator('#input-payment-postcode').fill('8001');
            await page.locator('#input-payment-country').selectOption({ label: 'Switzerland' });
            await page.locator('#input-payment-zone').selectOption({ label: 'Zürich' });
            await page.locator('#button-guest').click();
            await page.locator('#button-shipping-method').click();
            await page.locator("//input[@name='agree']").setChecked(true);
            await page.locator('#button-payment-method').click();
            await page.locator('#button-confirm').click();

            // waitFor() is the assertion of this keyword: it throws - and fails
            // the keyword - if the confirmation never shows up.
            await page.locator('text=Your order has been placed!').waitFor();

            output.add('Product', product);
            output.add('OrderPlaced', 'true');
        } finally {
            // In a finally block so the trace is attached on failure too - that
            // is when it is most useful. The `onError` hook of the Step Node.js
            // SDK is a more generic alternative, but it can only reach the
            // context through the session.
            await stopTracingAndAttach(context, output, 'opencart-trace');
        }
    } finally {
        // Unlike the playwright-typescript sample, this keyword owns its browser
        // from start to end: the next keyword tests a different application and
        // starts from a clean browser of its own.
        await browser.close();
    }
}

const TRACE_MIME_TYPE = 'application/vnd.step.playwright-trace+zip';

/**
 * Stops the running trace and attaches it to the Step execution report, where
 * it can be replayed with the built-in Playwright trace viewer.
 *
 * Several keywords may run concurrently on the same agent. Each one gets its
 * own forked process, but they all share the OS temp directory, so a fixed file
 * name would let one execution overwrite, read or delete another execution's
 * trace. `mkdtemp` hands each call a private directory instead; the whole
 * directory is removed once the trace has been attached.
 *
 * The attachment keeps the stable, readable name so the execution report is
 * unaffected by the randomised directory.
 */
async function stopTracingAndAttach(context, output, traceName) {
    const fileName = `${traceName}.zip`;
    const traceDir = await mkdtemp(join(tmpdir(), 'step-playwright-trace-'));
    const tracePath = join(traceDir, fileName);

    await context.tracing.stop({ path: tracePath });
    try {
        output.attach({
            name: fileName,
            hexContent: (await readFile(tracePath)).toString('base64'),
            mimeType: TRACE_MIME_TYPE,
        });
    } finally {
        await rm(traceDir, { recursive: true, force: true }).catch(() => {
            /* best effort: never fail a keyword over a leftover temp directory */
        });
    }
}

// The Step Node runtime looks the keyword up by the `name` declared in
// automation-package.yaml, as a property of the compiled module. Exporting the
// function under a string alias therefore lets the TypeScript keyword keep the
// same plan-facing name as its Java and .NET counterparts.
export { purchaseProductInOpenCart as 'Purchase product in OpenCart' };
