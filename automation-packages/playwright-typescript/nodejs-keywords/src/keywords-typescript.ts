import { chromium } from 'playwright';
import { mkdtemp, readFile, rm } from 'node:fs/promises';
import { tmpdir } from 'node:os';
import { join } from 'node:path';

/**
 * Opens the OpenCart store, searches for a product and reports its price.
 *
 * Inputs carry the business data of the call:
 *   url         - base URL of the OpenCart store
 *   productName - product to search for, e.g. "iMac"
 *
 * Properties carry the technical configuration:
 *   headless    - "true" to run the browser without a UI
 *
 * The browser is deliberately left open: `checkout` continues in it, and Step
 * closes it when the session ends.
 */
export async function findProduct(input, output, session, properties) {
    // Technical settings belong in the properties rather than in the inputs: a
    // property can be defined once as a Parameter in Step and then applies to
    // every plan and execution, while inputs are provided per keyword call.
    // Property values are always strings, so "false" has to be compared
    // explicitly - a truthiness check would read it as true.
    const headless = properties['headless'] === 'true';

    const browser = await chromium.launch({ headless });

    // Registering the browser in the session is all the cleanup this sample
    // needs: Step closes every session value that exposes close() when the
    // session ends, and closing a browser closes its contexts and pages too.
    session.set('browser', browser);

    // A context isolates cookies, storage, etc. from other executions
    const context = await browser.newContext();

    // Capture screenshots and DOM snapshots so failures can be replayed
    await context.tracing.start({ screenshots: true, snapshots: true });
    try {
        const page = await context.newPage();

        // Shared with follow-up keywords through a plain object: it has nothing
        // to close, so Step leaves it alone and the browser above stays the
        // single owner of the cleanup.
        session.set('playwright', { context, page });

        // Global timeout for all Playwright actions on this page
        page.setDefaultTimeout(5000);

        await page.goto(input.url);

        // Type the product name into the search bar and submit
        const searchBar = page.locator('input[name="search"]');
        await searchBar.fill(input.productName);
        await searchBar.press('Enter');

        // Open the detail page of the first matching search result
        await page.getByRole('link', { name: input.productName }).first().click();

        // Report the displayed price so the plan can use it downstream
        const price = await page.getByRole('heading', { name: '$' }).textContent();
        output.add('price', price);
    } finally {
        // In a finally block so the trace is attached on failure too - that is
        // when it is most useful. The `onError` hook of the Step Node.js SDK is
        // a more generic alternative, but it can only reach the context through
        // the session.
        await stopTracingAndAttach(context, output, 'findProduct-trace');
    }
}

/**
 * Adds the product opened by `findProduct` to the cart and completes a guest
 * checkout, reusing the page from the Step session.
 */
export async function checkout(input, output, session, properties) {
    const { context, page } = session.get('playwright');

    // Trace this keyword's actions as a separate report attachment
    await context.tracing.start({ screenshots: true, snapshots: true });
    try {
        // Add the product currently shown on the page to the cart
        await page.getByRole('button', { name: 'Add to Cart', exact: true }).click();

        // Navigate to cart
        await page.locator('#cart > button').click();
        await page.getByRole('link', { name: 'View Cart' }).click();

        // Proceed to checkout
        await page.getByRole('link', { name: 'Checkout', exact: true }).click();

        // Step 1: Select guest checkout
        await page.waitForTimeout(1000);
        await page.getByRole('radio', { name: 'Guest Checkout' }).check();
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
        await page.locator('#input-payment-country').selectOption({ label: 'United States' });
        await page.locator('#input-payment-zone').selectOption({ label: 'New York' });
        await page.locator('#button-guest').click();

        // Step 3: Delivery method - continue with default
        await page.locator('#button-shipping-method').click();

        // Step 4: Payment method - agree to terms and continue
        await page.locator('input[name="agree"]').check();
        await page.locator('#button-payment-method').click();
        await page.waitForLoadState('networkidle');

        // Step 5: Confirm order
        await page.locator('#button-confirm').click();
    } finally {
        await stopTracingAndAttach(context, output, 'checkout-trace');
    }
}

const TRACE_MIME_TYPE = 'application/vnd.step.playwright-trace+zip';

/**
 * Stops the running trace and attaches it to the Step execution report.
 *
 * A load test runs several keywords concurrently on the same agent. Each one
 * gets its own forked process, but they all share the OS temp directory, so a
 * fixed file name would let one execution overwrite, read or delete another
 * execution's trace. `mkdtemp` hands each call a private directory instead;
 * the whole directory is removed once the trace has been attached.
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