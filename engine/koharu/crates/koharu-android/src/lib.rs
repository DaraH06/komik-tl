//! **Android FFI bridge** — entry point engine (UniFFI), state management.
//!
//! FFI contract (dari [docs/architecture.md](../../../../../docs/architecture.md)):
//!
//! ```text
//! engine_version()            — dipanggil di self-test M0.5
//! process_page(PageInput)     — stateless, cancelable (WorkManager)
//! EngineSession               — stateful per chapter, ForegroundService (M3)
//! ```
//!
//! M3: permukaan FFI sudah diekspos; implementasi model/pipeline menunggu
//! libtorch-android yang valid (lihat docs/spike-m1.md).

uniffi::setup_scaffolding!();

use serde::{Deserialize, Serialize};

/// Versi engine yang dilihat Kotlin.
#[uniffi::export]
pub fn engine_version() -> String {
    const V: &str = "0.1.0";
    V.to_owned()
}

/// Input satu halaman: RGBA8888 bytes (dari Compose, tanpa re-encode).
#[derive(Debug, Clone, Serialize, Deserialize, uniffi::Record)]
pub struct PageInput {
    /// RGBA8888 bytes — untuk version safety, gunakan `image` sebelum I/O di Layer Room
    pub rgba: Vec<u8>,
    pub width: u32,
    pub height: u32,
}

/// Waktu tiap tahap pipeline, dalam milidetik.
#[derive(Debug, Clone, Serialize, Deserialize, uniffi::Record)]
pub struct StageLatency {
    pub stage: String,
    pub ms: u64,
}

/// Output satu halaman (dari Room entities, masuk penuh M3).
#[derive(Debug, Clone, Serialize, Deserialize, uniffi::Record)]
pub struct PageResult {
    pub boxes: Vec<BoxItem>,
    pub ocr: Vec<OcrLine>,
    pub translated: Vec<TranslatedLine>,
    pub inpainted: Option<Vec<u8>>,
    pub cache_key: String,
    pub stage_latency_ms: Vec<StageLatency>,
}

#[derive(Debug, Clone, Serialize, Deserialize, uniffi::Record)]
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

#[derive(Debug, Clone, Serialize, Deserialize, uniffi::Record)]
pub struct OcrLine {
    pub text: String,
    pub confidence: f32,
}

#[derive(Debug, Clone, Serialize, Deserialize, uniffi::Record)]
pub struct TranslatedLine {
    pub text: String,
    pub target_lang: String,
}

/// Deskripsi kegagalan pemrosesan yang dikirim lintas FFI.
#[derive(Debug, Clone, uniffi::Error)]
pub enum EngineError {
    NotImplemented { stage: String },
    ModelMissing { id: String },
}

impl std::fmt::Display for EngineError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            Self::NotImplemented { stage } => write!(f, "{stage}: belum diimplementasi"),
            Self::ModelMissing { id } => write!(f, "model tidak ditemukan: {id}"),
        }
    }
}

impl std::error::Error for EngineError {}

/// Session stateful untuk chapter — hidup di ForegroundService (M3).
#[derive(Debug, Default, uniffi::Object)]
pub struct EngineSession {}

#[uniffi::export]
impl EngineSession {
    #[uniffi::constructor]
    #[must_use]
    pub fn new() -> Self {
        Self::default()
    }

    /// Anggaran memori (MB) untuk tier saat ini; M3: dari DeviceBenchmark.
    #[must_use]
    pub fn memory_budget_mb(&self) -> u32 {
        const TIER_BASELINE_MB: u32 = 8 * 1024 / 4;
        TIER_BASELINE_MB
    }

    /// Load model → spill per tier (M3: dari koharu-pipeline + koharu-llm)
    pub fn load_models(&self) -> Result<(), EngineError> {
        Err(EngineError::NotImplemented {
            stage: "load_models".to_owned(),
        })
    }

    pub fn unload(&self) {}

    /// Proses satu halaman via crate koharu-pipeline (M3+)
    pub fn process_page(&self, input: PageInput) -> Result<PageResult, EngineError> {
        let _ = input;
        Err(EngineError::NotImplemented {
            stage: "process_page".to_owned(),
        })
    }
}
