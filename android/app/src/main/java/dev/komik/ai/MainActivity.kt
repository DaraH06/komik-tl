package dev.komik.ai

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * M0 skeleton — M2: ganti dengan 5 menu (Library / Reader / Screen Translator / Model Manager / Settings)
 * melalui Compose Navigation (compose deps bukan di sini saat ini — lihat build.gradle.kts comments)
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(android.widget.TextView(this).apply {
            text = "Komik-Ai — M0 skeleton"
        })
    }
}
