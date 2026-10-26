# Entry points LSPosed — não obfuscar
-keep class com.build.gcunlock.ModuleEntry       { *; }
-keep class com.build.gcunlock.ModuleApplication { *; }
-keep class com.build.gcunlock.core.**           { *; }
-keep class com.build.gcunlock.hooks.**          { *; }
-keep class io.github.libxposed.**              { *; }
# Manter nomes para logs
-keepnames class com.build.gcunlock.hooks.** { *; }
# Kotlin metadata
-keepattributes *Annotation*
-keepattributes RuntimeVisibleAnnotations
