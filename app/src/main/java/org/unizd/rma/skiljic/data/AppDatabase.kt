package org.unizd.rma.skiljic.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [VideoGame::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun videoGameDao(): VideoGameDao
}
