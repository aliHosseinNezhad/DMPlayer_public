import library.*

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = 32
    defaultConfig {
        minSdk = 21
        targetSdk = 32

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.7.0")

    coroutinesImplementations()

    androidTestImplementations()

    implementation("com.github.adrielcafe:AndroidAudioConverter:0.0.8")
    implementation("com.github.kshoji:javax.sound.midi-for-Android:0.0.3")
}

fun DependencyHandlerScope.androidTestImplementations() {
    androidTestImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.arch.core:core-testing:2.1.0")
    androidTestImplementation("com.google.truth:truth:1.0.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("org.mockito:mockito-core:2.21.0")
}

fun DependencyHandlerScope.coroutinesImplementations() {
    implementation(Libs.Coroutines.core)
    implementation(Libs.Coroutines.android)
    testImplementation(Libs.Coroutines.test)
    androidTestImplementation(Libs.Coroutines.test)
}