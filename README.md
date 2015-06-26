# README #
 
Half Way There App -- to augment your pedometer by letting you know when you are half way to your goal from where you started.

### What is this repository for? ###

* My first Android App on the android store.
* Helps the user to find out when they should turn around when trying to make a step goal.
Example: you are at 7500 steps of 10,000.  At 8750 steps it will signal that you should turn around.
App uses Accelerometer and a software pedometer that I built, it will show how many steps you have walked, and signal half way there as a notification.
* If you have android wear setup, it will notify to your android wearable.
* Version 1.00

### How do I get set up? ###

* Clone the repo to a local folder.  It will create a subfolder HalfWayThere
* In Android Studio, select File Open and the HalfWayThere folder that was created.
* Click on Build Variants, and change test artifact from Android Instrumentation Tests, to Unit Tests.
* How to run tests
1) Command Line -- Enter ./gradlew test, or ./gradlew clean test.  I prefer the later, it forces a rebuild of everything and then runs tests.
2) UI -- Android Studio, you need a version with experimental Unit test, or full unit test support.  Run the tests by right clicking and run.
* Deployment instructions
1) Command Line -- Enter ./gradlew assemble release  .  You must have already edited gradle to supply signing keys.
2) UI -- Android Studio click on Build, and then Generate Signed APK... , use adb to install to device.
* I used Junit tests, in combination with Robolectric to unit test the code, via TDD.
* Dependencies pulled into the project are Robolectric, Dagger, Android Support App Compatibility libraries, Mockito, Junit.

### Who do I talk to? ###

* hite77 
