# Roadmap — M0..M7

> Dimulai setelah spike M1 lolos. Konteks dari blueprint yang disempurnakan di [decisions.md](decisions.md).
> Catatan aneh: MVP 0.1 menyertakan inpainting, jadi spike M1 harus menguji lama + OCR + layout — bukan hanya OCR.

## M0 — Scaffold (1 minggu)

- Workspace `engine/`, `android/`, Gradle multi-module
- Crate `koharu-android` + UniFFI skeleton, 16KB page link flag
- `cargo ndk` build arm64-v8a + Gradle wiring
- Self-test screen (tampil `engine_version()`)

## M1 — Spike libtorch (1-2 minggu)

- Load `lama` + `hayai_ocr` + `koharu_layout_highres` di Android
- Proses 5 halaman dari `testdata/spike/` (hardcoded)
- Exit: box + teks OCR benar di device

## M2 — Android Foundation (2 minggu)

- Room: Manga, Chapter, Page, ReadingProgress, OCRResult, Translation, ModelManifest, DeviceBenchmark, JobLog, ProcessState
- SAF import CBZ/ZIP/folders, Library grid, Reader (AndroidView)
- Exit: Bisa membaca CBZ offline

## M3 — Pipeline Integration (1-2 minggu)

- `EngineSession`, `process_page` via UniFFI, WorkManager per chapter
- Progress + cancel, cache key ke Room
- Exit: translate 1 chapter Jepang→Indonesian, persist, buka ulang tanpa reprocess

## M4 — Overlay Rendering (2-3 minggu)

- Teks dari Room di atas halaman (Compose overlay), auto-fit dari koharu-renderer
- Font CJK, per-script fallback
- Exit: teks ID tampil benar, bisa diedit

## M5 — Inpainting (2-4 minggu)

- Mask (dari layout) → LaMa via VMask → crop/blend, bake CBZ export
- Exit: halaman bersih + teks menyatu

## M6 — ONNX Runtime Migration (3 minggu)

- Konversi 3 vision model ke ONNX, `VisionBackend{Torch,Ort}`
- Exit: APK ringan, NNAPI aktif, same perf path

## M7 — Multi-bahasa + Screen Translator (3-4 minggu)

- RTL, vertical CJK, fallback, font pack
- MediaProjection + overlay (deguf secara terpisah, menyusul)

---

Catatan teknis: M1 = risk terbesar (proof-of-spike). Target: halaman manga Jepang dari `testdata/spike/` menghasilkan box benar. Jika spike gagal, pindahkan ke M2 (OTR path), lalu kembali di M6.
