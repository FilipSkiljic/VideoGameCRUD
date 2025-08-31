package org.unizd.rma.skiljic.data

import androidx.room.*

@Dao
interface VideoGameDao {
    @Query("SELECT * FROM videogame ORDER BY title")
    suspend fun getAll(): List<VideoGame>

    @Insert
    suspend fun insert(game: VideoGame): Long

    @Update
    suspend fun update(game: VideoGame)

    @Delete
    suspend fun delete(game: VideoGame)
}
