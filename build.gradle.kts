

buildscript {
    dependencies {
        classpath ("com.android.tools.build:gradle:7.2.2")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
        classpath ("com.google.gms:google-services:4.4.0")
        classpath ("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:1.9.22-1.0.16")

        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.6")
    }
}

plugins {
    id("com.android.application") version "8.1.3" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.16" apply false
    id("org.jetbrains.kotlin.kapt") version "1.9.22"
}



allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
