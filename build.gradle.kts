buildscript {
    dependencies{
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.46")
        classpath("com.google.gms:google-services:4.4.1")
    }
}

plugins {
    id("com.android.application") version "8.5.0" apply false  // было 8.2.0
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false  // было 1.9.10
    id("com.google.dagger.hilt.android") version "2.51" apply false  // было 2.46
    id("com.google.firebase.crashlytics") version "3.0.2" apply false  // было 2.9.9
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false  // добавил
}

