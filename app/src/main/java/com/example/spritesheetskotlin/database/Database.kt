package com.example.spritesheetskotlin.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.spritesheetskotlin.palette.DBColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val NAME_DATABASE = "DATABASE_PIXEL_ART"
const val VERSION_DATABASE = 1

const val NOT_FOUND = "NOT_FOUND"

const val DROP_TABLE = "DROP TABLE IF EXISTS "
const val CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "

const val NAME_PALETTE_TABLE = "TABLE_PALETTE"
const val PRIMARY_KEY_PALETTE = "id_palette"
const val INDEX_ID_PALETTE = 0
const val NAME_PALETTE = "name_palette"
const val INDEX_NAME_PALETTE = 1
const val CREATE_TABLE_PALETTE =
    "$CREATE_TABLE $NAME_PALETTE_TABLE" +
            "($PRIMARY_KEY_PALETTE INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "$NAME_PALETTE VARCHAR(63) NOT NULL);"

const val NAME_COLOR_TABLE = "TABLE_COLOR"
const val PRIMARY_KEY_COLOR = "id_color"
const val INDEX_ID_COLOR = 0
const val NAME_COLOR = "name_color"
const val INDEX_NAME_COLOR = 1
const val INT_COLOR = "int_color"
const val INDEX_INT_COLOR = 2

const val INDEX_PALETTE_COLOR = 3
const val CREATE_TABLE_COLOR =
    "$CREATE_TABLE $NAME_COLOR_TABLE" +
            "($PRIMARY_KEY_COLOR INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "$NAME_COLOR VARCHAR(63), " +
            "$INT_COLOR INTEGER NOT NULL, " +
            "$PRIMARY_KEY_PALETTE INTEGER NOT NULL, " +
            "FOREIGN KEY ($PRIMARY_KEY_PALETTE)" +
            "REFERENCES $NAME_PALETTE_TABLE ($PRIMARY_KEY_PALETTE));"

