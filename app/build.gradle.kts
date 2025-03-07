import java.io.FileInputStream
import java.util.Properties

plugins {
        alias(libs.plugins.android.application)
        alias(libs.plugins.kotlin.android)
    }

android {
    namespace = "com.example.mobiswitch"
    compileSdk = 35
    signingConfigs {
        create("release") {
            val properties = Properties()
            val localPropsFile = rootProject.file("local.properties")
            if (localPropsFile.exists()) {
                properties.load(FileInputStream(localPropsFile))
            }

            val storeFilePath = properties.getProperty("storeFile") ?: ""
            val storePasswordVal = properties.getProperty("storePassword") ?: ""
            val keyAliasVal = properties.getProperty("keyAlias") ?: ""
            val keyPasswordVal = properties.getProperty("keyPassword") ?: ""

            if (storeFilePath.isNotEmpty()) {
                storeFile = file(storeFilePath)
                storePassword = storePasswordVal
                keyAlias = keyAliasVal
                keyPassword = keyPasswordVal
            }
        }
    }


    defaultConfig {
        applicationId = "com.example.mobiswitch"
        minSdk = 27
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}