package org.unizd.rma.skiljic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import org.unizd.rma.skiljic.data.VideoGame
import org.unizd.rma.skiljic.ui.GameEditScreen
import org.unizd.rma.skiljic.ui.GameListScreen
import org.unizd.rma.skiljic.ui.theme.VideoGameCRUDTheme
import org.unizd.rma.skiljic.vm.VideoGameViewModel

class MainActivity : ComponentActivity() {

    private val vm: VideoGameViewModel by viewModels {
        VideoGameViewModel.Factory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.load()

        setContent {
            VideoGameCRUDTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    var editing by remember { mutableStateOf<VideoGame?>(null) }
                    val games by vm.games.collectAsState()

                    if (editing == null) {
                        GameListScreen(
                            games = games,
                            onAdd = { editing = VideoGame(0, "", "", "Action", System.currentTimeMillis(), "") },
                            onEdit = { editing = it },
                            onDelete = { vm.delete(it) }
                        )
                    } else {
                        GameEditScreen(
                            initial = editing,
                            onSave = { g ->
                                if (g.id == 0L) vm.add(g) else vm.update(g)
                                editing = null
                            },
                            onCancel = { editing = null },
                            onDelete = if (editing?.id != 0L) {
                                { vm.delete(editing!!); editing = null }
                            } else null
                        )
                    }
                }
            }
        }
    }
}
