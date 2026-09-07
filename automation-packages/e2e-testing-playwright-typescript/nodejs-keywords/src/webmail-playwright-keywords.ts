import { chromium } from 'playwright';

const DEFAULT_WEBMAIL_URL = 'https://demo-webmail.exense.ch/';

// Mailbox the OpenCart guest checkout sends its confirmation to. Shared demo
// credentials of a throw-away account - real credentials belong in a Step
// Parameter, not in a keyword source.
const MAILBOX_USER = 'customer@opencart.demo';
const MAILBOX_PASSWORD = '8Fm#%GzdSocv3o';

/**
 * Second half of the cross-application test: logs into the Roundcube webmail
 * client and opens the order confirmation the shop sent, reporting its subject
 * and date back to the plan.
 *
 * Properties carry the technical configuration:
 *   webmailUrl - base URL of the webmail client
 *   headless   - "true" to run the browser without a UI
 *
 * This keyword deliberately starts from its own browser: it verifies a
 * different application than "Purchase product in OpenCart", and sharing a
 * session between the two would hide whether the mail really arrived.
 */
async function readOrderConfirmationInWebmail(input, output, session, properties) {
    const webmailUrl = properties['webmailUrl'] ?? DEFAULT_WEBMAIL_URL;
    const headless = (properties['headless'] ?? 'true') === 'true';

    const browser = await chromium.launch({ headless });
    try {
        const page = await browser.newPage();

        await page.goto(webmailUrl);
        await page.waitForLoadState('domcontentloaded');
        await page.locator('#rcmloginuser').fill(MAILBOX_USER);
        await page.locator('#rcmloginpwd').fill(MAILBOX_PASSWORD);
        await page.locator('#rcmloginsubmit').click();

        // waitFor() throws if the mailbox never loads, so a failed login fails
        // the keyword here rather than further down on a confusing locator.
        await page.locator('#messagelist').waitFor();
        await page.locator('#rcmcountdisplay').waitFor();

        // Click the first email sent by "Your Store"
        await page.locator('text=Your Store').nth(0).click();

        // Roundcube renders the message body in an iframe
        const iframe = page.frameLocator('#messagecontframe');
        const title = await iframe.locator("//h2[@class='subject']").innerText();
        const date = await iframe.locator("//div[@class='header-summary']//span[@class='text-nowrap']").innerText();

        output.add('EmailTitle', title);
        output.add('EmailDate', date);
    } finally {
        await browser.close();
    }
}

export { readOrderConfirmationInWebmail as 'Read order confirmation in Webmail' };
