plugins {
    id("com.android.application")
}

android {
    namespace = "dev.komik.ai"
    compileSdk = 35

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

    // M1+: Rust Engine via cargo ndk (engine dir ada di root project); AGP 9.x
    // tidak punya `cargo` externalNativeBuild — lihat docs/roadmap.md M1 spike.
}

dependencies {
    // M0: appcompat (Compose + dependency-injection screens M2; lihat roadmap)
    implementation("androidx.appcompat:appcompat:1.7.1")

    testImplementation("junit:junit:4.13.2")
}
