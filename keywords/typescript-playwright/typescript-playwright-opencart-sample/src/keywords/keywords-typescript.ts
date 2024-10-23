export async function Echo (input, output, session, properties) {
    const { chromium } = require('playwright');
    const browser = await chromium.launch({
      headless: false
    });
    const context = await browser.newContext();
    const page = await context.newPage();
    await page.goto('https://www.exense.ch/');
    await page.locator('li').filter({ hasText: 'Consulting' }).click();
  
    await context.close();
    await browser.close();
}

export function onError (exception, input, output, session, properties) {
	console.log(exception)
	return true
}