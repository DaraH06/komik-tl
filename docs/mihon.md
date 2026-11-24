# Reader: Mihon (fork) atau pola UX saja — keputusan untuk M2

**Kontekst:** Mihon = Apache-2.0, Gradle multi-module, `source-api` extension system. Paling berguna saat M2.

| Pilihan | Tendangan: memicu window

## Opsi

1. **Fork Mihon (hal ini)** — kita ambil Mihon sebagai app shell. Reader aslinya sudah lengkap (zoom/pan/RTL/webtoon preload). Butuh adaptasi dari model `Page/Chapter` Mihon → model kita (Room) sekali di M2.
2. **Pola UX saja** — adopsi layout/theme/viewer patterns, tapi build reader sendiri dari Compose View system.
3. **Hybrid** — Mihon sebagai app shell, reader dibuat ulang di M2.

## Rekomendasi

**Opsi 1 (Fork Mihon) untuk migrasi Cepat** — lebih cepat 2 minggu vs M2. Pertama putar Mihon source, lalu adaptasi M2 di blok v2.

Untuk opsi 1: hal-hal yang diambil:

- Modul: `app`, `core:database`, `data`, `domain`, `source-api`, `source-local`, `presentation-core`, `presentation-widget`
- Adaptasi umum (ganti ke kita): 
  - library `SourceManager` → directory (SAF import CBZ/ZIP),
  - `source-local` (manga reader lokal) → kita pakai sebagai basis impor,
  - `ReaderViewModel/Activity` kita: panggil `EngineSession.process_page(PageInput)` di background service,
  - Opsi remote source: HANYA target lokal — hidupi source dari path impor saja (CSP), sebagaimana putusan offline murni di blueprint.

Selanjutnya M2: bahwa reader diproduksi ulang di M2b dengan Kotlin UI Compose (Compose Views sudah bisa ditambahkan).

## Close: Pertama M2 (week ~3, MMM3 de afternoon).

Ref: https://github.com/mihonapp/mihon/blob/main/app/src/main/java/eu/kanade/tachiyomi/ui/reader/viewer/
