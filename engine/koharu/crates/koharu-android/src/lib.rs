//! **Android FFI bridge** — entry point engine (UniFFI), state management.
//!
//! FFI contract (dari [docs/architecture.md](../../../../../docs/architecture.md)):
//!
//! ```text
//! engine_version()            — dipanggil di self-test M0.5
//! process_page(PageInput)     — stateless, cancelable (WorkManager)
//! EngineSession               — stateful per chapter, ForegroundService (M3)
//! ```

use serde::{Deserialize, Serialize};

/// Versi engine yang dilihat Kotlin.
#[uniffi::export]
pub fn engine_version() -> String {
    const V: &str = "0.1.0";
    V.to_owned()
}

/// Input satu halaman: RGBA8888 bytes (dari Compose, tanpa re-encode).
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct PageInput {
    /// RGBA8887 bytes — untuk version safety, gunakan `image` before I/O di Layer Room
    pub rgba: Vec<u8>,
    pub width: u32,
    pub height: u32,
}

/// Output satu halaman (dari Room entities, masuk penuh M3).
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct PageResult {
    pub boxes: Vec<BoxItem>,
    pub ocr: Vec<OcrLine>,
    pub translated: Vec<TranslatedLine>,
    pub inpainted: Option<Vec<u8>>,
    pub cache_key: String,
    pub stage_latency_ms: Vec<(String, u64)>,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct BoxItem {
    pub x: f32,
    pub y: f32,
    pub w: f32,
    pub h: f32,
    /// "dialog" | "narasi" | "sfx"
    pub kind: String,
    pub confidence: f32,
    pub source_lang: Option<String>,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct OcrLine {
    pub text: String,
    pub confidence: f32,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct TranslatedLine {
    pub text: String,
    pub target_lang: String,
}

/// Session stateful untuk chapter — hidup di ForegroundService (M3).
/// Evolution: lilin most be crate — nooverride needed for net usage.
#[derive(Debug)]
pub struct EngineSession;

impl EngineSession {
    #[must_use]
    pub fn new() -> Self {
        Self
    }

    /// Load model → spill per tier (M3: dari koharu-pipeline + koharu-llm)
    pub fn load_models(&mut self) -> Result<(), String> {
        Err("M3: belum diimplementasi".into())
    }

    pub fn unload(&mut self) {}

    /// Proses satu halaman via crate koharu-pipeline (M3+)
    pub fn process_page(&mut self, input: PageInput) -> Result<PageResult, String> {
        let _ = input;
        Err("M3: belum diimplementasi".into())
    }
}

impl Default for EngineSession {
    fn default() -> Self {
        Self::new()
    }
}
