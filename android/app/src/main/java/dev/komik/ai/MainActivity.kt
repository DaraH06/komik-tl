package dev.komik.ai

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Skeleton - M2+: 5 menu utama via Compose Navigation (Library / Reader /
 * Screen Translator / Model Manager / Settings).
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(android.widget.TextView(this).apply {
            text = "Komik-Ai - skeleton (Compose M2)"
        })
    }
}
