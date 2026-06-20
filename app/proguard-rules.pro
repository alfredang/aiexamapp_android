# Keep kotlinx.serialization generated serializers
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class **$$serializer { *; }
-keepclasseswithmembers class com.tertiaryinfotech.aiexams.data.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.tertiaryinfotech.aiexams.data.**$$serializer { *; }
-keepclassmembers class com.tertiaryinfotech.aiexams.data.** {
    *** Companion;
}
