# Requirements

- Android Gradle Plugin Version 4.2.2
- Gradle version 6.7.1
- Emulator: Pixel 4 API 30

# Getting Started

1. Set up the development environment
    - Android Studio is required. If you haven't already done so, download and install it.
    - Add the Google Play services SDK to Android Studio. The Maps SDK for Android is distributed as part of the Google Play services SDK, which you can add through the SDK Manager (in Preferences).
2. Set up an Android device in Android Studio
    - To run an app that uses the Maps SDK for Android, you must deploy it to an Android device or Android emulator that is based on Android 4.0 or higher and includes the Google APIs.
    - To use the Android Emulator, you can create a virtual device and install the emulator by using the Android Virtual Device (AVD) Manager that comes with Android Studio.
3. Enable and get Google Maps API Key
    - Go to [this link](https://developers.google.com/maps/documentation/android-sdk/start#set_up_in_cloud_console and) and follow step 1-3 in "Set up in Cloud Console" to obtain your API key.
4. Create a copy of `app/src/main/res/values/google_maps_api_template.xml` in the same directory and name that file `google_maps_api.xml`.
5. Paste your api key (starts with "AIz..") where it says `YOUR_KEY_HERE`.
6. Compile and run.

# Using Firebase Emulator

When developing and testing, we will use the emulator in order to not incur extra costs.

1. Go to https://console.firebase.google.com/ and sign in with your own Google account. 
2. Follow this guide to install Firebase CLI: https://firebase.google.com/docs/cli#install_the_firebase_cli
    1. `curl -sL https://firebase.tools | bash`
    2. `firebase login`
3. Follow this guide to install the emulator: https://firebase.google.com/docs/emulator-suite/install_and_configure. For now, we will need User Authentication emulator.