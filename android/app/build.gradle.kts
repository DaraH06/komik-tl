plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "dev.komik.ai"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.komik.ai"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }

    buildFeatures {
        compose = true
    }

    // M1+: Rust Engine via cargo ndk (engine dir ada di root project); AGP 9.x
    // tidak punya `cargo` externalNativeBuild — lihat docs/roadmap.md M1 spike.
}

dependencies {
    // M2: Compose UI stack — attempt: ui 1.10.6 + material3 1.4.0 + activity-compose (lihat iterasi M2: mismatch inline Kotlin 2.4.20)
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.compose.ui:ui:1.10.6")
    implementation("androidx.compose.ui:ui-graphics:1.10.6")
    implementation("androidx.compose.ui:ui-test-junit4:1.10.6")
    implementation("androidx.compose.material3:material3:1.4.0")
    implementation("androidx.compose.ui:ui-text:1.10.6")
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.work:work-runtime:2.12.0") // M3: processing jobs/chapter
    implementation(project(":core:database")) // M2: Room data layer
    implementation("androidx.room:room-runtime:2.8.5")
    implementation(project(":feature:library")) // M2b: SAF import CBZ
    // M3: libtorch-android ktorun (sukTok), koharu-android EngineSession/Ffi

    testImplementation("junit:junit:4.13.2")
}
