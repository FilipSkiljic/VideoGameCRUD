package org.unizd.rma.skiljic.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videogame")
data class VideoGame(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val developer: String,
    val genre: String,
    val releaseDate: Long,
    val coverImageUri: String
)
