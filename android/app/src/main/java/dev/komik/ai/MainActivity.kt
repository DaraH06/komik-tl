package dev.komik.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

/** Komik AI — app shell. M2: Library + Reader (tanpa AI). AI pipeline = M3. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { KomikApp() }
    }
}
