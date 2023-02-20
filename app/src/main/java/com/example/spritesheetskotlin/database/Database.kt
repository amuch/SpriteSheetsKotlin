package com.example.spritesheetskotlin.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Color
import com.example.spritesheetskotlin.palette.DBColor
import java.lang.IllegalArgumentException

const val NAME_DATABASE = "DATABASE_PIXEL_ART"
const val VERSION_DATABASE = 1

const val NOT_FOUND = "NOT_FOUND"

const val DROP_TABLE = "DROP TABLE IF EXISTS "

const val NAME_TABLE_PALETTES = "TABLE_PALETTES"
const val KEY_PRIMARY_PALETTES = "id_palette"
const val INDEX_ID_PALETTE = 0
const val NAME_PALETTES = "name_palette"
const val INDEX_NAME_PALETTE = 1
const val TABLE_PALETTES_CREATE =
    "CREATE TABLE IF NOT EXISTS $NAME_TABLE_PALETTES" +
            "($KEY_PRIMARY_PALETTES INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "$NAME_PALETTES VARCHAR(63) NOT NULL);"

const val NAME_TABLE_COLORS = "TABLE_COLORS"
const val KEY_PRIMARY_COLORS = "id_color"
const val INDEX_ID_COLOR = 0
const val NAME_COLOR = "name_color"
const val INDEX_NAME_COLOR = 1
const val INT_COLOR = "int_color"
const val INDEX_INT_COLOR = 2

const val INDEX_PALETTE_COLOR = 3
const val TABLE_COLORS_CREATE =
    "CREATE TABLE IF NOT EXISTS $NAME_TABLE_COLORS" +
            "($KEY_PRIMARY_COLORS INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "$NAME_COLOR VARCHAR(63), " +
            "$INT_COLOR INTEGER NOT NULL, " +
            "$KEY_PRIMARY_PALETTES INTEGER NOT NULL, " +
            "FOREIGN KEY ($KEY_PRIMARY_PALETTES)" +
            "REFERENCES $NAME_TABLE_PALETTES ($KEY_PRIMARY_PALETTES));"

