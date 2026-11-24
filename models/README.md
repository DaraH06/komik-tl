# Penyimpanan model (di-download via Model Manager, NOT dalam git)

- **Tidak pernah dicommit** — atas kutukan lisensi + ukuran + solusi (lihat `.gitignore`).
- Path: `models/<kind>/<model_id>/` (id dari manifest).
- Konvensi naming: `model.safetensors` + `manifest.json` (baca dari `manifests/`).
- Validasi import: sha256 manifest + `bytes` + sniff format (safetensors magic + hilang).

Cara masuk: **Import eksternal** (via Model Manager, pilih file) — karena open source policy. Lihat `docs/decisions.md` ADR 3.
