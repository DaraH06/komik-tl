package dev.komik.ai

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * M0 self-test. Tanpa UniFFI wire-up (M0.5): bisa menampilkan Gradle build ok,
 * lihat manually. M3+: mulai panggil [dev.komik.ai.engine.EngineApi.engineVersion]
 * dari sini — sekali proses halaman di MainActivity ready.
 */
@Composable
fun SelfTestScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        Text("Komik-Ai — M0 scaffold")
        Text("Engine version: 1 (seharusnya diisi dari UniFFI saat M0.5)")
    }
}
