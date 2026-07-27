import java.util.Properties

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
        versionName = "1.0.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Room schema export location
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    val keystorePropertiesFile = file("keystore.properties")
    val keystoreProperties = Properties()
    val hasLocalKeystoreProperties = keystorePropertiesFile.exists()
    if (hasLocalKeystoreProperties) {
        keystoreProperties.load(keystorePropertiesFile.inputStream())
    }
    val hasEnvKeystore = System.getenv("KEYSTORE_FILE") != null
    val canSignRelease = hasLocalKeystoreProperties || hasEnvKeystore

    signingConfigs {
        create("release") {
            if (hasEnvKeystore) {
                // For GitHub Actions (reads from environment variables)
                storeFile = file(System.getenv("KEYSTORE_FILE"))
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
            } else if (hasLocalKeystoreProperties) {
                // For local/manual release builds (e.g. Play Console uploads)
                storeFile = file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Only sign if a keystore is available (env vars or local keystore.properties)
            if (canSignRelease) {
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

    // Drag-and-drop reorderable list
    implementation("sh.calvin.reorderable:reorderable:2.3.3")

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