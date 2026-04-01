// Unit tests for the OpenCart keywords (keywords-typescript.ts).
// They use the step-node-agent runner, which executes keywords the same way
// the Step platform does — with a real session object — so the two keywords
// can share browser/page state via session.set / session.get, exactly as they
// would in a live Step plan.
const runner = require('step-node-agent').runner({})
import assert = require('assert');

describe('OpenCart e2e purchase flow', () => {
    afterAll(() => runner.close());

    // TC1 — happy path: search for a product, verify its price, add it to the
    // cart, and complete a guest checkout end-to-end.
    // The two keywords are called sequentially on the same runner instance so
    // that the browser/page opened by findProduct is reused by
    // checkout via the Step session.
    it('should search for iMac, verify its price, and complete the purchase', async () => {
        // Step 1 — open the OpenCart store, search for "iMac", and assert the displayed price
        let output = await runner.run('findProduct', {url: 'https://opencart-prf.stepcloud.ch/', productName: 'iMac'});
        assert.equal(output.payload.price, '$122.00')

        // Step 2 — add the product to the cart, fill in guest-checkout billing
        // details, and confirm the order; assert no error was returned
        output = await runner.run('checkout', {})
        assert.ok(output.error == null)
    });
});