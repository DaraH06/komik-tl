# Audit crate Koharu (repo `koharu-rs/koharu` @ main, v0.94.0)

## KEEP (porting nol)

- `koharu-leptonica` — pure Rust (`image`, `thiserror`)
- `koharu-config`
- `koharu-llama` + `koharu-llama-sys` — llama.cpp bindgen, `libloading` runtime
- `koharu-llm` — GGUF + `llguidance` + `mtmd`
- `koharu-renderer` — `harfrust`, `skrifa`, `glifo`, `unicode-bidi`, `icu_segmenter`, `jieba-rs`, `hypher` — **HarfBuzz port murni Rust untuk Android** (jangan ubah)
- `koharu-pipeline/src/stages/{detect,recognize,translate,inpaint}` — stage graph + scheduler + cancellation
- `koharu-ml/src/{image.rs, nn}` — pre/post-processing model (port-value 30%)

## ADAPT (ubah dependency, logika tetap)

- `koharu-scene` — buang `rusqlite` + `specta`; sisakan tipe + `json-patch`. Room adalah sumber kebenaran metadata.
- `koharu-runtime` — `device.rs` keep; `huggingface.rs`/`reqwest`/`hf-hub` di feature `download` OFF
- `koharu-pipeline` — buang `specta`/`axum`; `gpu.rs` diganti CPU
- `koharu-renderer` — ganti `vello_gpu`/`wgpu` dengan CPU rasterizer

## REWRITE (kecil, wajib baru)

- `koharu-android` (new) — UniFFI, `EngineSession`, `process_page`
- `koharu-langs` (new) — Model + Language Registry manifest
- `koharu-mobile-rt` (new) — `VisionBackend { Torch, Ort }`

## DROP (dari Android workspace)

- `koharu-desktop`, `koharu-canvas`, `koharu-agent`, `koharu-psd`, `koharu-sentry`, `koharu-bindgen`, `koharu-torch*`, `koharu-diffusion*`, `flux3_klein`, `qwen_image`, `koharu-translator` (tidak perlu — ada di koharu-llm + stages)

## Note penting

`koharu-torch-sys` / `koharu-llama-sys` / `koharu-diffusion-sys` pakai `libloading` — tidak perlu ubah arsitektur. Kamu tinggal provide `libtorch.so` (Android) di `jniLibs/`.
