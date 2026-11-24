plugins {
    id("com.android.library")
}

android {
    namespace = "dev.komik.ai.core.database"
    compileSdk = 35
    defaultConfig { minSdk = 31 }
}

dependencies {
    // M2+: Room entities/tables dari blueprint — didShin dirimak KSP (v M2.5)
    implementation("androidx.room:room-runtime:2.8.5")
}
