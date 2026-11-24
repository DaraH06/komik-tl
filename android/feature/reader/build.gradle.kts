plugins {
    id("com.android.library")
}
// NOTE: dimulai M2 — Reader UI (via AndroidView, lihat decisions.md ADR 6).
android {
    namespace = "dev.komik.ai.feature.reader"
    compileSdk = 35
    defaultConfig { minSdk = 31 }
}
