# Arsitektur

## Garis besar (dari blueprint yang disempurnakan)

```
┌───────────────────────────────────────────────────────────┐
│  Android App (Kotlin + Jetpack Compose)                   │
│  Library │ Reader │ Screen Translator │ Model Manager    │
│  Room = sumber kebenaran metadata (Page, OCRResult,       │
│  Translation, ModelManifest, DeviceBenchmark, JobLog)     │
└──────────────────────────────────┬────────────────────────┘
                                   │ UniFFI (async + Canceller)
                                   ▼
┌───────────────────────────────────────────────────────────┐
│  Rust Engine (workspace `engine/`, nama crate Koharu)     │
│                                                            │
│  koharu-pipeline  │ stages: detect → recognize → translate│
│                   → inpaint  (per-STAGE BATCH, bukan per-  │
│                     page)                                   │
│  koharu-ml        │ koharu-layout-highres, hayai-ocr, lama │
│  koharu-llm       │ llama.cpp + GGUF, output via llguidance│
│  koharu-renderer  │ HarfBuzz(harfrust)/skrifa/glifo/unici  │
│                     code-bidi + vertical CJK + RTL (pure    │
│                     Rust — upstream sudah punya)           │
│  koharu-android   │ EngineSession, process_page, spawner   │
│  koharu-langs     │ Model + Language Registry (manifest    │
│                     JSON, validator)                        │
│  koharu-mobile-rt │ trait VisionBackend { Torch, Ort } —   │
│                     libtorch spike, ONNX release           │
└───────────────────────────────────────────────────────────┘
        Local models dir: Android/data/dev.<app>/files/models
```

## API FFI (contract kunci)

```rust
// Stateless — bisa dipanggil dari coroutine/WorkManager kapan saja
fn engine_version() -> String
fn process_page(rgba: ByteBufferDirect, w: u32, h: u32, cfg: PageConfig)
              -> PageResult        // async, cancelable via Canceller

// Stateful — masa aktif = chapter/job satu, hidup di ForegroundService
struct EngineSession;
fn EngineSession.load_models()
fn EngineSession.unload()
fn EngineSession.memory_budget_mb() -> u32
```

`PageConfig` (dari Room-normalized): model ids + tier + tile_px + prompt + glossary.
`PageResult`:

```json
{
  "cache_key": "hash(gambar+model_id+model_version+config_version+prompt_version)",
  "boxes": [ { x, y, w, h, kind: "dialog"|"narasi"|"sfx", conf, source_lang } ],
  "ocr":   [ { line, text, box_id, conf, backend } ],
  "translation": [ { box_id, text, provider, latency_ms } ],
  "inpainted": Option<byte[]>,
  "stats": { stage_latency_ms: {...} }
}
```

## Cache key — format eksplisit

```
sha256(
  image_checksum          // dari Room Page.checksum
  "|" model_id
  "|" model_version       // dari ModelManifest
  "|" config_version      // global config rev
  "|" prompt_version      // system prompt LLM rev
)
```

Kalau kamu ubah prompt tanpa bump `prompt_version`, cache lama tetap dipakai — itu bug yang sangat meletakkan aja.

## Eviction model (masuk M3)

- Rank memory: `Ocr` (terkecil) < `Llama` < `Inpaint`
- Chapter flow: detect+OCR → evict all (LLM in) → translate → evict all (LaMa in)
- Free-RAM guard: sebelum load model, cek `free_ram > model.memory_mb × 1.4`, else auto-downgrade tier
- Thermal status ≥ SEVERE → downgrade; OOM → retry satu tier turun sekali

## Test spec (masuk M2-M4)

Golden reference: `testdata/golden/<lang>/<page>.{jpg,json}` — dari koharu DESKTOP pada 20 halaman manga. Bagian `json` = expected `PageResult` (kotak, teks, panjang per baris). Regresi = diff > threshold.

## Aturan keras (aturan ini tidak bisa diperdebatkan — lihat "aturan keras" di git log nanti)

1. **16 KB page size** — gunakan `-C link-arg=-Wl,-z,max-page-size=16384` (untuk target Android 15+); lihat `engine/.cargo/config.toml`.
2. **Model tidak di-commit ke git** — `models/` di-ignore (lihat `.gitignore`).
3. **Halaman chapter kebalikan (reverse)** harus tetap hasil yang sama.
4. **FLAG_SECURE** saat Screen Translator aktif di layar komik (komik lokal banyak aplikasi yang set flag ini).
5. **16 KB alignment + libc++_shared.so** — libtorch-android HARUS dimuat sebelum app memanggil `EngineSession`, bukan pada lazily-init pertama (crash di startup paling buruk).

---

*Catatan: dimulai setelah spike M1 lolos — spike menguji 5 halaman koharu-ml yang ada (lama, hayai_ocr, koharu_layout_highres) di Android ARM64.*
