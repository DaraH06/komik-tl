plugins {
    id("com.android.library")
    // KSP (Room compiler): pickup M3 — versi stabil saat ini belum compatible dengan Kotlin 2.4.20 di AGP 9.5.2, coba ksp 2.3.14+ atau ksp 2.4.x
}

android {
    namespace = "dev.komik.ai.core.database"
    compileSdk = 35
    defaultConfig { minSdk = 31 }
}

dependencies {
    implementation("androidx.room:room-runtime:2.8.5")
    // ksp("androidx.room:room-compiler:2.8.5") — pickup saat M3
}
