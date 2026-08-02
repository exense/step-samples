import { describe, it, after } from 'node:test';
import assert from 'node:assert';
import { runner } from 'step-node-agent';

// The runner's first argument is the keyword properties, the same channel Step
// feeds from its Parameters. Values are strings, exactly as they arrive on an agent.
const keywordRunner = runner({ headless: 'true' });

describe('OpenCart e2e purchase flow', () => {
    // Disposes the keyword session, which closes the browser opened by findProduct
    after(() => keywordRunner.close());

    it('should search for iMac, verify its price, and complete the purchase', { timeout: 180000 }, async () => {
        // `run()` throws when a keyword reports an error, so reaching the next
        // line already proves the keyword succeeded.
        const found = await keywordRunner.run('findProduct', {
            url: 'https://opencart-prf.stepcloud.ch/',
            productName: 'iMac',
        });

        // Matched as a pattern rather than a literal: the price comes from a
        // live store and must not break this test when the catalogue changes.
        assert.match(found.payload.price, /^\$\d+\.\d{2}$/);

        await keywordRunner.run('checkout');
    });
});
