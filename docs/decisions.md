# ADR — Decisions (dari blueprint yang disempurnakan)

## ADR 1: Nama crate engine tetap = Koharu (rebase-friendly)

Bukan `koharu-translator`/`koharu-mobile-api`/`koharu-ml-android`. Crate baru: `koharu-android` (bridge), `koharu-langs` (registry). Upstream crates tetap dirapihkan via git subtree.

*Alasan:* 2 minggu masa depan masih bisa meredefine nama.Kalau dirubah now, fork jadi gelap.

## ADR 2: Android MVP 0.1 punya inpainting

Spike M1 (libtorch) menguji 5 halaman: `lama`, `hayai_ocr`, `koharu_layout_highres`. Mask dari layout model — tidak perlu segmentasi terpisah.

## ADR 3: Open source — model tidak dibundel

Side-load + manifest + sha256. Manifest validasi semua fields (ui guna untuk "cek update model"). User bisa pakai manga-ocr-ar (CC BY-NC-SA) secara pribadi.

*Alasan:* platform tidak punya constraints — bebas pilih model legal, dan open-source compatibility meringankan komitmen lisensi.

## ADR 4: Tier = auto-resolve + user-ceiling

User memilihSpeed(1)/Balanced(2)/Quality(3) sebagai ceiling. Runtime auto-downgrade: free-RAM < 1.4× model → downgrade; thermal ≥ SEVERE → downgrade; OOM → 1 tier turun + retry.

`DeviceBenchmark` (3 halaman golden) di Room; tier resolver pakai angka, bukan RAM saja.

## ADR 5: API FFI — stateless page processor + stateful session

`process_page(rgba, w, h, cfg) -> PageResult` (stateless, cancelable). `EngineSession` (load/unload, per-chapter). Lihat architecture.md.

## ADR 6: Reader = fork Mihon apa adanya via AndroidView

Keputusan bisa di-refactor kapan saja (namespace Kotlin). Room schema dirancang agar `Page`/`Chapter` ~menyerupai `MangaChapterPage` Mihon. Blocking: sebelum Room migrasi pertama (M2).

## ADR 7: libtorch spike → ONNX Runtime Mobile

libtorch Android: ~hundreds MB, CPU-only, conflict libc++_shared. ONNX: lebih ringan, NNAPI. `trait VisionBackend { Torch, Ort }` memisahkan.

## ADR 8: Prompt LLM — struct

System prompt spec + glossary injection + per-balon context + honorific + retry. llguidance (sudah di koharu-llm) digunakan dari M3.

*Catatan luar dari time:* normalisasi gambar & EXIF terstandalisasi di M2.
