package dev.komik.ai

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * M0 scaffold — Java-Activity + TextView (tanpa Compose). Timeline:
 * - M2: switch ke Jetpack Compose, 5 menu (Library / Reader / Screen Translator /
 *   Model Manager / Settings), pindahkan structured self-test ini ke Compose.
 * - Compose + material3 + activity-compose: dummy di [android/app/build.gradle.kts].
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(TextView(this).apply {
            text = "Komik-Ai — M0 scaffold"
        })
    }
}
