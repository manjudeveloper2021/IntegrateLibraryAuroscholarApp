plugins {
    id("maven-publish")
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id ("com.google.dagger.hilt.android")
}

android {
    compileSdk = 34
    defaultConfig {
        minSdk = 24   //24
        targetSdk = 34
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.extensions)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.adaptive.android)

//    def camerax_version = "1.3.0-beta01"
   /* implementation (libs.androidx.camera.camera2.v134)
    implementation (libs.androidx.camera.extensions.v134)*/

//    implementation(libs.androidx.datastore.core.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.ui.text.google.fonts)
    //noinspection UseTomlInstead
    implementation("com.google.accompanist:accompanist-navigation-animation:0.32.0")

  /*  val daggerHilt = "2.47"
    val coroutine = "1.7.1"
*/
    //dagger hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    //coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.lifecycle.runtime.compose)

    //paging 3
    implementation ( libs.androidx.paging.runtime.ktx)
    implementation (libs.androidx.paging.compose)
    
    //retrofit
    implementation (libs.retrofit)
    implementation (libs.logging.interceptor)
    implementation(libs.converter.gson)

    //moshi
    implementation(libs.moshi.kotlin)
    implementation (libs.converter.moshi)

    // Splash Screen
    implementation(libs.androidx.core.splashscreen)

    // For Coil- image loading library
    implementation(libs.coil.kt.coil.compose)
    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.coil.kt.coil.svg)

    implementation (libs.androidx.datastore.preferences)
    implementation (libs.androidx.datastore.preferences.core)

    //SMSRetrieval API Dependencies for Auto OTP Verification
    implementation(libs.play.services.auth.api.phone)
    implementation(libs.play.services.auth)
    implementation(libs.play.services.auth.v2050)

    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    implementation (libs.accompanist.pager)

    implementation (libs.foundation)
    implementation (libs.accompanist.pager.indicators)

    implementation (libs.androidx.lifecycle.viewmodel.ktx)
    implementation (libs.androidx.browser)

    //location
    implementation(libs.play.services.location)
    implementation(libs.accompanist.permissions)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.crashlytics.ktx)

    // Lottie for animation
    implementation(libs.lottie)

}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.AuroSdk"
                artifactId = "Auro_Scholar_Library"
                version = "0.1.0"
            }
        }
    }
}