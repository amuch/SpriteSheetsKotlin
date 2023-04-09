package com.example.spritesheetskotlin.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.spritesheetskotlin.palette.DBColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

const val PALETTE_RAINBOW = "Rainbow"
const val PALETTE_DEFAULT = "Rainbow Extended"

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

const val NAME_PREFERENCE_TABLE = "TABLE_PREFERENCE"
const val PRIMARY_KEY_PREFERENCE = "id_preference"
const val INDEX_ID_PREFERENCE = 0
const val NAME_PREFERENCE = "name_preference"
const val INDEX_PREFERENCE_NAME = 1
const val INT_PREFERENCE = "int_preference"
const val INDEX_PREFERENCE_INT = 2

const val CREATE_PREFERENCE_TABLE =
    "$CREATE_TABLE $NAME_PREFERENCE_TABLE" +
            "($PRIMARY_KEY_PREFERENCE INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "$NAME_PREFERENCE VARCHAR(63), " +
            "$INT_PREFERENCE INTEGER NOT NULL);"

class Database(
    context: Context?,
) : SQLiteOpenHelper(context, NAME_DATABASE, null, VERSION_DATABASE) {


    lateinit var database: SQLiteDatabase
    var isCreating = false

    override fun onCreate(sqliteDatabase: SQLiteDatabase?) {
        isCreating = true
        database = sqliteDatabase!!
        createTables(sqliteDatabase)
    }

    override fun getWritableDatabase(): SQLiteDatabase {
        if(isCreating && database.isOpen) {
            return database
        }
        return super.getWritableDatabase()
    }

    override fun getReadableDatabase(): SQLiteDatabase {
        if(isCreating && database.isOpen) {
            return database
        }
        return super.getReadableDatabase()
    }

    override fun onUpgrade(sqliteDatabase: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        println("Database upgrade called.")
    }

    private fun createTables(sqliteDatabase: SQLiteDatabase?) {
        sqliteDatabase!!.execSQL(CREATE_TABLE_PALETTE)
        sqliteDatabase.execSQL(CREATE_TABLE_COLOR)
        sqliteDatabase.execSQL(CREATE_PREFERENCE_TABLE)


        createPaletteRainbowExtended()
        populatePaletteRainbowExtended()
        isCreating = false
    }

    private fun dropTables(sqliteDatabase: SQLiteDatabase?) {
        sqliteDatabase!!.execSQL("$DROP_TABLE $NAME_PREFERENCE_TABLE;")
        sqliteDatabase.execSQL("$DROP_TABLE $NAME_COLOR_TABLE;")
        sqliteDatabase.execSQL("$DROP_TABLE $NAME_PALETTE_TABLE;")
    }

    fun clearTables(sqliteDatabase: SQLiteDatabase?) {
        dropTables(sqliteDatabase)
        createTables(sqliteDatabase)
    }

    /*** Preference CRUD methods. ***/
    fun createPreference(preference: String?, value: Int): String {
        if(preference!!.isEmpty()) {
            println("Preferences must have a name!")
            return "Preferences must have a name!"
        }

        if(-1 != readPreference(preference)) {
            return updatePreference(preference, value)
        }

        val contentValues = ContentValues()
        contentValues.put(NAME_PREFERENCE, preference)
        contentValues.put(INT_PREFERENCE, value)
        val database = writableDatabase
        database.insert(NAME_PREFERENCE_TABLE, null, contentValues)

        println("Added $preference as $value to the database.")
        return "Added $preference as $value to the database."
    }

    fun readPreference(preference: String): Int {
        val noPreference = -1
        val query = "SELECT * FROM $NAME_PREFERENCE_TABLE WHERE $NAME_PREFERENCE = '$preference';"

        val database = readableDatabase
        val cursor = database.rawQuery(query, null)

        println("Cursor: ${cursor.getColumnName(INDEX_PREFERENCE_INT)}, ${cursor.count}")
        if(null != cursor && cursor.moveToFirst()) {
            val result = cursor.getInt(INDEX_PREFERENCE_INT)
            cursor.close()
            return result
        }

        cursor.close()
        println("No Preference")
        return noPreference
    }

    fun updatePreference(preference: String?, value: Int): String {
        if(preference!!.isEmpty()) {
            println("Preferences must have a name!")
            return "Preferences must have a name!"
        }

        val contentValues = ContentValues()
        contentValues.put(INT_PREFERENCE, value)
        val database = writableDatabase
        database.update(NAME_PREFERENCE_TABLE, contentValues, "$NAME_PREFERENCE = '$preference'", null)

        println("Updated $preference to $value in the database.")
        return "Updated $preference to $value in the database."
    }

    /*** Palette CRUD methods. ***/
    fun createPalette(name: String?): String {
        if(name!!.isEmpty()) {
            println("Palettes must have a name!")
            return "Palettes must have a name!"
        }

        if(!isUnique(name, paletteNames())) {
            println("$name already exists in the database!")
            return "$name already exists in the database!"
        }

        val contentValues = ContentValues()
        contentValues.put(NAME_PALETTE, name)

        val database = writableDatabase
        database.insert(NAME_PALETTE_TABLE, null, contentValues)
        println("Added $name to the database.")
        return "Added $name to the database."
    }

    fun readPalette(id: Int): DBPalette {
        val query = "SELECT * FROM $NAME_PALETTE_TABLE WHERE $PRIMARY_KEY_PALETTE = $id;"

        val database = readableDatabase
        val cursor = database.rawQuery(query, null)

        val dbPalette = DBPalette(0, NOT_FOUND)
        if(null != cursor && cursor.moveToFirst()) {
            val idPalette = cursor.getInt(INDEX_ID_PALETTE)
            val name = cursor.getString(INDEX_NAME_PALETTE)
            dbPalette.id = idPalette
            dbPalette.name = name
        }
        cursor.close()
        return dbPalette
    }

    fun readPalette(name: String): DBPalette {
        val query = "SELECT * FROM $NAME_PALETTE_TABLE WHERE $NAME_PALETTE = '$name';"

        val database = readableDatabase
        val cursor = database.rawQuery(query, null)

        val dbPalette = DBPalette(0, NOT_FOUND)
        if(null != cursor && cursor.moveToFirst()) {
            val id = cursor.getInt(INDEX_ID_PALETTE)
            val namePalette = cursor.getString(INDEX_NAME_PALETTE)
            dbPalette.id = id
            dbPalette.name = namePalette
        }
        cursor.close()
        return dbPalette
    }

    fun readPalettes() : ArrayList<DBPalette> {
        val query = "SELECT * FROM $NAME_PALETTE_TABLE;"
        val database = writableDatabase
        val cursor = database.rawQuery(query, null)

        val dbPalettes = ArrayList<DBPalette>(1)
        if(null != cursor && cursor.moveToFirst()) {
            while(cursor.moveToNext()) {
                val id = cursor.getInt(INDEX_ID_PALETTE)
                val name = cursor.getString(INDEX_NAME_PALETTE)
                dbPalettes.add(DBPalette(id, name))
            }
        }
        cursor.close()
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

        val database = writableDatabase
        database.update(NAME_PALETTE_TABLE, contentValues, "$PRIMARY_KEY_PALETTE = $id", null)

        return "Set ${dbPalette.name} to $name in the database."
    }

    fun deletePalette(id: Int): String {
        val dbPalette = readPalette(id)
        if(NOT_FOUND == dbPalette.name) {
            return "Couldn't find that palette in the database."
        }

        val query = "DELETE FROM $NAME_PALETTE_TABLE WHERE $PRIMARY_KEY_PALETTE = $id"
        val database = writableDatabase
        database.execSQL(query)

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
    // Create
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

        val database = writableDatabase
        database.insert(NAME_COLOR_TABLE, null, contentValues)
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

        val database = writableDatabase
        database.insert(NAME_COLOR_TABLE, null, contentValues)

        return "Added ${dbColor.color} to ${dbPalette.name} in the database."
    }

    // Read
    fun readColors(idPalette: Int) : ArrayList<DBColor> {
        val query = "SELECT * FROM $NAME_COLOR_TABLE WHERE $PRIMARY_KEY_PALETTE = $idPalette;"
        val database = writableDatabase
        val cursor = database.rawQuery(query, null)

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

        return dbColors
    }

    fun readColor(idPalette: Int, color: Long) : DBColor {
        val dummyColor = DBColor(0, NOT_FOUND, 0, 0)
        val dbPalette = readPalette(idPalette)
        if(NOT_FOUND == dbPalette.name) {
            return dummyColor
        }

        val colors = readColors(idPalette)
        for(i in 0 until colors.size) {
            println(colors[i].color)
            if(colors[i].color == color) {
                return colors[i]
            }
        }
        println("No Color $color")

        return dummyColor
    }

    // Update
    fun updateColor(idPalette: Int, colorCurrent: Long, colorUpdate: Long): String {
        val dbPalette = readPalette(idPalette)
        println(dbPalette.id)
        println(dbPalette.name)
        if(NOT_FOUND == dbPalette.name) {
            return "Unable to find palette."
        }

        val colorToUpdate = readColor(idPalette, colorCurrent)
        if(NOT_FOUND == colorToUpdate.name) {
            println("Unable to find color.")
            return "Unable to find color."
        }

        val name = colorNameAsHex(colorUpdate)
        val contentValues = ContentValues()
        contentValues.put(NAME_COLOR, name)
        contentValues.put(INT_COLOR, colorUpdate)

        val database = writableDatabase
        database.update(NAME_COLOR_TABLE, contentValues, "$PRIMARY_KEY_COLOR = ${colorToUpdate.id}", null)

        println("Set ${colorToUpdate.name} to $name in the database.")
        return "Set ${colorToUpdate.name} to $name in the database."
    }

    // Delete
    fun deleteColor(idPalette: Int, color: Long): String {
        val dbPalette = readPalette(idPalette)
        println(dbPalette.id)
        println(dbPalette.name)
        if(NOT_FOUND == dbPalette.name) {
            println("Unable to find palette.")
            return "Unable to find palette."
        }

        val colorToDelete = readColor(idPalette, color)
        if(NOT_FOUND == colorToDelete.name) {
            println("Unable to find color.")
            return "Unable to find color."
        }

        val query = "DELETE FROM $NAME_COLOR_TABLE WHERE $PRIMARY_KEY_COLOR = ${colorToDelete.id}"
        val database = writableDatabase
        database.execSQL(query)

        return "Deleted ${colorToDelete.name} from the ${dbPalette.name}."
    }

    /*** Helpers ***/
    private fun colorIsInPalette(idPalette: Int, idColor: Int) : Boolean {
        val colorList = readColors(idPalette)

        for(i in 0 until colorList.size) {
            if(idColor == colorList[i].id) {
                return true
            }
        }

        return false
    }

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
                println("Found Match: ${list[i]}")
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

    private fun colorNameAsHex(color: Long) : String {
        val red = (color shr 16) and 0xFF
        val green = (color shr 8) and 0xFF
        val blue = color and 0xFF
        val formatTemplate = "#%02X%02X%02X"
        return formatTemplate.format(red, green, blue)
    }

    /*** Default palettes ***/
//    fun createDefaultPalettes() {
//        createPaletteRainbow()
//        createPaletteRainbowExtended()
//        coroutineScopeMain.launch {
//            delay(300)
//            populatePaletteRainbow()
//            populatePaletteRainbowExtended()
//        }
//    }

    private fun createPaletteRainbow() {
        createPalette(PALETTE_RAINBOW)
    }

    private fun createPaletteRainbowExtended() {
        createPalette(PALETTE_DEFAULT)
    }

    private fun populatePaletteRainbow() {
        val palette = readPalette(PALETTE_RAINBOW)

        createColor("White", 0xFFFFFFFF, palette.id)
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
        val palette = readPalette(PALETTE_DEFAULT)

        createColor("White", -1, palette.id)
        createColor("Black", -16777216, palette.id)
        createColor("Dark Gray",  -12303292, palette.id)
        createColor("Silver", -6776919, palette.id)
        createColor("Pink", -38476, palette.id)
        createColor("Red", -65536, palette.id)
        createColor("Maroon", -10551296, palette.id)
        createColor("Orange", -23296, palette.id)
        createColor("Yellow", -256, palette.id)
        createColor("Green", -16711936, palette.id)
        createColor("Hunter Green", -16764140, palette.id)
        createColor("Blue",  -16776961, palette.id)
        createColor("Sky Blue", -7876885, palette.id)
        createColor("Purple", -8388480, palette.id)
        createColor("Brown", -7650029, palette.id)
        createColor("Tan", -3299447, palette.id)
        createColor("Gold", -10136, palette.id)
        createColor("Light Gray", -3355444, palette.id)
        createColor("White", -1, palette.id)
    }
}