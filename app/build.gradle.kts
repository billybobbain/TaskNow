plugins {
    id("com.google.devtools.ksp") version "2.0.21-1.0.27"
    id("org.cyclonedx.bom") version "1.10.0"
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.billybobbain.tasknow"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.billybobbain.tasknow"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            // For GitHub Actions (reads from environment variables)
            val keystoreFile = System.getenv("KEYSTORE_FILE")
            if (keystoreFile != null) {
                storeFile = file(keystoreFile)
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
            }
            // For local builds, you can create a keystore.properties file
            // See setup instructions in the workflow comments
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Only sign if keystore is available
            if (System.getenv("KEYSTORE_FILE") != null) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Room dependencies
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    ksp("androidx.room:room-compiler:$room_version")

    // OSMDroid for maps
    implementation("org.osmdroid:osmdroid-android:6.1.17")

    // Existing compose dependencies...
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

tasks.cyclonedxBom {
    setIncludeConfigs(listOf("releaseRuntimeClasspath"))
    setOutputFormat("json")
    setOutputName("bom")
    setProjectType("application")
    setSchemaVersion("1.4")
    setComponentName("TaskNow")
    setComponentVersion("1.0")
}