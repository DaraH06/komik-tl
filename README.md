# Komik AI

Aplikasi pembaca komik + **penerjemah komik lokal (offline)** untuk **Android-only**, engine pemrosesan berbahasa **Rust** turunan dari [koharu-rs/koharu](https://github.com/koharu-rs/koharu).

**Prinsip inti:** semua pemrosesan AI (deteksi teks/layout, OCR, terjemahan, inpainting, rendering) berjalan **di perangkat** — **tanpa API cloud, tanpa koneksi internet untuk memproses**. Model AI *tidak dibundel* dan *tidak didistribusikan*; model di-*side-load* oleh pengguna dan divalidasi via manifest + `sha256`.

> Repo ini = **project Komik AI**, root-nya `D:\project nyoba\komik-ai` (repo git juga di dalam folder ini).

---

## Status saat ini

- **M0 Scaffold** ✅ — workspace `android/` + `engine/`, Gradle multi-module, build `assembleDebug` hijau.
- **M1 Spike engine (Rust)** ⛔ *blocked* — `cargo check --target aarch64-linux-android` gagal `lld-link: could not open 'kernel32.lib'` karena **Windows SDK tidak terpasang**; plus URL libtorch-android **403**. (Detail: `docs/spike-m1.md`.)
- **M2 Android Foundation** ✅ —
  - **M2a** Room beneran hidup (KSP 2.3.14, 10 entity, 10 DAO, `KomikDatabase`, type-converter).
  - **M2b** **SAF import CBZ/ZIP** → unzip → `sha256` → insert `Manga/Chapter/Page`.
  - **M2c** **Jetpack Compose** (plugin + `buildFeatures.compose`) → layar **Library** + **Reader**, baca offline.
- **M3 Pipeline integration** ⬜ *menunggu M1* — `EngineSession.process_page` masih `Err("belum diimplementasi")`.
- **M4 Overlay, M5 Inpainting, M6 ONNX, M7 Multi-bahasa** ⬜ (lihat `docs/roadmap.md`).

**MVP 0.1 target:** *Import CBZ → baca → OCR teks Jepang → terjemah ke Indonesia → inpaint balon → render teks ID di atas halaman → buka ulang tanpa proses ulang.*

---

## Platform & teknologi

| Bagian | Pilihan |
|---|---|
| OS | Android 12+ (**API 31**), `arm64-v8a` saja |
| `minSdk` / `compileSdk` | 31 / 36 |
| UI | Kotlin + **Jetpack Compose** (ui 1.10.6, material3 1.4.0, activity-compose 1.13.0) |
| Build app | **AGP 9.5.2** + Kotlin **2.2.10** (built-in), **Gradle 9.9.2**, JDK 21 |
| Compose plugin | `org.jetbrains.kotlin.plugin.compose` 2.2.10 + `buildFeatures.compose = true` |
| Data | **Room 2.8.5** (sumber kebenaran), KSP **2.3.14** |
| Jobs | `androidx.work:work-runtime` 2.12.0 (M3) |
| Engine | **Rust** workspace (`engine/koharu`, nama crate Koharu dipertahankan untuk rebase) |
| Bridge | **UniFFI** 0.32 |
| `applicationId` | `dev.komik.ai` (**placeholder**, nama app belum final) |
| Vision backend | `VisionBackend { Torch, Ort }`; spike **libtorch-android** (M1) → **ONNX Runtime Mobile** rilis (M6) |
| LLM | **llama.cpp + GGUF**, output ter-constrain via **`llguidance`** (`koharu-llm`) |
| Rendering | `koharu-renderer` (HarfBuzz/harfrust + skrifa + unicode-bidi, CJK vertikal + RTL, pure Rust) |
| Linker | LLVM `lld`, **16 KB page alignment** `-C link-arg=-Wl,-z,max-page-size=16384` |

---

## Struktur repo

```
komik-ai/
├── android/                 Gradle multi-module (Kotlin + Compose)
│   ├── app/                 :app — ComponentActivity, Compose app shell
│   ├── core/
│   │   ├── database/        :core:database — Room (entity, DAO, DB, converter)
│   │   └── designsystem/    :core:designsystem — (kosong, dipesan untuk M4)
│   ├── feature/
│   │   ├── library/         :feature:library — SAF import CBZ + Library
│   │   ├── reader/          :feature:reader — (dipesan Mihon reader M2b)
│   │   └── translate/       :feature:translate — (M3+)
│   └── engine/              :engine → ../engine (placeholder)
├── engine/
│   └── koharu/              workspace Rust (upstream koharu + crate Android)
├── manifests/               Model + Language Registry (JSON + sha256)   [docs]
├── models/                  bobot AI lokal — **DI-IGNORE git, jangan commit**
├── fonts/                   font CJK subset yang ship bersama app
└── docs/                    arsitektur, keputusan, roadmap, audit, spike
```

---

## Penjelasan kode

### Android (Kotlin + Compose)

**`:app`** — `dev.komik.ai`
- `MainActivity` — `ComponentActivity`, `setContent { KomikApp() }`.
- `KomikApp` — state machine Compose (`Screen.Library` / `Screen.Reader`):
  - inisialisasi `KomikDatabase`, korutin `Dispatchers.IO` ↔ `Dispatchers.Main`,
  - **SAF** `ActivityResultContracts.OpenDocument()` → `CbzImporter.importCbz` → `rememberCoroutineScope`,
  - `LibraryScreen` (tombol *Import CBZ/ZIP* + daftar `Manga`),
  - `ReaderScreen` (`LazyColumn`, dekode `BitmapFactory.decodeFile`, `ContentScale.FillWidth`, `LaunchedEffect`),
  - `LocalPageImage`, `displayName`.
- `network_security_config.xml` + `data_extraction_rules.xml` — kebijakan offline/privasi.

**`:core:database`** — `dev.komik.ai.core.database` (Room)
- **10 entity** (tabel):
  | Entity | Isi |
  |---|---|
  | `Manga` | `id`, `title`, `source`, `uri`, `addedAt` |
  | `Chapter` | `id`, `mangaId`, `index`, `title`, `url`, `addedAt` (FK→manga, CASCADE) |
  | `Page` | `id`, `chapterId`, `pageIndex`, `width`, `height`, `checksum`, `cacheKey`, `filePath` (FK→chapter, CASCADE) |
  | `OCRResult` | `id`, `pageId`, `boxes`, `lines`, `backend` (FK→page, CASCADE) |
  | `Translation` | `id`, `pageId`, `targetLang`, `boxId`, `text`, `latencyMs`, `promptVersion` (FK→page, CASCADE) |
  | `ModelRegistry` | `id`, `kind`, `backend`, `sha256`, `bytes`, `license`, `sourceLangs`, `targetLangs`, `minRamMb`, `installedAt` |
  | `JobLog` | `jobId`, `mangaId`, `state`, `startMs`, `endMs`, `error` |
  | `ReadingProgress` | `chapterId`, `pageIndex`, `lastReadAt` (FK→chapter, CASCADE) |
  | `DeviceBenchmark` | `id`, `totalRamMb`, `freeRamMbAtCapture`, `tier`, `capturedAt` |
  | `ProcessState` | `chapterId`, `state`, `stage`, `progressDone`, `progressTotal`, `updatedAt`, `error` |
  - value type `Box`, `OcrLine` (**bukan** tabel).
- **10 DAO** (`MangaDao`, `ChapterDao`, `PageDao`, `OcrResultDao`, `TranslationDao`, `ModelRegistryDao`, `JobLogDao`, `ReadingProgressDao`, `DeviceBenchmarkDao`, `ProcessStateDao`).
- `Converters` — **TypeConverter** `List<String>`/`List<Box>`/`List<OcrLine>` ↔ JSON (`org.json`).
- `KomikDatabase` — `@Database(version=1, exportSchema=false)`, `@TypeConverters(Converters::class)`, singleton `Room.databaseBuilder`, `fallbackToDestructiveMigration()`.

**`:feature:library`** — `dev.komik.ai.feature.library`
- `CbzImporter` (`importCbz(uri, title): Result<ImportSummary>`, *blocking*, panggil di thread latar):
  - buka stream SAF; `ZipInputStream`; filter ekstensi `{jpg,jpeg,png,webp,gif,bmp}`;
  - ekstrak ke `filesDir/manga/<mangaId>/`, urut natural berdasarkan nama;
  - **`SHA-256`** per halaman; baca dimensi `BitmapFactory.Options(inJustDecodeBounds=true)`;
  - insert `Manga` + satu `Chapter` + `Page` per halaman (`page.id = "<chapterId>:<index>"`).
  - `ImportSummary(mangaId, chapterId, title, pageCount)`.

**`:feature:reader` / `:feature:translate` / `:core:designsystem` / `:engine`** — belum berisi kode (dipesan).

### Engine (Rust, `engine/koharu`)

Workspace Koharu (nama crate **tidak diubah** agar bisa di-rebase dari upstream). Crate relevan untuk Android:
- **`koharu-android`** *(ditambah)* — glue UniFFI:
  - `engine_version() -> String`;
  - `EngineSession` (`new`, `load_models`, `unload`, `process_page(PageInput) -> Result<PageResult, String>`, **M3 masih belum diimplementasi**);
  - `PageInput { rgba, width, height }`; `PageResult { boxes, ocr, translated, inpainted, cache_key, stage_latency_ms }`; `BoxItem`, `OcrLine`, `TranslatedLine`.
- **`koharu-langs`** *(ditambah)* — Model + Language Registry (manifest + validator).
- Crate inti yang dipakai: `koharu-pipeline` (detect→recognize→translate→inpaint, per-**stage** batch + eviction), `koharu-ml` (`koharu-layout-highres`, `hayai-ocr`, `lama`, `koharu-torch`), `koharu-llm` (llama.cpp + `llguidance`), `koharu-renderer`, `koharu-scene`, `koharu-runtime`, `koharu-config`, `koharu-diffusion`, `koharu-canvas`, dll. (`koharu-desktop`, `koharu-psd`, `koharu-sentry`, `koharu-agent`, `koharu-bindgen`, `koharu-torch-sys`, `koharu-leptonica`, `koharu-llama-sys`, `koharu-diffusion-sys` ada tapi tidak wajib untuk Android.)
- `.cargo/config.toml` — linker LLVM + **16 KB page size**.

### FFI contract (inti)

```
engine_version() -> String
process_page(rgba, w, h, cfg) -> PageResult      // async, cancelable, stateless
EngineSession.load_models() / unload() / memory_budget_mb()
```

**Cache key:** `sha256( image_checksum | model_id | model_version | config_version | prompt_version )`. (Lihat `docs/architecture.md`.)

**Per-stage batch + eviction (M3):** detect+OCR → evict → translate → evict → inpaint; urutan memori `Ocr < Llama < Inpaint`; free-RAM guard `free_ram > model.memory_mb × 1.4`; thermal ≥ SEVERE → turunkan tier; OOM → 1 retry 1 tier turun; device tier **Speed(1)/Balanced(2)/Quality(3)** + auto-scale dari RAM; baseline **8 GB**.

---

## Build

**Toolchain (di host ini):** Rust 1.100.0 (+`aarch64-linux-android`), JDK 21 (`C:\java\jdk-21.0.13.1+2`), Gradle 9.9.2 (`C:\dev\gradle\gradle-9.9.2`), Android SDK `C:\dev\android-sdk`, NDK 30.0.16248370, LLVM 23.1.6, VS Build Tools 2022, **Windows SDK (diperlukan untuk Rust di Windows)**.

```powershell
# Android
cd komik-ai\android
$env:JAVA_HOME = "C:\java\jdk-21.0.13.1+2"
$env:ANDROID_HOME = "C:\dev\android-sdk"
.\gradlew.bat assembleDebug        # -> android\app\build\outputs\apk\debug\app-debug.apk
```

```powershell
# Engine (M1; butuh Windows SDK + libtorch-android)
cd komik-ai\engine\koharu
cargo check -p koharu-android --target aarch64-linux-android
```

AGP 9.x: Kotlin **built-in**, `<uses-sdk>` **dilarang** di manifest, `externalNativeBuild{cargo}` **tidak ada** (Rust via `cargo ndk` terpisah).

---

## Blocker yang diketahui (nyata, bukan basa-basi)

1. **Windows SDK** tidak terpasang → `cargo check` Rust di Windows gagal `kernel32.lib/ntdll/userenv`. Pasang *Windows 10/11 SDK + VC.Tools.x86.x64* di VS Installer **sebagai Administrator**. (Android-side **tidak** butuh ini.)
2. **libtorch-android** URL resmi **403** → perlu mirror / build sendiri.
3. **M3+** menunggu M1 (Windows SDK + libtorch).

---

## Aturan keras (tidak bisa diperdebatkan)

1. **16 KB page size** `-C link-arg=-Wl,-z,max-page-size=16384`; `libtorch-android` dimuat **sebelum** `EngineSession`, bukan lazy.
2. **Model tidak di-commit** ke git; `models/` di-ignore.
3. `FLAG_SECURE` saat Screen Translator aktif di layar komik.
4. Halaman chapter **terbalik (reverse)** hasilnya harus sama.
5. **Offline murni** — tidak ada API cloud untuk memproses; model side-load + `sha256`.

---

## Privasi & lisensi

- **100% lokal/offline**; komik, hasil OCR, terjemahan tersimpan di perangkat (Room + `filesDir`). Tidak ada telemetry untuk memproses.
- **Kode** open source. **Model AI tidak dibundel/ didistribusikan** — lisensi per model tersimpan di `ModelRegistry.license` / manifest; `manga-ocr-ar` (CC BY-NC-SA, non-komersial) & model *research-only* (mis. Qwen-Image) **dikecualikan** dari set yang boleh didistribusikan.

---

## Dokumen

- [`docs/architecture.md`](docs/architecture.md) — arsitektur, FFI, cache key, eviction, aturan keras.
- [`docs/decisions.md`](docs/decisions.md) — ADR / keputusan.
- [`docs/roadmap.md`](docs/roadmap.md) — **M0–M7**.
- [`docs/crate-audit.md`](docs/crate-audit.md) — audit crate Koharu (KEEP/ADAPT/REWRITE/DROP).
- [`docs/spike-m1.md`](docs/spike-m1.md) — status M1 + blocker toolchain.
- [`docs/mihon.md`](docs/mihon.md) — keputusan Reader Mihon fork vs UX.
- [`docs/model-licenses.md`](docs/model-licenses.md) — lisensi model.
- [`manifests/README.md`](manifests/README.md) — skema manifest.

**Remote:** `https://github.com/DaraH06/komik-tl.git` · branch `main`.
