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

#-verbose
-dontobfuscate

#Pattonville App
-keep,includedescriptorclasses class org.pattonvillecs.pattonvilleapp.** {
    *;
}

#iCal4j
-dontwarn net.fortuna.ical4j.model.**
-dontwarn net.fortuna.ical4j.groovy.**
-dontwarn groovy.**
-keep,includedescriptorclasses class * extends net.fortuna.ical4j.validate.CalendarValidatorFactory
-keep,includedescriptorclasses class * extends net.fortuna.ical4j.model.Content$Factory
-keep,includedescriptorclasses class net.fortuna.ical4j.util.MapTimeZoneCache
-dontwarn net.fortuna.ical4j.util.JCacheTimeZoneCache

#Retrolambda
-dontwarn java.lang.invoke.*
-dontwarn **$$Lambda$*

#Picasso
-dontwarn com.squareup.okhttp.**

#OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

#Guava
-dontwarn java.lang.ClassValue
-dontwarn com.google.common.collect.EnumMultiset

#Kryo
-dontwarn com.esotericsoftware.kryo.**
-keepclassmembers,includedescriptorclasses class * extends com.esotericsoftware.kryo.Serializer {
    *;
}

#KryoSerializers
-dontwarn de.javakaffee.kryoserializers.**

#ErrorProne
-dontwarn com.google.errorprone.annotations.*

#Support Library Bug
-keep,includedescriptorclasses class android.support.v7.widget.SearchView { *; }

#Spotlight
-keep,includedescriptorclasses class com.wooplr.spotlight.** { *; }
-keep,includedescriptorclasses interface com.wooplr.spotlight.**
-keep,includedescriptorclasses enum com.wooplr.spotlight.**

#Lifecycles/Pagination
-dontwarn android.arch.paging.LivePagedListProvider*

#java.time JSR310
-keep class org.threeten.bp.** { *; }

#Tests
-keep,includedescriptorclasses class android.arch.persistence.room.** { *; }
-keep,includedescriptorclasses class android.arch.persistence.room.**
-keep,includedescriptorclasses class android.arch.lifecycle.**
-keep,includedescriptorclasses class kotlin.collections.CollectionsKt** { *; }
-keep,includedescriptorclasses class android.arch.persistence.db.SupportSQLiteDatabase { *; }
-keep,includedescriptorclasses class kotlin.jvm.internal.FunctionReference { *; }
-keep,includedescriptorclasses class kotlin.reflect.KCallable { *; }
-keep,includedescriptorclasses class kotlin.Triple { *; }
-keep,includedescriptorclasses class kotlin.ranges.ClosedRange { *; }
-keep,includedescriptorclasses class kotlin.text.StringsKt** { *; }
-keep,includedescriptorclasses class kotlin.collections.SetsKt** { *; }
-keep,includedescriptorclasses class kotlin.collections.MapsKt** { *; }
-keep,includedescriptorclasses class kotlin.Lazy** { *; }
-keep,includedescriptorclasses class kotlin.reflect.KClass** { *; }
-keep,includedescriptorclasses class kotlin.jvm.JvmClassMappingKt** { *; }
-keep,includedescriptorclasses class kotlin._Assertions** { *; }

#Okio
-dontwarn okio.**

#Retrofit
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

#CheckerFramework
-dontwarn afu.org.checkerframework.**
-dontwarn org.checkerframework.**

#SimpleXML RSS
-keepattributes *Annotation*
-keepclassmembers class org.simpleframework.xml.** {
    <init>(...);
}
-keep class com.github.magneticflux.rss.** { *; }
-keep class * extends org.simpleframework.xml.convert.Converter
-keep class * extends org.simpleframework.xml.transform.Transform
-dontwarn org.xmlpull.v1.**
-dontwarn javax.xml.**