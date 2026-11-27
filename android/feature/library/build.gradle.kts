plugins {
    id("com.android.library")
}
// NOTE: dimulai M2 — Library UI (LibraryGrid, importer SAF). Lihat M0-M2 dokumen.
android {
    namespace = "dev.komik.ai.feature.library"
    compileSdk = 36
    defaultConfig { minSdk = 31 }
}

dependencies {
    implementation(project(":core:database"))
    implementation("androidx.room:room-runtime:2.8.5")
}
