package com.example.spritesheetskotlin.roomdb

import androidx.room.*
import com.example.spritesheetskotlin.database.NAME_PALETTE_TABLE

@Dao
interface PaletteRoomDao {
    @Insert
    suspend fun createPalette(paletteRoom: PaletteRoom)

    @Query("SELECT * FROM $NAME_PALETTE_TABLE")
    fun readPalettes(): List<PaletteRoom>

    @Update
    suspend fun updatePalette(paletteRoom: PaletteRoom)

    @Delete
    suspend fun deletePalette(paletteRoom: PaletteRoom)
}