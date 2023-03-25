package com.example.spritesheetskotlin.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.spritesheetskotlin.database.NAME_DATABASE
import kotlinx.coroutines.CoroutineScope

@Database(entities = [PaletteRoom::class], version = 1)
abstract class PixelArtDatabase: RoomDatabase() {
    abstract fun paletteRoomDao(): PaletteRoomDao

    companion object {
        @Volatile
        private var INSTANCE: PixelArtDatabase? = null

        fun pixelArtDatabase(context: Context, coroutineScope: CoroutineScope) : PixelArtDatabase {
            println("Creating database.")
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PixelArtDatabase::class.java,
                    NAME_DATABASE
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}