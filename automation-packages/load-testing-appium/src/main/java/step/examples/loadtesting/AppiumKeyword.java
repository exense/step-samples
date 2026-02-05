package step.examples.loadtesting;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import step.grid.io.AttachmentHelper;
import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;

import java.io.File;
import java.time.Duration;

public class AppiumKeyword extends AbstractKeyword {

    @Keyword
    public void AppiumDemoAppKeyword() {
        // 1. Start Appium Server Programmatically
        try (AppiumDriverLocalService service = new AppiumServiceBuilder()
                .withIPAddress("127.0.0.1")
                .usingPort(4723)
                .build()) {
            AndroidDriver driver = null;
            try {
                service.start();

                // 2. Setup Emulator & App Options
                String apkPath = "src/main/resources/mda-2.2.0-25.apk";
                if (this.isInAutomationPackage()) {
                    File file = this.retrieveAndExtractAutomationPackage();
                    output.add("file", file.getAbsolutePath());
                    apkPath = file.getAbsolutePath() + "\\mda-2.2.0-25.apk";
                }

                // 1. Setup Options with specific timeouts
                UiAutomator2Options options = new UiAutomator2Options()
                        .setAvd("Step_Mobile_Stable")
                        .setAvdLaunchTimeout(Duration.ofMinutes(3)) // Max time to wait for boot
                        .setAvdReadyTimeout(Duration.ofMinutes(2))   // Max time to wait for ADB response
                        .setApp(apkPath)
                        .setAppWaitActivity("com.saucelabs.mydemoapp.android.view.activities.MainActivity") // Wait for this specific screen
                        .setAppWaitDuration(Duration.ofMillis(30000))
                        // This bypasses many permission popups in API 34
                        .amend("appium:autoGrantPermissions", true)
                        // Optimization: Don't spend time signing the app if not needed
                        .amend("appium:noSign", true)
                        .amend("appium:adbExecTimeout", 60000)
                        .amend("appium:uiautomator2ServerInstallTimeout", 90000);
                options.setCapability("appium:disableWindowAnimation", true); // Improves stability


                // 3. Initialize Driver (This installs and starts the app)
                driver = new AndroidDriver(service.getUrl(), options);

                // 3. Optional: Explicit Wait for a specific element (The real "App is Ready" signal)
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
                wait.until(ExpectedConditions.presenceOfElementLocated(AppiumBy.accessibilityId("App logo and name")));

                WebElement el1 = driver.findElement(AppiumBy.accessibilityId("View menu"));
                el1.click();
                wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.androidUIAutomator("new UiSelector().text(\"Catalog\")")));
                WebElement el2 = driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().text(\"Catalog\")"));
                el2.click();
                wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/productIV\").instance(0)")));
                WebElement el3 = driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/productIV\").instance(0)"));
                el3.click();
                wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId("Tap to add product to cart")));
                WebElement el4 = driver.findElement(AppiumBy.accessibilityId("Tap to add product to cart"));
                el4.click();
                wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.id("com.saucelabs.mydemoapp.android:id/cartTV")));
                WebElement el5 = driver.findElement(AppiumBy.id("com.saucelabs.mydemoapp.android:id/cartTV"));
                el5.click();
                wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.id("com.saucelabs.mydemoapp.android:id/cartBt")));

                System.out.println("Emulator booted and App is interactive!");

                // 4. Close the Application
                driver.terminateApp("com.saucelabs.mydemoapp.android");
                System.out.println("Application Closed.");
            } catch (Exception e) {
                if (driver != null) {
                    // Capture screenshot on failure
                    byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                    // Attach it to the Step report (using Step's Attachment API)
                    // This makes the screenshot visible in the Step report
                    output.addAttachment(AttachmentHelper.generateAttachmentFromByteArray(screenshot, "Screenshot on Failure", "image/png"));
                }
                throw e; // Re-throw to mark the Step as failed
            } finally {
                if (driver != null) {
                    // 5. Quit Driver Session
                    driver.quit();
                }

                // 6. Shutdown the Emulator (Using ADB command)
                // Appium quit() closes the session, but doesn't kill the emulator hardware.
                try {
                    Runtime.getRuntime().exec("adb -s emulator-5554 emu kill");
                    System.out.println("Emulator Shutdown Signal Sent.");
                } catch (Exception e) {
                    System.out.println("Could not kill emulator: " + e.getMessage());
                }

                // 7. Stop Appium Server
                service.stop();
            }
        }
    }
}
