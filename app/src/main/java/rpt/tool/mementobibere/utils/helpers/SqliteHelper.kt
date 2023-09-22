package rpt.tool.mementobibere.utils.helpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SqliteHelper(val context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME, null,
    DATABASE_VERSION
) {

    companion object {
        private val DATABASE_VERSION = 2
        private val DATABASE_NAME = "RptBibere"
        private val TABLE_STATS = "stats"
        private val KEY_ID = "id"
        private val KEY_DATE = "date"
        private val KEY_INTOOK = "intook"
        private val KEY_TOTAL_INTAKE = "totalintake"
        private val KEY_UNIT = "unit"
    }

    override fun onCreate(db: SQLiteDatabase?) {

        val CREATE_STATS_TABLE = ("CREATE TABLE " + TABLE_STATS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_DATE + " TEXT UNIQUE,"
                + KEY_INTOOK + " INT," + KEY_TOTAL_INTAKE + " INT," + KEY_UNIT +
                " VARCHAR(200) DEFAULT \"ml\""+")")
        db?.execSQL(CREATE_STATS_TABLE)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) =
        if(newVersion > oldVersion && newVersion == 2){
            db!!.execSQL("ALTER TABLE $TABLE_STATS ADD COLUMN $KEY_UNIT VARCHAR(250) DEFAULT \"ml\"")
            db!!.execSQL("UPDATE $TABLE_STATS set $KEY_UNIT = \"ml\"")
        }
        else{
            db!!.execSQL("DROP TABLE IF EXISTS $TABLE_STATS")
            onCreate(db)
        }



    fun addAll(date: String, intook: Int, totalintake: Float, unit: String): Long {
        if (checkExistance(date) == 0) {
            val values = ContentValues()
            values.put(KEY_DATE, date)
            values.put(KEY_INTOOK, intook)
            values.put(KEY_TOTAL_INTAKE, totalintake)
            values.put(KEY_UNIT, unit)
            val db = this.writableDatabase
            val response = db.insert(TABLE_STATS, null, values)
            db.close()
            return response
        }
        return -1
    }

    fun getIntook(date: String): Float {
        val selectQuery = "SELECT $KEY_INTOOK FROM $TABLE_STATS WHERE $KEY_DATE = ?"
        val db = this.readableDatabase
        var intook = 0f
        db.rawQuery(selectQuery, arrayOf(date)).use {
            if (it.moveToFirst()) {
                intook = it.getFloat(it.getColumnIndexOrThrow(KEY_INTOOK))
            }
        }
        return intook
    }

    fun resetIntook(date: String): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_INTOOK, 0)

        val response = db.update(TABLE_STATS, contentValues, "$KEY_DATE = ?", arrayOf(date))
        db.close()
        return response
    }

    fun addIntook(date: String, selectedOption: Float, unit: String): Int {
        val intook = getIntook(date)
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_INTOOK, intook + selectedOption)
        contentValues.put(KEY_UNIT,unit)

        val response = db.update(TABLE_STATS, contentValues, "$KEY_DATE = ?", arrayOf(date))
        db.close()
        return response
    }

    fun checkExistance(date: String): Int {
        val selectQuery = "SELECT $KEY_INTOOK FROM $TABLE_STATS WHERE $KEY_DATE = ?"
        val db = this.readableDatabase
        db.rawQuery(selectQuery, arrayOf(date)).use {
            if (it.moveToFirst()) {
                return it.count
            }
        }
        return 0
    }

    fun getTotalIntake(date: String): Cursor{
        val selectQuery = "SELECT $KEY_UNIT, $KEY_UNIT FROM $TABLE_STATS WHERE $KEY_DATE = ?"
        val db = this.readableDatabase
        return db.rawQuery(selectQuery, arrayOf(date))
    }

    fun getAllStats(): Cursor {
        val selectQuery = "SELECT * FROM $TABLE_STATS"
        val db = this.readableDatabase
        return db.rawQuery(selectQuery, null)

    }

    fun updateTotalIntake(date: String, totalintake: Float, unit: String): Int {
        val intook = getIntook(date)
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_TOTAL_INTAKE, totalintake)
        contentValues.put(KEY_UNIT, unit)

        val response = db.update(TABLE_STATS, contentValues, "$KEY_DATE = ?", arrayOf(date))
        db.close()
        return response
    }

}
