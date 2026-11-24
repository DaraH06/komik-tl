# Model + Language Registry (manifest JSON)

> NOTE: Nama crate ini baru saat spike M1 — sebelumnya plan- Blueprint had ini di `engine/crates/language-registry`. Ambil saja jalan.

Ket Entri dua jenis (schema di bawah), saat M1+ atau nanti, paling penting pas pada:
- M5: model inpainting (LaMa) di masukkan
- M6: model vision alternatif via ONNX (backend)

Schema manifest (sumber kebenaran utama, warisan AutoKD):

```json
{
  "id": "hayai-ocr-v2.7",
  "kind": "ocr",
  "license": "Apache-2.0",
  "source_languages": ["ja", "zh-Hans", "zh-Hant", "ko", "en"],
  "target_languages": [],
  "files": [
    { "name": "model.safetensors", "sha256": "abc123...", "bytes": 471859200 }
  ]
}
```

## Macam-macam kind

- `ocr` — model OCR sumber → hasil `source_languages`
- `layout` — deteksi layout/mask → `source_languages`
- `translate` — model penerjemah GGUF → `source_languages` + `target_languages`
- `inpaint` — pembersih teks visual → `source_languages`

## Validasi yang dilakukan di validator (M2)

- field `id` unik dan non-empty
- `kind` ∈ [ocr, layout, translate, inpaint]
- `license` non-empty (selalu disebutkan dalam NOTICES)
- semua `files[i].sha256` ≥ 48 hex chars dan `bytes` > 0
- untuk `translate`: `target_languages` non-empty

## Atau di hilangkan semua = hanya model yang dipasang via import eksternal (allowed — open source policy)
