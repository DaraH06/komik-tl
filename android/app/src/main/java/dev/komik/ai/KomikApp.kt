package dev.komik.ai

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.komik.ai.core.database.KomikDatabase
import dev.komik.ai.core.database.entities.Manga
import dev.komik.ai.core.database.entities.Page
import dev.komik.ai.feature.library.CbzImporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private sealed interface Screen {
    data object Library : Screen
    data class Reader(val chapterId: String) : Screen
}

@Composable
fun KomikApp() {
    val context = LocalContext.current
    val db = remember { KomikDatabase.get(context) }
    val scope = rememberCoroutineScope()

    var manga by remember { mutableStateOf<List<Manga>>(emptyList()) }
    var screen by remember { mutableStateOf<Screen>(Screen.Library) }
    var message by remember { mutableStateOf<String?>(null) }

    fun reload() {
        scope.launch(Dispatchers.IO) {
            val list = db.mangaDao().observeAll()
            withContext(Dispatchers.Main) { manga = list }
        }
    }

    LaunchedEffect(Unit) { reload() }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri != null) {
            scope.launch(Dispatchers.IO) {
                val title = displayName(context, uri)?.substringBeforeLast('.') ?: "Komik"
                val result = CbzImporter(context, db).importCbz(uri, title)
                withContext(Dispatchers.Main) {
                    message = result.fold(
                        onSuccess = { "Import ${it.title}: ${it.pageCount} halaman" },
                        onFailure = { "Gagal import: ${it.message}" },
                    )
                }
                reload()
            }
        }
    }

    MaterialTheme {
        when (val s = screen) {
            Screen.Library -> LibraryScreen(
                manga = manga,
                message = message,
                onImport = { importLauncher.launch(arrayOf("*/*")) },
                onOpen = { m ->
                    scope.launch(Dispatchers.IO) {
                        val chapter = db.chapterDao().forManga(m.id).firstOrNull()
                        withContext(Dispatchers.Main) {
                            if (chapter != null) screen = Screen.Reader(chapter.id)
                        }
                    }
                },
            )

            is Screen.Reader -> ReaderScreen(
                db = db,
                chapterId = s.chapterId,
                onBack = { screen = Screen.Library; reload() },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryScreen(
    manga: List<Manga>,
    message: String?,
    onImport: () -> Unit,
    onOpen: (Manga) -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Komik AI") }) },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Button(onClick = onImport, modifier = Modifier.fillMaxWidth()) { Text("Import CBZ / ZIP") }
            if (message != null) {
                Spacer(Modifier.height(8.dp))
                Text(message, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(12.dp))
            if (manga.isEmpty()) {
                Text("Belum ada komik. Import CBZ dari perangkat.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(manga, key = { it.id }) { m ->
                        Card(modifier = Modifier.fillMaxWidth().clickable { onOpen(m) }) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(m.title, style = MaterialTheme.typography.titleMedium)
                                    Text(m.source, style = MaterialTheme.typography.bodySmall)
                                }
                                Text("Buka →")
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReaderScreen(db: KomikDatabase, chapterId: String, onBack: () -> Unit) {
    var pages by remember(chapterId) { mutableStateOf<List<Page>>(emptyList()) }

    LaunchedEffect(chapterId) {
        val loaded = withContext(Dispatchers.IO) { db.pageDao().forChapter(chapterId) }
        pages = loaded
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reader (${pages.size})") },
                navigationIcon = { TextButton(onClick = onBack) { Text("←") } },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(0.dp),
        ) {
            items(pages, key = { it.id }) { page -> LocalPageImage(page.filePath) }
        }
    }
}

@Composable
private fun LocalPageImage(path: String?) {
    var bmp by remember(path) { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(path) {
        bmp = if (path == null) null else withContext(Dispatchers.IO) { BitmapFactory.decodeFile(path) }
    }
    val bitmap = bmp
    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth,
        )
    } else {
        Box(modifier = Modifier.fillMaxWidth().height(240.dp))
    }
}

private fun displayName(context: Context, uri: Uri): String? =
    runCatching {
        context.contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
            if (c.moveToFirst()) c.getString(0) else null
        }
    }.getOrNull()
