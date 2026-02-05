# Appium Load Testing with Step

This sample demonstrates how to perform mobile automation and load testing using Appium and the Step platform.

## Prerequisites

* **Demo Application:** We use the Sauce Labs demo app ([Download here](https://github.com/saucelabs/my-demo-app-android/releases)).
    * Version used: `mda-2.2.0-25.apk`
    * Place the downloaded APK in: `src/main/resources/`
* **Java:** Version 11 or higher is required.

---

## Setup

### 1. Android Command Line Tools
1. Go to [Android Studio Downloads](https://developer.android.com/studio) and scroll to the **"Command line tools only"** section.
2. Download the zip for your OS.
3. Create your `ANDROID_HOME` folder:
    * **Linux:** `~/Android/Sdk/`
    * **Windows:** `%LOCALAPPDATA%\Android\Sdk\`
4. Extract the archive. Move the contents (`bin` and `lib` folders) to:
   `ANDROID_HOME/cmdline-tools/latest/`
   *(Note: You must manually create the `latest` folder).*

### 2. Environment Variables
Set the following variables in your system:

* **ANDROID_HOME:** `%LOCALAPPDATA%\Android\Sdk`
* **JAVA_HOME:** Path to your JDK installation.
* **Path:** Add these entries to your system Path:
    * `%ANDROID_HOME%\cmdline-tools\latest\bin`
    * `%ANDROID_HOME%\platform-tools`
    * `%ANDROID_HOME%\emulator`

### 3. Install SDK and Platform Tools
Open a terminal and execute the following to install the stable API 34 components:

```bash
# Accept licenses
sdkmanager --licenses

# Install Platform and System Image
sdkmanager "platforms;android-34" "system-images;android-34;google_apis;x86_64"

# Install Platform Tools
sdkmanager "platform-tools"
```

### 4. Create the Emulator
Create the Virtual Device (AVD) using the Pixel 6 hardware profile:

```bash
avdmanager create avd -n Step_Mobile_Stable -k "system-images;android-34;google_apis;x86_64" --device "pixel_6"
```

### 5. Install Node.js & Appium
1. Download and install **Node.js** (Version 24+ recommended) from [nodejs.org](https://nodejs.org/).
2. Install Appium and the UiAutomator2 driver:

```bash
npm install -g appium
appium driver install uiautomator2
```

3. **Validate Installation:**
```bash
appium driver doctor uiautomator2
```

---

## Recording and Scripting

We use **Appium Inspector** to map the application and record interactions.

1. Download the latest version from [Appium Inspector Releases](https://github.com/appium/appium-inspector/releases).
2. Start your local Appium server and the emulator:
    * **Appium:** Run `appium` in a terminal.
    * **Emulator:** Run `emulator -avd Step_Mobile_Stable` in a terminal.
3. In Appium Inspector, start a session with the following JSON (update the `app` path):

```json
{
  "platformName": "Android",
  "appium:automationName": "UiAutomator2",
  "appium:deviceName": "Step_Mobile_Stable",
  "appium:app": "C:/path/to/your/src/main/resources/mda-2.2.0-25.apk",
  "appium:ensureWebviewsHavePages": true,
  "appium:appWaitActivity": "com.saucelabs.mydemoapp.android.view.activities.MainActivity",
  "appium:nativeWebScreenshot": true,
  "appium:newCommandTimeout": 3600,
  "appium:connectHardwareKeyboard": true
}
```

---

## Execution

### Run Locally
Execute the JUnit test class `AppiumKeywordTest` to verify the logic on your local machine.

### Execute on Step
1. **Prepare Agents:**
   Hardware acceleration is required to run emulators. Repeat the setup procedure on each Step Agent machine (Appium, Android SDK, and AVD creation).

   > **Note:** For bare-metal or on-premise implementations, ensure that the Agent user has the correct environment variables and permissions to launch the emulator.

2. **Deploy Package:**
   Upload the generated Automation Package to your Step instance. Refer to the standard Step documentation for deployment and execution.