package library

object Versions {
    const val hilt = "1.0.0"
    const val compose = "1.2.0-rc03"
    const val composeCompiler = "1.2.0"
    const val room = "2.4.2"
    const val kotlin = "1.7.0"
    const val gradlePlugin = "7.0.4"
    const val coroutine = "1.6.1"
    const val dagger = "2.38.1"
    const val navigation = "2.4.1"
    const val exoPlayer = "2.17.1"
}

object Libs {
    const val MavenGradlePlugin = "com.github.dcendents:android-maven-gradle-plugin:1.5"
    const val AndroidGradlePlugin = "com.android.tools.build:gradle:${Versions.gradlePlugin}"
    const val NavigationGradlePlugin =
        "androidx.navigation:navigation-safe-args-gradle-plugin:2.4.1"
    const val MoshGradlePlugin = "com.squareup.moshi:moshi-kotlin:1.12.0"

    object Dagger {
        const val plugin =
            "com.google.dagger:hilt-android-gradle-plugin:${Versions.dagger}"

        // Dagger Core
        const val dagger = "com.google.dagger:dagger:${Versions.dagger}"
        const val daggerCompile = "com.google.dagger:dagger-compiler:${Versions.dagger}"

        // Dagger Android
        const val daggerAndroid = "com.google.dagger:dagger-android:${Versions.dagger}"
        const val daggerAndroidSupport =
            "com.google.dagger:dagger-android-support:${Versions.dagger}"
        const val daggerAndroidProcessor =
            "com.google.dagger:dagger-android-processor:${Versions.dagger}"

        // Dagger - Hilt
        const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.dagger}"
        const val hiltAndroidCompiler = "com.google.dagger:hilt-android-compiler:${Versions.dagger}"
    }

    object Kotlin {
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
        const val extensions = "org.jetbrains.kotlin:kotlin-android-extensions:${Versions.kotlin}"
    }

    object Coroutines {
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutine}"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutine}"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutine}"

    }

    object AndroidX {
        //for compose
        const val constraintLayoutCompose =
            "androidx.constraintlayout:constraintlayout-compose:1.0.0"
        const val navigationCompose = "androidx.navigation:navigation-compose:2.5.0-rc01"
        const val activityCompose = "androidx.activity:activity-compose:1.4.0"
        const val pagingCompose = "androidx.paging:paging-compose:1.0.0-alpha14"

        const val media = "androidx.media:media:1.5.0"

        const val core = "androidx.core:core-ktx:1.7.0"
        const val coreTest = "androidx.arch.core:core-testing:2.1.0"

        const val dataStorePreferences = "androidx.datastore:datastore-preferences:1.0.0"
        const val legacySupport = "androidx.legacy:legacy-support-v4:1.0.0"
        const val appcompat = "androidx.appcompat:appcompat:1.4.1"
        const val runner = "androidx.test:runner:1.4.0"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.1.3"

        object Lifecycle {
            const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1"
            const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:2.4.1"
            const val extensions = "androidx.lifecycle:lifecycle-extensions:2.2.0"
            const val livedata = "androidx.lifecycle:lifecycle-livedata-ktx:2.4.1"
            const val viewModelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:2.4.1"
        }

        object Room {
            const val runtime = "androidx.room:room-runtime:${Versions.room}"
            const val compile = "androidx.room:room-compiler:${Versions.room}"
            const val extensions = "androidx.room:room-ktx:${Versions.room}"
            const val test = "androidx.room:room-testing:${Versions.room}"
        }

        object Compose {
            const val ui = "androidx.compose.ui:ui:${Versions.compose}"
            const val uiUtils = "androidx.compose.ui:ui-util:${Versions.compose}"
            const val runtime = "androidx.compose.runtime:runtime:${Versions.compose}"
            const val material = "androidx.compose.material:material:${Versions.compose}"
            const val runtimeLivedata =
                "androidx.compose.runtime:runtime-livedata:${Versions.compose}"
            const val materialIconExtended =
                "androidx.compose.material:material-icons-extended:${Versions.compose}"
            const val uiTooling = "androidx.compose.ui:ui-tooling:${Versions.compose}"
            const val uiToolingPreview =
                "androidx.compose.ui:ui-tooling-preview:${Versions.compose}"
            const val uiTestJunit = "androidx.compose.ui:ui-test-junit4:${Versions.compose}"
        }

        object Navigation {
            const val ui = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"
            const val fragment =
                "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
            const val dynamicFeaturesFragment =
                "androidx.navigation:navigation-dynamic-features-fragment:${Versions.navigation}"
            const val test = "androidx.navigation:navigation-testing:${Versions.navigation}"
        }

        object Work {
            const val runtime = "androidx.work:work-runtime-ktx:2.7.1"
        }

        object Hilt {
            const val compiler = "androidx.hilt:hilt-compiler:${Versions.hilt}"
            const val navigationFragment = "androidx.hilt:hilt-navigation-fragment:${Versions.hilt}"
            const val navigationCompose = "androidx.hilt:hilt-navigation-compose:${Versions.hilt}"
        }
    }

    object Accompanist {
        const val pager = "com.google.accompanist:accompanist-pager:0.13.0"
        const val pagerIndicators = "com.google.accompanist:accompanist-pager-indicators:0.13.0"

        const val insets = "com.google.accompanist:accompanist-insets:0.23.0"
        const val systemUIController =
            "com.google.accompanist:accompanist-systemuicontroller:0.23.0"
        const val flowLayout = "com.google.accompanist:accompanist-flowlayout:0.23.0"
    }

    object ExoPlayer {
        const val core = "com.google.android.exoplayer:exoplayer-core:${Versions.exoPlayer}"
        const val dash = "com.google.android.exoplayer:exoplayer-dash:${Versions.exoPlayer}"
        const val ui = "com.google.android.exoplayer:exoplayer-ui:${Versions.exoPlayer}"
        const val session =
            "com.google.android.exoplayer:extension-mediasession:${Versions.exoPlayer}"
    }

    const val retrofit = "com.squareup.retrofit2:retrofit:2.9.0"
}