buildscript{
    dependencies{
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.6.0")
        classpath("com.google.gms:google-services:4.4.0")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
        classpath("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.1")
    }
}
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.0" apply false
    id("com.android.library") version "8.1.0" apply false
}