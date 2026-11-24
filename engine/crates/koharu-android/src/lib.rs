//!Android glue crate — entry point engine yang dipanggil dari Kotlin via UniFFI.
//!
//! FFI contract utama (masuk penuh di M3, squint dari spesifikasi docs/architecture.md):
//!
//! - [`engine_version`] — dipanggil di self-test di M0
//! - [`process_page`] — proses satu halaman (stateless, cancelable)
//! - [`EngineSession`] — stateful per chapter, hidup di ForegroundService
//!
//! Upstream Koharu crates (koharu-ml/pipeline/llm/…) ditambahkan di M1 — lihat
//! docs/crate-audit.md (KEEP/ADAPT/REWRITE/DROP).

/// Versi engine yang dilihat Kotlin.
#[uniffi::export]
pub fn engine_version() -> String {
    const V: &str = "0.1.0";
    V.to_owned()
}

/// Input satu halaman: RGBA8888 bytes (dari Compose, tanpa re-encode).
#[derive(Debug, Clone)]
pub struct PageInput {
    pub rgba: Vec<u8>,
    pub width: u32,
    pub height: u32,
    /// Nilai: detect, halaman masuk dari generation, atau chapter baru
    pub force: bool,
}

/// Output: hasil proses satu halaman (masuk penuh di M3).
#[derive(Debug, Clone)]
pub struct PageResult {
    pub boxes: Vec<BoxItem>,
    pub ocr: Vec<OcrLine>,
    pub translated: Vec<TranslatedLine>,
    pub inpainted: Option<Vec<u8>>,
    pub cache_key: String,
    pub stage_latency_ms: Vec<(String, u64)>,
}

#[derive(Debug, Clone)]
pub struct BoxItem {
    pub x: f32,
    pub y: f32,
    pub w: f32,
    pub h: f32,
    pub kind: String, // "dialog" | "narasi" | "sfx"
    pub confidence: f32,
    pub source_lang: Option<String>,
}

#[derive(Debug, Clone)]
pub struct OcrLine {
    pub text: String,
    pub confidence: f32,
}

#[derive(Debug, Clone)]
pub struct TranslatedLine {
    pub text: String,
    pub target_lang: String,
}

/// Proses satu halaman (stateless).
///
/// Sintaks placeholder — implementasi nyata masuk M1–M3 dari crate Koharu
/// di atas, namun antarmuka ini sudah diamati dan tidak bisa berubah sebelum
/// ada konsumen di Android.
#[allow(dead_code)]
fn process_page_placeholder(input: PageInput) -> PageResult {
    // TODO(M1): deteksi + layout; TODO(M2): OCR; TODO(M3): terjemah + inpaint
    PageResult {
        boxes: Vec::new(),
        ocr: Vec::new(),
        translated: Vec::new(),
        inpainted: None,
        cache_key: String::new(),
        stage_latency_ms: Vec::new(),
    }
}
