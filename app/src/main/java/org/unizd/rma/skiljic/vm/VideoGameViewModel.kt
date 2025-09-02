package org.unizd.rma.skiljic.vm


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.unizd.rma.skiljic.data.AppDatabase
import org.unizd.rma.skiljic.data.VideoGame
import org.unizd.rma.skiljic.data.VideoGameDao


class VideoGameViewModel(private val dao: VideoGameDao) : ViewModel() {

    private val _games = MutableStateFlow<List<VideoGame>>(emptyList())
    val games: StateFlow<List<VideoGame>> = _games.asStateFlow()

    fun load() = viewModelScope.launch(Dispatchers.IO) {
        _games.value = dao.getAll()
    }

    fun add(game: VideoGame) = viewModelScope.launch(Dispatchers.IO) {
        dao.insert(game)
        _games.value = dao.getAll()
    }

    fun update(game: VideoGame) = viewModelScope.launch(Dispatchers.IO) {
        dao.update(game)
        _games.value = dao.getAll()
    }

    fun delete(game: VideoGame) = viewModelScope.launch(Dispatchers.IO) {
        dao.delete(game)
        _games.value = dao.getAll()
    }

    companion object {
        fun Factory(appContext: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val db = Room.databaseBuilder(
                        appContext.applicationContext,
                        AppDatabase::class.java,
                        "games.db"
                    ).build()
                    return VideoGameViewModel(db.videoGameDao()) as T
                }
            }
    }
}
