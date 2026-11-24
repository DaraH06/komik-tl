plugins {
    id("com.android.library")
}
// NOTE: dimulai M2 — Library UI (LibraryGrid, importer SAF). Lihat M0-M2 dokumen.
android {
    namespace = "dev.komik.ai.feature.library"
    compileSdk = 35
    defaultConfig { minSdk = 31 }
}
