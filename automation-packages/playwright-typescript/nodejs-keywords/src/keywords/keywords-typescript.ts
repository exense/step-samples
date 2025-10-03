const { chromium } = require('playwright');
import { execSync } from "child_process";

function ensurePlaywrightInstalled() {
	// The Java implementation of Playwright downloads and manages the browser binaries automatically
	// This is not the case with the Node.js implementation which expects to run "npx playwright install" first
	try {
		execSync("npx playwright install", { stdio: "inherit" });
	} catch (err) {
		console.error("Failed to install Playwright browsers:", err);
	}
}

export async function buyMacBookInOpenCart (input, output, session, properties) {
	ensurePlaywrightInstalled();
	const browser = await chromium.launch({
		headless: false
	});
	const context = await browser.newContext();
	try {
		const page = await context.newPage();
		await page.goto(input.url);
		await page.getByRole('link', { name: 'Desktops', exact: true }).click();
		await page.getByRole('link', { name: 'Mac (1)' }).click();
		await page.getByText('iMac', { exact: true }).click();
		const price = await page.getByRole('heading', { name: '$' }).textContent();
		output.send({"price": price})
		await page.getByRole('button', { name: 'Add to Cart', exact: true }).click();
	} finally {
		await context.close();
		await browser.close();
	}
}

export function onError (exception, input, output, session, properties) {
	console.log(exception)
	return true
}

