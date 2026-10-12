import { describe, it, after } from 'node:test';
import assert from 'node:assert';
import { runner } from 'step-node-agent';

// The runner's first argument is the keyword properties, the same channel Step
// feeds from its Parameters. Values are strings, exactly as they arrive on an
// agent - these are the values declared in automation-package.yaml.
const keywordRunner = runner({
    headless: 'true',
    targetUrl: 'https://opencart-prf.stepcloud.ch/',
    webmailUrl: 'https://demo-webmail.exense.ch/',
});

// Local equivalent of "OpenCart Test Set 01" / "OpenCart Test Case 01": the same
// two keywords, in the same order, with the same input.
describe('OpenCart Test Set 01', () => {
    after(() => keywordRunner.close());

    it('OpenCart Test Case 01: buys a MacBook and finds its confirmation mail', { timeout: 300000 }, async () => {
        // `run()` throws when a keyword reports an error, so reaching the next
        // line already proves the purchase went through.
        const purchase = await keywordRunner.run('Purchase product in OpenCart', { Product: 'MacBook' });
        assert.strictEqual(purchase.payload.Product, 'MacBook');
        assert.strictEqual(purchase.payload.OrderPlaced, 'true');

        const mail = await keywordRunner.run('Read order confirmation in Webmail');

        // Asserted as patterns rather than literals: subject and date come from
        // a live mailbox and must not break this test on every new order.
        assert.match(mail.payload.EmailTitle, /^Your Store - Order \d+/);
        assert.match(mail.payload.EmailDate, /^\d{4}-\d{2}-\d{2} \d{2}:\d{2}$/);
    });
});
