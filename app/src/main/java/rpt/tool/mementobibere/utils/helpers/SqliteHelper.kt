package rpt.tool.mementobibere.utils.helpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.lifecycle.MutableLiveData
import rpt.tool.mementobibere.utils.data.appmodel.ReachedGoal
import rpt.tool.mementobibere.utils.extensions.toCalendar
import rpt.tool.mementobibere.utils.extensions.toReachedStatsString


class SqliteHelper(val context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME, null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_VERSION = 3
        private const val DATABASE_NAME = "RptBibere"
        private const val TABLE_STATS = "stats"
        private const val TABLE_INTOOK_COUNTER = "intook_count"
        private const val TABLE_REACHED = "intake_reached"
        private const val KEY_ID = "id"
        private const val KEY_DATE = "date"
        private const val KEY_INTOOK = "intook"
        private const val KEY_TOTAL_INTAKE = "totalintake"
        private const val KEY_UNIT = "unit"
        private const val KEY_INTOOK_COUNT = "intook_count"
        private const val KEY_QTA = "qta"

    }

    override fun onCreate(db: SQLiteDatabase?) {

        val createStatTable = ("CREATE TABLE " + TABLE_STATS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_DATE + " TEXT UNIQUE,"
                + KEY_INTOOK + " INT," + KEY_TOTAL_INTAKE + " INT," + KEY_UNIT +
                " VARCHAR(200) DEFAULT \"ml\""+")")
        db?.execSQL(createStatTable)

        addNewTables(db)

    }

    private fun addNewTables(db: SQLiteDatabase?) {
        val createIntookCounterTable = ("CREATE TABLE " + TABLE_INTOOK_COUNTER + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_DATE + " TEXT,"
                + KEY_INTOOK + " INT," + KEY_INTOOK_COUNT + " INT)")
        db?.execSQL(createIntookCounterTable)

        val createReachedTable = ("CREATE TABLE " + TABLE_REACHED + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_DATE +
                " TEXT UNIQUE," + KEY_QTA + " FLOAT,"
                + KEY_UNIT +
                " VARCHAR(200))")
        db?.execSQL(createReachedTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) =
        if(newVersion > oldVersion && newVersion == 2){
            db!!.execSQL("ALTER TABLE $TABLE_STATS ADD COLUMN $KEY_UNIT VARCHAR(250) DEFAULT \"ml\"")
            db.execSQL("UPDATE $TABLE_STATS set $KEY_UNIT = \"ml\"")
        }
        else if(newVersion > oldVersion && newVersion == 3){
            addNewTables(db!!)
        }
        else{
            db!!.execSQL("DROP TABLE IF EXISTS $TABLE_STATS")
            db!!.execSQL("DROP TABLE IF EXISTS $TABLE_INTOOK_COUNTER")
            db!!.execSQL("DROP TABLE IF EXISTS $TABLE_REACHED")
            onCreate(db)
        }



    fun addAll(date: String, intook: Int, totalintake: Float, unit: String): Long {
        val selectQuery = "SELECT $KEY_INTOOK FROM $TABLE_STATS WHERE $KEY_DATE = ?"
        if (checkExistance(date,selectQuery) == 0) {
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

    private fun checkExistance(date: String, query: String): Int {
        val db = this.readableDatabase
        db.rawQuery(query, arrayOf(date)).use {
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
        val selectQuery = "SELECT * FROM $TABLE_STATS ORDER BY $KEY_DATE DESC"
        val db = this.readableDatabase
        return db.rawQuery(selectQuery, null)

    }

    fun updateTotalIntake(date: String, totalintake: Float, unit: String): Int {
        getIntook(date)
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_TOTAL_INTAKE, totalintake)
        contentValues.put(KEY_UNIT, unit)

        val response = db.update(TABLE_STATS, contentValues, "$KEY_DATE = ?", arrayOf(date))
        db.close()
        return response
    }

    fun addOrUpdateIntookCounter(date: String, selectedOption: Float, value: Int): Int {
        val intook = getIntookCounter(date,selectedOption)
        if(intook==-1){
            val values = ContentValues()
            values.put(KEY_DATE, date)
            values.put(KEY_INTOOK, selectedOption)
            values.put(KEY_INTOOK_COUNT, 1)
            val db = this.writableDatabase
            val response = db.insert(TABLE_INTOOK_COUNTER, null, values)
            db.close()
            return response.toInt()
        }
        else{
            val countNow = "SELECT $KEY_INTOOK_COUNT FROM $TABLE_INTOOK_COUNTER WHERE $KEY_DATE = ? AND $KEY_INTOOK = ?"
            var counter = 0
            val db = this.writableDatabase
            db.rawQuery(countNow, arrayOf(date,selectedOption.toString())).use {
                if (it.moveToFirst()) {
                    counter = it.getInt(it.getColumnIndexOrThrow(KEY_INTOOK_COUNT))
                }
            }
            val contentValues = ContentValues()
            contentValues.put(KEY_INTOOK_COUNT,  counter + value)

            val response = db.update(TABLE_INTOOK_COUNTER, contentValues,
                "$KEY_DATE = ? AND $KEY_INTOOK = ?",
                arrayOf(date,selectedOption.toString()))
            db.close()
            return response
        }

        return -1
    }

    fun resetIntookCounter(date: String): Int{
        val db = this.writableDatabase
        val response = db.delete(TABLE_INTOOK_COUNTER, "$KEY_DATE = ?", arrayOf(date))
        db.close()
        return response
    }

    private fun getIntookCounter(date: String, selectedOption: Float): Int {
        val selectQuery = "SELECT $KEY_INTOOK FROM $TABLE_INTOOK_COUNTER WHERE $KEY_DATE = ? AND $KEY_INTOOK = ?"
        val db = this.readableDatabase
        var intook = -1
        db.rawQuery(selectQuery, arrayOf(date,selectedOption.toString())).use {
            if (it.moveToFirst()) {
                intook = it.getInt(it.getColumnIndexOrThrow(KEY_INTOOK))
            }
        }
        return intook
    }

    fun getAllIntookStats(): Cursor {
        val selectQuery = "SELECT t1.$KEY_INTOOK, t2.total FROM $TABLE_INTOOK_COUNTER t1 " +
                "INNER JOIN (SELECT $KEY_INTOOK,SUM($KEY_INTOOK_COUNT) AS total FROM " +
                "$TABLE_INTOOK_COUNTER GROUP BY $KEY_INTOOK) t2 ON t1.$KEY_INTOOK = t2.$KEY_INTOOK" +
                " GROUP BY t1.$KEY_INTOOK"
        val db = this.readableDatabase
        return db.rawQuery(selectQuery, null)
    }

    fun getDailyIntookStats(date: String): Cursor {
        val selectQuery = "SELECT * FROM $TABLE_INTOOK_COUNTER WHERE $KEY_DATE = ?"
        val db = this.readableDatabase
        return db.rawQuery(selectQuery, arrayOf(date))

    }

    fun getMaxIntookStats(): Int {
        val selectQuery = "SELECT t1.$KEY_INTOOK, t2.total FROM $TABLE_INTOOK_COUNTER t1 " +
                "INNER JOIN (SELECT $KEY_INTOOK,SUM($KEY_INTOOK_COUNT) AS total FROM " +
                "$TABLE_INTOOK_COUNTER GROUP BY $KEY_INTOOK) t2 ON t1.$KEY_INTOOK = t2.$KEY_INTOOK ORDER BY t2.total DESC LIMIT 1"
        val db = this.readableDatabase
        var intook = -1
        db.rawQuery(selectQuery, null).use {
            if (it.moveToFirst()) {
                intook = it.getInt(it.getColumnIndexOrThrow(KEY_INTOOK))
            }
        }
        return intook
    }

    fun getMinIntookStats(): Int {
        val selectQuery = "SELECT t1.$KEY_INTOOK, t2.total FROM $TABLE_INTOOK_COUNTER t1 " +
                "INNER JOIN (SELECT $KEY_INTOOK,SUM($KEY_INTOOK_COUNT) AS total FROM " +
                "$TABLE_INTOOK_COUNTER GROUP BY $KEY_INTOOK) t2 ON t1.$KEY_INTOOK = t2.$KEY_INTOOK ORDER BY t2.total ASC LIMIT 1"
        val db = this.readableDatabase
        var intook = -1
        db.rawQuery(selectQuery, null).use {
            if (it.moveToFirst()) {
                intook = it.getInt(it.getColumnIndexOrThrow(KEY_INTOOK))
            }
        }
        return intook
    }

    fun getMaxTodayIntookStats(date: String): Int {
        val selectQuery = "SELECT $KEY_INTOOK FROM $TABLE_INTOOK_COUNTER  WHERE $KEY_DATE = ? ORDER BY $KEY_INTOOK_COUNT DESC LIMIT 1"
        val db = this.readableDatabase
        var intook = -1
        db.rawQuery(selectQuery, arrayOf(date)).use {
            if (it.moveToFirst()) {
                intook = it.getInt(it.getColumnIndexOrThrow(KEY_INTOOK))
            }
        }
        return intook
    }

    fun addReachedGoal(date: String, value: Float, unit: String) : Long {
        val selectQuery = "SELECT * FROM $TABLE_REACHED WHERE $KEY_DATE = ?"
        if (checkExistance(date,selectQuery) == 0) {
            val values = ContentValues()
            values.put(KEY_DATE, date)
            values.put(KEY_QTA, value)
            values.put(KEY_UNIT, unit)
            val db = this.writableDatabase
            val response = db.insert(TABLE_REACHED, null, values)
            db.close()
            return response
        }
        return -1
    }

    fun removeReachedGoal(date: String) : Int{
        val selectQuery = "SELECT * FROM $TABLE_REACHED WHERE $KEY_DATE = ?"
        if(checkExistance(date,selectQuery)>0){
            val deleteQuery = "DELETE FROM $TABLE_REACHED  WHERE $KEY_DATE = ?"
            val db = this.writableDatabase
            db.rawQuery(deleteQuery, arrayOf(date))
            db.close()
            return 1
        }
        return 0
    }

    fun getAllReached(): MutableLiveData<List<ReachedGoal>> {
        val selectQuery = "SELECT * FROM $TABLE_REACHED"
        val db = this.readableDatabase
        val list : ArrayList<ReachedGoal> = arrayListOf<ReachedGoal>()
        val entry = MutableLiveData<List<ReachedGoal>>()
        db.rawQuery(selectQuery, null).use { it ->
            if (it.moveToFirst()) {
                for (i in 0 until it.count) {
                    list.add(ReachedGoal(it.getString(1).toCalendar(), it.getFloat(2)
                        .toReachedStatsString(it.getString(3))))
                    it.moveToNext()
                }
                entry.postValue(list.sortedBy { it.day })
            }
        }
        return entry
    }
}
