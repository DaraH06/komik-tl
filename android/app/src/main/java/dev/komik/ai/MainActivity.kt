package dev.komik.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier

/**
 * M0 scaffold. Self-test engine: panggil [dev.komik.ai.engine.engineVersion].
 *
 * M2+: pindahkan ke Compose navigation dengan 5 menu (Library / Reader /
 * Screen Translator / Model Manager / Settings).
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                SelfTestScreen(Modifier.padding(padding))
            }
        }
    }
}
