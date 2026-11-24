# Komik-Ai (Android, Koharu-derived)

Aplikasi pembaca dan penerjemah komik **Android-only**, engine pemrosesannya adalah **Rust** turunan dari [koharu-rs/koharu](https://github.com/koharu-rs/koharu). AI lokal (OCR, penerjemahan, inpainting) berjalan di perangkat — tanpa API cloud.

> **Status: spec ditetapkan, scaffold M0 sedang disusun.**

## Konteks utama

- Platform: Android 12+ (API 31), `arm64-v8a` saja
- UI: Kotlin + Jetpack Compose
- Engine: Rust, nama crate upstream Koharu dipertahankan buat bisa di-rebase
- Bridge: UniFFI
- Distribusi: open source, model **tidak dibundel** (side-load + manifest + sha256)
- Backend vision: libtorch-android (spike M1) → ONNX Runtime Mobile (rilis M6)

## Struktur

```
engine/            # Rust workspace (koharu-* + crate Android glue)
android/           # Gradle multi-module: :app, :feature:*, :core:*
manifests/         # Model + Language Registry manifest (JSON + sha256)
docs/              # architecture, decisions, crate-audit, roadmap
models/            # Bobot AI (nanti) — JANGAN masuk git
fonts/             # Font CJK subset, ship with APK
```

## Dokumen kuncinya

- [`docs/architecture.md`](docs/architecture.md) — arsitektur & flow data
- [`docs/decisions.md`](docs/decisions.md) — ADR keputusan yang diambil
- [`docs/crate-audit.md`](docs/crate-audit.md) — audit per crate Koharu (keep/adapt/rewrite/drop)
- [`docs/roadmap.md`](docs/roadmap.md) — milestone M0–M7

## Langkah berikutnya

M0 (1 minggu): workspace engine + crate `koharu-android` + Gradle scaffold.