class Database(
    context: Context?,
) : SQLiteOpenHelper(context, NAME_DATABASE, null, VERSION_DATABASE) {
    private val coroutineScopeMain = CoroutineScope(Dispatchers.Main)

    companion object {
//        const val DROP_TABLE = "DROP TABLE IF EXISTS "
//        const val CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
//
//        const val NAME_PALETTE_TABLE = "TABLE_PALETTE"
//        const val PRIMARY_KEY_PALETTE = "id_palette"
//        const val INDEX_ID_PALETTE = 0
//        const val NAME_PALETTE = "name_palette"
//        const val INDEX_NAME_PALETTE = 1
//        const val CREATE_TABLE_PALETTE =
//            "$CREATE_TABLE $NAME_PALETTE_TABLE" +
//            "($PRIMARY_KEY_PALETTE INTEGER PRIMARY KEY AUTOINCREMENT, " +
//            "$NAME_PALETTE VARCHAR(63) NOT NULL);"
//
//        const val NAME_COLOR_TABLE = "TABLE_COLOR"
//        const val PRIMARY_KEY_COLOR = "id_color"
//        const val INDEX_ID_COLOR = 0
//        const val NAME_COLOR = "name_color"
//        const val INDEX_NAME_COLOR = 1
//        const val INT_COLOR = "int_color"
//        const val INDEX_INT_COLOR = 2
//
//        const val INDEX_PALETTE_COLOR = 3
//        const val CREATE_TABLE_COLOR =
//            "$CREATE_TABLE $NAME_COLOR_TABLE" +
//            "($PRIMARY_KEY_COLOR INTEGER PRIMARY KEY AUTOINCREMENT, " +
//            "$NAME_COLOR VARCHAR(63), " +
//            "$INT_COLOR INTEGER NOT NULL, " +
//            "$PRIMARY_KEY_PALETTE INTEGER NOT NULL, " +
//            "FOREIGN KEY ($PRIMARY_KEY_PALETTE)" +
//            "REFERENCES $NAME_PALETTE_TABLE ($PRIMARY_KEY_PALETTE));"
    }

    override fun onCreate(sqliteDatabase: SQLiteDatabase?) {
//        clearTables(sqliteDatabase)
        createTables(sqliteDatabase)
    }

    private fun getDB() : SQLiteDatabase {
        return this.writableDatabase
    }

    override fun onUpgrade(sqliteDatabase: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        println("Database upgrade called.")
    }

    private fun createTables(sqliteDatabase: SQLiteDatabase?) {
        sqliteDatabase!!.execSQL(CREATE_TABLE_PALETTE)
        sqliteDatabase.execSQL(CREATE_TABLE_COLOR)
        println("Create tables called.")
        createDefaultPalettes()
        sqliteDatabase.close()
    }

    private fun dropTables(sqliteDatabase: SQLiteDatabase?) {
        sqliteDatabase!!.execSQL("$DROP_TABLE $NAME_COLOR_TABLE;")
        sqliteDatabase.execSQL("$DROP_TABLE $NAME_PALETTE_TABLE;")
//        sqliteDatabase.close()
    }

    fun clearTables(sqliteDatabase: SQLiteDatabase?) {
        dropTables(sqliteDatabase)
        createTables(sqliteDatabase)
    }

    fun clearTables() {
        dropTables(getDB())
        createTables(getDB())
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
        contentValues.put(NAME_PALETTE, name)

        val sqliteDatabase = this.writableDatabase
        sqliteDatabase.insert(NAME_PALETTE_TABLE, null, contentValues)
        sqliteDatabase.close()

        return "Added $name to the database."
    }

    fun readPalette(id: Int): DBPalette {
        val query = "SELECT * FROM $NAME_PALETTE_TABLE WHERE $PRIMARY_KEY_PALETTE = $id;"
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
        val query = "SELECT * FROM $NAME_PALETTE_TABLE WHERE $NAME_PALETTE = '$name';"
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
        val query = "SELECT * FROM $NAME_PALETTE_TABLE;"
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
        contentValues.put(NAME_PALETTE, name)

        val sqliteDatabase = this.writableDatabase
        sqliteDatabase.update(NAME_PALETTE_TABLE, contentValues, "$PRIMARY_KEY_PALETTE = $id", null)
        sqliteDatabase.close()

        return "Set ${dbPalette.name} to $name in the database."
    }

    fun deletePalette(id: Int): String {
        val dbPalette = readPalette(id)
        if(NOT_FOUND == dbPalette.name) {
            return "Couldn't find that palette in the database."
        }

        val query = "DELETE FROM $NAME_PALETTE_TABLE WHERE $PRIMARY_KEY_PALETTE = $id"
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
    fun createColor(name: String, color: Long, idPalette: Int): String {
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
        contentValues.put(PRIMARY_KEY_PALETTE, idPalette)

        val sqliteDatabase = this.writableDatabase
        sqliteDatabase.insert(NAME_COLOR_TABLE, null, contentValues)
        sqliteDatabase.close()
        println("Added $name ($color) to ${dbPalette.name} in the database.")
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
        contentValues.put(PRIMARY_KEY_PALETTE, idPalette)

        val sqliteDatabase = this.writableDatabase
        sqliteDatabase.insert(NAME_COLOR_TABLE, null, contentValues)
        sqliteDatabase.close()

        return "Added ${dbColor.color} to ${dbPalette.name} in the database."
    }

    fun readColors(idPalette: Int) : ArrayList<DBColor> {
        val query = "SELECT * FROM $NAME_COLOR_TABLE WHERE $PRIMARY_KEY_PALETTE = $idPalette;"
        val sqliteDatabase = this.readableDatabase
        val cursor = sqliteDatabase.rawQuery(query, null)

        val dbColors = ArrayList<DBColor>(1)
        if(null != cursor && cursor.moveToFirst()) {
            while(cursor.moveToNext()) {
                val id = cursor.getInt(INDEX_ID_COLOR)
                val name = cursor.getString(INDEX_NAME_COLOR)
                val color = cursor.getLong(INDEX_INT_COLOR)
                dbColors.add(DBColor(id, name, color, idPalette))
            }
        }
        cursor.close()
        sqliteDatabase.close()
        return dbColors
    }
    /*** Helpers ***/
    fun colorIsInPalette(color: Long, idPalette: Int) : Boolean {
        return !isUnique(color, colorHexes(idPalette))
    }

    private fun isUnique(value: String, list: ArrayList<String> ) : Boolean {
        for(i in 0 until list.size) {
            if(0 == value.compareTo(list[i])) {
                return false
            }
        }
        return true
    }

    private fun isUnique(value: Long, list: ArrayList<Long> ) : Boolean {
        for(i in 0 until list.size) {
            if(0 == value.compareTo(list[i])) {
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

    private fun colorHexes(idPalette: Int) : ArrayList<Long> {
        val dbColors = readColors(idPalette)
        val values = ArrayList<Long>()
        for(i in 0 until dbColors.size) {
            values.add(dbColors[i].color)
        }
        return values
    }

    /*** Default palettes ***/
    private fun createDefaultPalettes() {
        createPaletteRainbow()
        createPaletteRainbowExtended()
        coroutineScopeMain.launch {
            delay(300)
            populatePaletteRainbow()
            populatePaletteRainbowExtended()
        }
    }

    private fun createPaletteRainbow() {
        val namePalette = "Rainbow"
        createPalette(namePalette)
    }

    private fun createPaletteRainbowExtended() {
        val namePalette = "Rainbow Extended"
        createPalette(namePalette)
    }

    private fun populatePaletteRainbow() {
        val namePalette = "Rainbow"
        val palette = readPalette(namePalette)

        createColor("Black", 0xFF000000, palette.id)
        createColor("Red", 0xFFFF0000, palette.id)
        createColor("Orange", 0xFFFFA500, palette.id)
        createColor("Yellow", 0xFFFFFF00, palette.id)
        createColor("Green", 0xFF00FF00, palette.id)
        createColor("Blue", 0xFF0000FF, palette.id)
        createColor("Purple", 0xFF800080, palette.id)
        createColor("White", 0xFFFFFFFF, palette.id)
    }

    private fun populatePaletteRainbowExtended() {
        val namePalette = "Rainbow Extended"
        val palette = readPalette(namePalette)

        createColor("Black", 0xFF000000, palette.id)
        createColor("Dark Gray", 0xFF444444, palette.id)
        createColor("Silver", 0xFF9897A9, palette.id)
        createColor("Pink", 0xFFFF69B4, palette.id)
        createColor("Red", 0xFFFF0000, palette.id)
        createColor("Maroon", 0xFF5F0000, palette.id)
        createColor("Orange", 0xFFFFA500, palette.id)
        createColor("Yellow", 0xFFFFFF00, palette.id)
        createColor("Green", 0xFF00FF00, palette.id)
        createColor("Hunter Green", 0xFF003314, palette.id)
        createColor("Blue", 0xFF0000FF, palette.id)
        createColor("Sky Blue", 0xFF87CEEB, palette.id)
        createColor("Purple", 0xFF800080, palette.id)
        createColor("Brown", 0xFF8B4513, palette.id)
        createColor("Tan", 0xFFCDA789, palette.id)
        createColor("Gold", 0xFFFFD868, palette.id)
        createColor("Light Gray", 0xFFCCCCCC, palette.id)
        createColor("White", 0xFFFFFFFF, palette.id)
    }
}