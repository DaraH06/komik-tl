# M1 Spike — versi alfaf (2026-11-25, diupdate saat spike lolos)

**Tujuan spike:** 1 halaman manga Jepang → OCR benar di Android ARM64.

> Status per toolchain ini: **engine resolved, namun linker Windows SDK tidak ada** —
> akhir: `lld-link.exe` kenaku masih sebenarnya tersedia di LLVM, tapi windows SDK
> (`kernel32.lib`) belum terpasang. Saat spike, install VS Build Tools + Windows
> SDK melalui winget: `winget install -e -i Microsoft.VisualStudio.2022.BuildTools`
> (pastikan tambahkan workload `Microsoft.VisualStudio.Workload.VCTools`), lalu
> verifikasi `lld-link.exe` muncul di `C:\Program Files\Microsoft Visual Studio\...\Tools\MSVC\...\bin\Hostx64\x64\lld-link.exe`.

## Ringkasan persiapan yang dilakukan

✅ Workspace merged: root = upstream koharu-rs/koharu + crate `koharu-android` + `koharu-langs`
✅ Rust 1.100.0, target `aarch64-linux-android` (rustup)
✅ Android SDK r: platform-35, build-tools-36.1.0, platform-tools, NDK 30.0.16248370
✅ JNI Bridge: `koharu-android` (UniFFI, `engine_version()` saat M0.5)
✅ Machine host: Windows 10/11 — **Catatan khusus: Windows SDK (kernel32.lib) tidak tersedia**, jadi `cargo check` belum jalan hingga SDK masuk (install VS Build Tools + WinSDK). Perintah di bawah berlaku bila SDK ada.

## Perintah spike (untuk Windows/host yang punya Windows SDK)

```bash
cd engine/koharu
cargo check --target aarch64-linux-android   # validasi dep tree (butuh link.exe MSVC-flavored + SDK)
cargo ndk -t arm64-v8a build --release       # bangun lib engine → app/src/main/jniLibs/armeabi-v7a
```

## Rantai ketergantungan spike

1. `koharu-torch-sys` (libtorch-android): sebelum `cargo check --target aarch64-linux-android` berhasil, siapkan libtorch Android:
   - Download: `https://ossci-android.s3.amazonaws.com/libtorch/releases/v2.9.0/cpu/libtorch-cpu-2.9.0.zip` (ganti versi bila lebih baru tersedia)
   - Extract, simpan ke `engine/koharu/jniLibs/` + `engine/koharu/third_party/libtorch/` (untuk bindgen headers)
   - Atur `cargo ndk` dengan `CC`/`CXX` ke toolchain Android
2. Validasi: satu halaman manga dari `engine/koharu/testdata/spike/` → 0 error, box ≥ 3 region.

## Alternatif di sini (engine staðus k 1890)

- Install Windows SDK + MSVC: `winget install -e -i Microsoft.VisualStudio.2022.BuildTools` (meshadd `Microsoft.VisualStudio.Workload.VCTools`), verifikasi `lld-link.exe` ada, lalu `cargo check` ulang.
- Unduh + extract libtorch Android, pasang di jniLibs: **langkah selanjutnya** saat toolchain berhasil (langsung cek `cargo check` dulu).

## Hasil ketergantungan saat ini (dokumentasikan di M1)

```
cargo check --target aarch64-linux-android → fail: lld-link can't open kernel32.lib
```
Estimasi target: paling cepat 1 minggu setelah SDK masuk di sini.

## Catatan teknis Android yang baru menyambut (disimpan di roadmap)

- `cargo install cargo-ndk` — memberi `linker = "cargo-ndk-linker"`
- 16 KB page size: `-C link-arg=-Wl,-z,max-page-size=16384` (ada di `.cargo/config.toml`)
- library desktop: default OFF saat android.

## Versi tools yang dipakai saat ini (hardcoded di config.toml)

- Gradle 9.9.2, AGP 9.5.2, JDK 21, LLVM 23.1.6 (placeholder linker), sdk cmdline-tools 23.0
