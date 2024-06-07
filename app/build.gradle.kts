plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.org.jetbrains.kotlin.kapt)
    alias(libs.plugins.com.google.dagger.hilt.android)
}

android {
    namespace = "com.marlove.catalog"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.marlove.catalog"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation (libs.androidx.swiperefreshlayout )

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.com.squareup.retrofit2.retrofit )
    implementation(libs.com.squareup.retrofit2.converter.gson)
    implementation(libs.com.squareup.okhttp3.okhttp)

    implementation( libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.paging.runtime.ktx)

    // Dagger - Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    kapt(libs.hilt.compiler)

    // Coroutines dependency
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.kermit)

    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)

    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.mockwebserver)

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$1.9.0-RC")
}