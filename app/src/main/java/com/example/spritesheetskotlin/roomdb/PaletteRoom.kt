package com.example.spritesheetskotlin.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spritesheetskotlin.database.NAME_PALETTE_TABLE

@Entity(tableName = NAME_PALETTE_TABLE)
data class PaletteRoom(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var name: String
)