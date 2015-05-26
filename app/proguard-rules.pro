# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Applications/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-injars      build/intermediates/classes
-outjars     bin/classes-processed.jar
-libraryjars /usr/local/java/android-sdk/platforms/android-9/android.jar

-dontpreverify
-repackageclasses ''
-allowaccessmodification
-optimizations !code/simplification/arithmetic

-keep public class hiteware.com.halfwaythere.MainActivity

-keep class **$$ModuleAdapter
-keep class **$$InjectAdapter
-keep class **$$StaticInjection

-keep class hiteware.com.halfwaythere.TestModule
-keep class hiteware.com.halfwaythere.TestInjectableApplication
-keep class hiteware.com.halfwaythere.StepSensorChangeModule
-keep class hiteware.com.halfwaythere.ProductionModule
-keep class hiteware.com.halfwaythere.InjectableApplication

-keep class dagger.Lazy
