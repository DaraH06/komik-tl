plugins {
    id("com.android.library")
    id("com.google.devtools.ksp")
}

android {
    namespace = "dev.komik.ai.core.database"
    compileSdk = 36
    defaultConfig { minSdk = 31 }
}

dependencies {
    implementation("androidx.room:room-runtime:2.8.5")
    implementation("androidx.room:room-ktx:2.8.5")
    ksp("androidx.room:room-compiler:2.8.5")
}
