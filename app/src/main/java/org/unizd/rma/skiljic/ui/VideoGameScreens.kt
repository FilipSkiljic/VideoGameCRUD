package org.unizd.rma.skiljic.ui

import android.app.DatePickerDialog
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.unizd.rma.skiljic.data.VideoGame
import java.text.SimpleDateFormat
import java.util.*

val GENRES = listOf("Action", "Adventure", "RPG", "Shooter", "Strategy", "Sports")

@Composable
fun GameListScreen(
    games: List<VideoGame>,
    onAdd: () -> Unit,
    onEdit: (VideoGame) -> Unit,
    onDelete: (VideoGame) -> Unit
) {
    Scaffold(
        floatingActionButton = { FloatingActionButton(onClick = onAdd) { Text("+") } }
    ) { inner ->
        if (games.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(inner), contentAlignment = Alignment.Center) {
                Text("No games yet. Tap + to add.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(inner).padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(games, key = { it.id }) { g ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth().clickable { onEdit(g) }
                    ) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(model = g.coverImageUri, contentDescription = null, modifier = Modifier.size(56.dp))
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(g.title, fontWeight = FontWeight.Bold)
                                Text("${g.developer}  •  ${g.genre}")
                            }
                            TextButton(onClick = { onDelete(g) }) { Text("Del") }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameEditScreen(
    initial: VideoGame?,
    onSave: (VideoGame) -> Unit,
    onCancel: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var developer by remember { mutableStateOf(initial?.developer ?: "") }
    var genre by remember { mutableStateOf(initial?.genre ?: GENRES.first()) }
    var cover by remember { mutableStateOf(initial?.coverImageUri ?: "") }
    var dateMillis by remember { mutableStateOf(initial?.releaseDate ?: System.currentTimeMillis()) }

    val ctx = LocalContext.current
    val cal = Calendar.getInstance().apply { timeInMillis = dateMillis }
    val dateFmt = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val dateDialog = DatePickerDialog(
        ctx,
        { _, y, m, d -> cal.set(y, m, d); dateMillis = cal.timeInMillis },
        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
    )

    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) cover = uri.toString()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (initial == null) "Add Game" else "Edit Game") },
                navigationIcon = { TextButton(onClick = onCancel) { Text("Back") } },
                actions = {
                    if (onDelete != null && initial != null) {
                        TextButton(onClick = onDelete) { Text("Delete") }
                    }
                }
            )
        }
    ) { inner ->
        Column(
            Modifier.fillMaxSize().padding(inner).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = developer, onValueChange = { developer = it }, label = { Text("Developer") }, singleLine = true, modifier = Modifier.fillMaxWidth())

            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(value = genre, onValueChange = {}, readOnly = true, label = { Text("Genre") }, modifier = Modifier.menuAnchor().fillMaxWidth())
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    GENRES.forEach { g -> DropdownMenuItem(text = { Text(g) }, onClick = { genre = g; expanded = false }) }
                }
            }

            OutlinedButton(onClick = { dateDialog.show() }) { Text(dateFmt.format(Date(dateMillis))) }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { pickImage.launch("image/*") }) { Text("Pick image") }
                if (cover.isNotBlank()) {
                    AsyncImage(model = cover, contentDescription = null, modifier = Modifier.size(64.dp))
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    if (title.isBlank() || developer.isBlank()) return@Button
                    onSave(
                        VideoGame(
                            id = initial?.id ?: 0,
                            title = title.trim(),
                            developer = developer.trim(),
                            genre = genre,
                            releaseDate = dateMillis,
                            coverImageUri = cover
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Save") }
        }
    }
}
