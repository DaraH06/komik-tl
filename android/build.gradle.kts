plugins {
    id("com.android.application") version "9.5.2" apply false
    id("com.google.devtools.ksp") version "2.3.14" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.10" apply false
}
// NOTE: AGP 9.x memiliki Kotlin built-in (plugin kotlin.android tak perlu, lihat error konfigurasi)

// NOTE: M2+: root ini hanya deklarasi plugin; tidak ada task selain yang di modul.
