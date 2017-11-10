# Firebase Demo project
A Demo project with simple exercises to give hands on experience.

**Please follow following steps to setup firebase with this project:**

### Step 1. Add Firebase to your app:
#### A. Use the Firebase Assistant
1. Click **Tools > Firebase** to open the **Assistant** window.
2. Click to expand **Authentication** feature, then click the provided link.
3. Click the **Connect to Firebase** button to connect to Firebase and add the necessary code to your app.

#### B. Manually add Firebase

If you prefer not to use the Firebase Assistant, you can still add Firebase to your app using the Firebase console.

To add Firebase to your app you'll need a Firebase project and a Firebase configuration file for your app.

1. Create a Firebase project in the **Firebase console**, if you don't already have one. If you already have an existing Google project associated with your mobile app, click Import Google Project. Otherwise, click Add project.
2. Click Add Firebase to your Android app and follow the setup steps. If you're importing an existing Google project, this may happen automatically and you can just download the config file.
3. When prompted, enter your app's package name. It's important to enter the package name your app is using; this can only be set when you add an app to your Firebase project.
4. At the end, you'll download a google-services.json file. You can download this file again at any time.
5. If you haven't done so already, copy this into your project's module folder, typically app/

### Step 2. Add the SDK

First, add following rules to your **root-level build.gradle** file, to include the google-services plugin.

```groovy
buildscript {
        // ...
        dependencies {
            // ...
            classpath 'com.google.gms:google-services:3.1.1' // google-services plugin
        }
    }
```
    
Then, in your module Gradle file (usually the **app/build.gradle**), add the apply plugin line at the bottom of the file to enable the Gradle plugin:

```groovy
apply plugin: 'com.android.application'

android {
  // ...
}

dependencies {
  // ...
  compile 'com.google.firebase:firebase-core:11.6.0'
  
  // Getting a "Could not find" error? Make sure you have
  // the latest Google Repository in the Android SDK manager
}

// ADD THIS AT THE BOTTOM
apply plugin: 'com.google.gms.google-services'
```

#### Available libraries:
com.google.firebase:firebase-core:11.6.0	> **Analytics** 

com.google.firebase:firebase-database:11.6.0	> **Realtime Database**

com.google.firebase:firebase-firestore:11.6.0	> **Cloud Firestore**

com.google.firebase:firebase-storage:11.6.0	> **Storage**

com.google.firebase:firebase-crash:11.6.0	> **Crash Reporting**

com.google.firebase:firebase-auth:11.6.0	> **Authentication**

com.google.firebase:firebase-messaging:11.6.0	> **Cloud Messaging**

com.google.firebase:firebase-config:11.6.0	> **Remote Config**

com.google.firebase:firebase-invites:11.6.0	> **Invites and Dynamic Links**

com.google.firebase:firebase-ads:11.6.0	> **AdMob**

com.google.firebase:firebase-appindexing:11.6.0	> **App Indexing**

com.google.firebase:firebase-perf:11.6.0	> **Performance Monitoring**


Reference: https://firebase.google.com/docs/android/setup