class Database(
    context: Context?,
) : SQLiteOpenHelper(context, NAME_DATABASE, null, VERSION_DATABASE) {

    override fun onCreate(sqliteDatabase: SQLiteDatabase?) {
        createTables(sqliteDatabase)
    }

    override fun onUpgrade(sqliteDatabase: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        println("Database upgrade called.")
    }

    private fun createTables(sqliteDatabase: SQLiteDatabase?) {
        sqliteDatabase!!.execSQL(TABLE_PALETTES_CREATE)
        sqliteDatabase.execSQL(TABLE_COLORS_CREATE)
        println("Create tables called.")
    }

    private fun dropTables(sqliteDatabase: SQLiteDatabase?) {
        sqliteDatabase!!.execSQL("$DROP_TABLE $NAME_TABLE_COLORS;")
        sqliteDatabase.execSQL("$DROP_TABLE $NAME_TABLE_PALETTES;")
    }

    fun clearTables(sqliteDatabase: SQLiteDatabase?) {
        dropTables(sqliteDatabase)
        println("Dropped tables")
        createTables(sqliteDatabase)
        println("Created tables")
    }

    /*** Palette CRUD methods. ***/
    fun createPalette(name: String?): String {
        if(name!!.isEmpty()) {
            return "Palettes must have a name!"
        }

        if(!isUnique(name, paletteNames())) {
            return "$name already exists in the database!"
        }

        val contentValues = ContentValues()
        contentValues.put(NAME_PALETTES, name)

        val sqliteDatabase = this.writableDatabase
        sqliteDatabase.insert(NAME_TABLE_PALETTES, null, contentValues)
        sqliteDatabase.close()

        return "Added $name to the database."
    }

    fun readPalette(id: Int): DBPalette {
        val query = "SELECT * FROM $NAME_TABLE_PALETTES WHERE $KEY_PRIMARY_PALETTES = $id;"
        val sqliteDatabase = this.readableDatabase
        val cursor = sqliteDatabase.rawQuery(query, null)

        val dbPalette = DBPalette(0, NOT_FOUND)
        if(null != cursor && cursor.moveToFirst()) {
            val id = cursor.getInt(INDEX_ID_PALETTE)
            val name = cursor.getString(INDEX_NAME_PALETTE)
            dbPalette.id = id
            dbPalette.name = name
        }
        cursor.close()
        sqliteDatabase.close()
        return dbPalette
    }

    fun readPalette(name: String): DBPalette {
        val query = "SELECT * FROM $NAME_TABLE_PALETTES WHERE $NAME_PALETTES = '$name';"
        val sqliteDatabase = this.readableDatabase
        val cursor = sqliteDatabase.rawQuery(query, null)

        val dbPalette = DBPalette(0, NOT_FOUND)
        if(null != cursor && cursor.moveToFirst()) {
            val id = cursor.getInt(INDEX_ID_PALETTE)
            val namePalette = cursor.getString(INDEX_NAME_PALETTE)
            dbPalette.id = id
            dbPalette.name = namePalette
        }
        cursor.close()
        sqliteDatabase.close()
        return dbPalette
    }

    fun readPalettes() : ArrayList<DBPalette> {
        val query = "SELECT * FROM $NAME_TABLE_PALETTES;"
        val sqliteDatabase = this.readableDatabase
        val cursor = sqliteDatabase.rawQuery(query, null)

        val dbPalettes = ArrayList<DBPalette>(1)
        if(null != cursor && cursor.moveToFirst()) {
            while(cursor.moveToNext()) {
                val id = cursor.getInt(INDEX_ID_PALETTE)
                val name = cursor.getString(INDEX_NAME_PALETTE)
                dbPalettes.add(DBPalette(id, name))
            }
        }
        cursor.close()
        sqliteDatabase.close()
        return dbPalettes
    }

    fun updatePalette(id: Int, name: String): String {
        val dbPalette = readPalette(id)
        if(NOT_FOUND == dbPalette.name) {
            return "Couldn't find $name in the database."
        }

        if(name.isEmpty()) {
            return "Palettes must have a name!"
        }

        if(!isUnique(name, paletteNames())) {
            return "$name already exists in the database!"
        }

        val contentValues = ContentValues()
        contentValues.put(NAME_PALETTES, name)

        val sqliteDatabase = this.writableDatabase
        sqliteDatabase.update(NAME_TABLE_PALETTES, contentValues, "$KEY_PRIMARY_PALETTES = $id", null)
        sqliteDatabase.close()

        return "Set ${dbPalette.name} to $name in the database."
    }

    fun deletePalette(id: Int): String {
        val dbPalette = readPalette(id)
        if(NOT_FOUND == dbPalette.name) {
            return "Couldn't find that palette in the database."
        }

        val query = "DELETE FROM $NAME_TABLE_PALETTES WHERE $KEY_PRIMARY_PALETTES = $id"
        val sqliteDatabase = this.writableDatabase
        sqliteDatabase.execSQL(query)
        sqliteDatabase.close()

        return "Deleted ${dbPalette.name} from the database."
    }

    fun deletePalette(name: String): String {
        val dbPalettes = readPalettes()

        var id = 0
        for(i in 0 until dbPalettes.size) {
            if(name == dbPalettes[i].name) {
                id = dbPalettes[i].id
                break
            }
        }

        return deletePalette(id)
    }
    /*** Color CRUD methods. ***/
    fun createColor(name: String, color: Int, idPalette: Int): String {
        val dbPalette = readPalette(idPalette)
        if(NOT_FOUND == dbPalette.name) {
            return "Unable to find palette."
        }

        if(!isUnique(color, colorHexes(idPalette))) {
            return "$color already exists in ${dbPalette.name}!"
        }

        val contentValues = ContentValues()
        contentValues.put(NAME_COLOR, name)
        contentValues.put(INT_COLOR, color)
        contentValues.put(KEY_PRIMARY_PALETTES, idPalette)

        val sqliteDatabase = this.writableDatabase
        sqliteDatabase.insert(NAME_TABLE_PALETTES, null, contentValues)
        sqliteDatabase.close()

        return "Added $name ($color) to ${dbPalette.name} in the database."
    }

    fun createColor(dbColor: DBColor, idPalette: Int): String {
        val dbPalette = readPalette(idPalette)
        if(NOT_FOUND == dbPalette.name) {
            return "Unable to find palette."
        }

        if(!isUnique(dbColor.color, colorHexes(idPalette))) {
            return "${dbColor.color} already exists in ${dbPalette.name}!"
        }

        val contentValues = ContentValues()
        contentValues.put(NAME_COLOR, dbColor.name)
        contentValues.put(INT_COLOR, dbColor.color)
        contentValues.put(KEY_PRIMARY_PALETTES, idPalette)

        val sqliteDatabase = this.writableDatabase
        sqliteDatabase.insert(NAME_TABLE_COLORS, null, contentValues)
        sqliteDatabase.close()

        return "Added ${dbColor.color} to ${dbPalette.name} in the database."
    }

    fun readColors(idPalette: Int) : ArrayList<DBColor> {
        val query = "SELECT * FROM $NAME_TABLE_COLORS WHERE $KEY_PRIMARY_PALETTES = $idPalette;"
        val sqliteDatabase = this.readableDatabase
        val cursor = sqliteDatabase.rawQuery(query, null)

        val dbColors = ArrayList<DBColor>(1)
        if(null != cursor && cursor.moveToFirst()) {
            while(cursor.moveToNext()) {
                val id = cursor.getInt(INDEX_ID_COLOR)
                val name = cursor.getString(INDEX_NAME_COLOR)
                val color = cursor.getInt(INDEX_INT_COLOR)
                dbColors.add(DBColor(id, name, color, idPalette))
            }
        }
        cursor.close()
        sqliteDatabase.close()
        return dbColors
    }
    /*** Helpers ***/
    private fun isUnique(value: String, list: ArrayList<String> ) : Boolean {
        for(i in 0 until list.size) {
            if(value == list[i]) {
                return false
            }
        }
        return true
    }

    // Todo use Generic
    private fun isUnique(value: Int, list: ArrayList<Int> ) : Boolean {
        for(i in 0 until list.size) {
            if(value == list[i]) {
                return false
            }
        }
        return true
    }

    private fun paletteNames() : ArrayList<String> {
        val dbPalettes = readPalettes()
        val values = ArrayList<String>()
        for(i in 0 until dbPalettes.size) {
            values.add(dbPalettes[i].name)
        }
        return values
    }

    private fun colorHexes(idPalette: Int) : ArrayList<Int> {
        val dbColors = readColors(idPalette)
        val values = ArrayList<Int>()
        for(i in 0 until dbColors.size) {
            values.add(i)
        }
        return values
    }

}