plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") apply false // dimutl mulai M2 (scaffold M0)
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
        externalNativeBuild {
            // Lihat engine/.cargo/config.toml untuk flags 16KB
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }

    externalNativeBuild {
        cargo {
            path("../engine/Cargo.toml")
            targetDirectory("../engine/target")
        }
    }
    externalNativeBuildEnabled = true
}

dependencies {
    // M2: compose, room, coil, workmanager
    // androidTest: ui-test-junit4
}
