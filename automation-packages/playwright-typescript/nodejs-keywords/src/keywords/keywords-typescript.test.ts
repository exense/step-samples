const runner = require('step-node-agent').runner({})
const assert = require('assert')

describe('buyMacBookInOpenCart', () => {
    it('should run the Keyword buyMacBookInOpenCart and verify output', async () => {
        // Unit-test buyMacBookInOpenCart using the Step Keyword runner
        var output = await runner.run('buyMacBookInOpenCart', {url: 'https://opencart-prf.stepcloud.ch/'})
        assert.equal(output.payload.price, '$122.00')
    });
});