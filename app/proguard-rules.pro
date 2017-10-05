# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Applications/Android/sdk/tools/proguard/proguard-android.txt
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

-verbose
-dontobfuscate

#Pattonville App
-keepclassmembers class org.pattonvillecs.pattonvilleapp.** {
    *;
}
-keep class org.pattonvillecs.pattonvilleapp.**

#iCal4j
-dontwarn net.fortuna.ical4j.model.**
-dontwarn net.fortuna.ical4j.groovy.**
-dontwarn groovy.**
-keep class * extends net.fortuna.ical4j.validate.CalendarValidatorFactory
-keep class * extends net.fortuna.ical4j.model.Content$Factory

#Retrolambda
-dontwarn java.lang.invoke.*
-dontwarn **$$Lambda$*

#Picasso
-dontwarn com.squareup.okhttp.**

#Guava
-dontwarn java.lang.ClassValue

#Kryo
-dontwarn com.esotericsoftware.kryo.**
-keepclassmembers class * extends com.esotericsoftware.kryo.Serializer {
    *;
}

#KryoSerializers
-dontwarn de.javakaffee.kryoserializers.**

#ErrorProne
-dontwarn com.google.errorprone.annotations.*

#Support Library Bug
-keep class android.support.v7.widget.SearchView { *; }

#Spotlight
-keep class com.wooplr.spotlight.** { *; }
-keep interface com.wooplr.spotlight.**
-keep enum com.wooplr.spotlight.**

#Tests
-keepclassmembers class android.arch.persistence.room.** {
    *;
}
-keep class android.arch.persistence.room.**
-keepclassmembers class android.arch.lifecycle.** {
    *;
}
-keep class android.arch.lifecycle.**