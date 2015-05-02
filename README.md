# README #
 
Half Way There App -- to augment your pedometer by letting you know when you are half way to your goal from where you started.

### What is this repository for? ###

* My first Android App on the android store.
Helps the user to find out when they should turn around when trying to make a step goal.
Example: you are at 7500 steps of 10,000.  At 8750 steps it will signal that you should turn around.
App uses KitKat StepCounter Sensor, it will show how many steps you have walked.
* Future Versions
Will add capability to specify at what intervals to notify. Such as thirds, or fourths, etc.
Improve UI, add more functions. 
* Version 1.00

### How do I get set up? ###

* Summary of set up
* Configuration
* Dependencies
* How to run tests
1) Command Line -- Enter ./gradlew test, or ./gradlew clean test.  I prefer the later, it forces a rebuild of everything and then runs tests.
2) UI -- Android Studio, you need a version with experimental Unit test, or full unit test support.  Run the tests by right clicking and run.
* Deployment instructions
1) Command Line -- Enter ./gradlew assemble release  .  You must have already edited gradle to supply signing keys.
2) UI -- Select App, and Release build.  Click the Play button.
Files are located in your module, under the bin folder.

### Who do I talk to? ###

* hite77 
