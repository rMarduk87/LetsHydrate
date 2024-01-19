package rpt.tool.mementobibere.utils.helpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.lifecycle.MutableLiveData
import rpt.tool.mementobibere.utils.data.appmodel.ReachedGoal
import rpt.tool.mementobibere.utils.extensions.toCalendar
import rpt.tool.mementobibere.utils.extensions.toMonth
import rpt.tool.mementobibere.utils.extensions.toReachedStatsString
import rpt.tool.mementobibere.utils.extensions.toYear


class SqliteHelper(val context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME, null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_VERSION = 7
        private const val DATABASE_NAME = "RptBibere"
        private const val TABLE_STATS = "stats"
        private const val TABLE_INTOOK_COUNTER = "intook_count"
        private const val TABLE_REACHED = "intake_reached"
        private const val TABLE_AVIS = "avis_day"
        private const val KEY_ID = "id"
        private const val KEY_DATE = "date"
        private const val KEY_INTOOK = "intook"
        private const val KEY_TOTAL_INTAKE = "totalintake"
        private const val KEY_UNIT = "unit"
        private const val KEY_INTOOK_COUNT = "intook_count"
        private const val KEY_QTA = "qta"
        private const val KEY_MONTH = "month"
        private const val KEY_YEAR = "year"
        private const val KEY_N_INTOOK = "n_intook"
        private const val KEY_N_TOTAL_INTAKE = "n_totalintake"

    }

    override fun onCreate(db: SQLiteDatabase?) {

        addFirstTable(db)

        addNewTables(db)

        addAvisTable(db)

    }

    private fun addFirstTable(db: SQLiteDatabase?) {
        val createStatTable = ("CREATE TABLE " + TABLE_STATS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_DATE + " TEXT UNIQUE,"
                + KEY_INTOOK + " INT," + KEY_TOTAL_INTAKE + " INT," + KEY_UNIT +
                " VARCHAR(200),"+ KEY_MONTH +
                " VARCHAR(200)," + KEY_YEAR + " VARCHAR(200)," + KEY_N_INTOOK + " FLOAT,"
                + KEY_N_TOTAL_INTAKE + " FLOAT "+")")
        db?.execSQL(createStatTable)
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

    private fun addAvisTable(db: SQLiteDatabase?) {
        val createAvisTable = ("CREATE TABLE " + TABLE_AVIS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_DATE + " TEXT)")
        db?.execSQL(createAvisTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) =
        if(newVersion > oldVersion){
            var counter = oldVersion
            if(counter==1){
                counter += 1
                db!!.execSQL("ALTER TABLE $TABLE_STATS ADD COLUMN $KEY_UNIT VARCHAR(250)")
                db!!.execSQL("UPDATE $TABLE_STATS set $KEY_UNIT = \"ml\"")
            }
            if(oldVersion<3){
                counter += 1
                addNewTables(db!!)
            }
            if(counter<4){
                counter += 1
                addAvisTable(db!!)
            }
            if(counter<5){
                counter += 1
                db!!.execSQL("UPDATE $TABLE_INTOOK_COUNTER set $KEY_INTOOK_COUNT = 6 WHERE $KEY_INTOOK_COUNT = 5")
            }
            if (counter<6){
                counter += 1
                db!!.execSQL("ALTER TABLE $TABLE_STATS ADD COLUMN $KEY_MONTH VARCHAR(200)")
                db!!.execSQL("ALTER TABLE $TABLE_STATS ADD COLUMN $KEY_YEAR VARCHAR(200)")
                updateValuesOfStats(db!!)
                updateValuesOfCounter(db!!)
            }
            if (counter<7){
                counter += 1
                db!!.execSQL("ALTER TABLE $TABLE_STATS ADD COLUMN $KEY_N_INTOOK FLOAT")
                db!!.execSQL("ALTER TABLE $TABLE_STATS ADD COLUMN $KEY_N_TOTAL_INTAKE FLOAT")
                updateValuesOfIntake(db!!)
            } else {
            }
        }
        else{
            db!!.execSQL("DROP TABLE IF EXISTS $TABLE_STATS")
            db!!.execSQL("DROP TABLE IF EXISTS $TABLE_INTOOK_COUNTER")
            db!!.execSQL("DROP TABLE IF EXISTS $TABLE_REACHED")
            db!!.execSQL("DROP TABLE IF EXISTS $TABLE_AVIS")
            onCreate(db)
        }


    private fun updateValuesOfStats(db: SQLiteDatabase) {
        val selectQuery = "SELECT * FROM $TABLE_STATS ORDER BY $KEY_DATE DESC"
        db.rawQuery(selectQuery, null).use{ it ->
            if (it.moveToFirst()) {
                for (i in 0 until it.count) {
                    var month = it.getString(1).toMonth()
                    var year = it.getString(1).toYear()
                    var id = it.getInt(0)
                    db!!.execSQL("UPDATE $TABLE_STATS set $KEY_MONTH = $month, $KEY_YEAR = $year WHERE $KEY_ID = $id")
                    it.moveToNext()
                }
            }
        }
    }

    private fun updateValuesOfCounter(db: SQLiteDatabase) {
        getAllReached(db).use{ reached ->
            if (reached.moveToFirst()) {
                for (i in 0 until reached.count) {
                    val selectQuery = "SELECT $KEY_INTOOK FROM $TABLE_INTOOK_COUNTER WHERE $KEY_DATE = ?"
                    db.rawQuery(selectQuery, arrayOf(reached.getString(1))).use {
                        if (!it.moveToFirst()) {
                            var data = reached.getString(1)
                            var script ="INSERT INTO $TABLE_INTOOK_COUNTER ($KEY_DATE, $KEY_INTOOK, $KEY_INTOOK_COUNT) VALUES (\"$data\", 6,1)"
                            db!!.execSQL(script)
                        }
                    }
                    reached.moveToNext()
                }
            }
        }
    }

    private fun updateValuesOfIntake(db: SQLiteDatabase) {
        val selectQuery = "SELECT * FROM $TABLE_STATS ORDER BY $KEY_DATE DESC"
        db.rawQuery(selectQuery, null).use{ it ->
            if (it.moveToFirst()) {
                for (i in 0 until it.count) {
                    var intook = it.getInt(2)
                    var total = it.getInt(3)
                    var id = it.getInt(0)
                    db!!.execSQL("UPDATE $TABLE_STATS set $KEY_INTOOK = -1, $KEY_TOTAL_INTAKE = -1,$KEY_N_INTOOK = $intook, $KEY_N_TOTAL_INTAKE = $total WHERE $KEY_ID = $id")
                    it.moveToNext()
                }
            }
        }
    }
    fun addAll(date: String, intook: Float, totalintake: Float, unit: String, month: String,
               year: String): Long {
        val selectQuery = "SELECT $KEY_N_INTOOK FROM $TABLE_STATS WHERE $KEY_DATE = ?"
        if (checkExistance(date,selectQuery) == 0) {
            val values = ContentValues()
            values.put(KEY_DATE, date)
            values.put(KEY_N_INTOOK, intook)
            values.put(KEY_N_TOTAL_INTAKE, totalintake)
            values.put(KEY_UNIT, unit)
            values.put(KEY_MONTH, month)
            values.put(KEY_YEAR, year)
            val db = this.writableDatabase
            val response = db.insert(TABLE_STATS, null, values)
            db.close()
            return response
        }
        return -1
    }

    fun getAllStats(): Cursor {
        val selectQuery = "SELECT * FROM $TABLE_STATS ORDER BY $KEY_DATE DESC"
        val db = this.readableDatabase
        return db.rawQuery(selectQuery, null)
    }
    fun getIntook(date: String): Float {
        val selectQuery = "SELECT $KEY_N_INTOOK FROM $TABLE_STATS WHERE $KEY_DATE = ?"
        val db = this.readableDatabase
        var intook = 0f
        db.rawQuery(selectQuery, arrayOf(date)).use {
            if (it.moveToFirst()) {
                intook = it.getFloat(it.getColumnIndexOrThrow(KEY_N_INTOOK))
            }
        }
        return intook
    }
    fun resetIntook(date: String): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_N_INTOOK, 0)

        val response = db.update(TABLE_STATS, contentValues, "$KEY_DATE = ?", arrayOf(date))
        db.close()
        return response
    }
    fun addIntook(date: String, selectedOption: Float, unit: String, month: String, year: String): Int {
        val intook = getIntook(date)
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_N_INTOOK, intook + selectedOption)
        contentValues.put(KEY_UNIT,unit)
        return db.update(TABLE_STATS, contentValues,
            "$KEY_DATE = ? AND $KEY_MONTH = ? AND $KEY_YEAR = ?",
            arrayOf(date,month,year))
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
        val selectQuery = "SELECT $KEY_UNIT, $KEY_N_TOTAL_INTAKE FROM $TABLE_STATS WHERE $KEY_DATE = ?"
        val db = this.readableDatabase
        return db.rawQuery(selectQuery, arrayOf(date))
    }
    fun getTotalIntakeValue(date: String) : Float{
        var total = 0f
        getTotalIntake(date).use{
            if (it.moveToFirst()) {
                total = it.getFloat(1)
            }
        }
        return total
    }

    fun getAllStatsYear(year : String): Cursor {
        val selectQuery = "SELECT * FROM $TABLE_STATS WHERE $KEY_YEAR = ? ORDER BY $KEY_DATE DESC"
        val db = this.readableDatabase
        return db.rawQuery(selectQuery, arrayOf(year))
    }
    fun getAllStatsMonthly(month : String, year : String): Cursor {
        val selectQuery = "SELECT * FROM $TABLE_STATS WHERE $KEY_YEAR = ? AND $KEY_MONTH = ? ORDER BY $KEY_DATE DESC"
        val db = this.readableDatabase
        return db.rawQuery(selectQuery, arrayOf(year,month))
    }

    fun delete() : Int {
        val db = this.writableDatabase
        var response = db.delete(TABLE_STATS,"1",null)
        db.close()
        return response
    }
    fun updateTotalIntake(date: String, totalintake: Float, unit: String): Int {
        val db = this.writableDatabase
        val update = "UPDATE $TABLE_STATS SET $KEY_N_TOTAL_INTAKE = $totalintake, $KEY_UNIT = \"$unit\" WHERE $KEY_DATE = \"$date\""
        db.execSQL(update)
        db.close()
        return 1
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

            return db.update(TABLE_INTOOK_COUNTER, contentValues,
                "$KEY_DATE = ? AND $KEY_INTOOK = ?",
                arrayOf(date,selectedOption.toString()))
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

    fun getSumOfDailyIntookCounter(date: String): Cursor {
        val selectQuery = "SELECT $KEY_DATE, SUM($KEY_INTOOK_COUNT) as count " +
                "FROM $TABLE_INTOOK_COUNTER WHERE $KEY_DATE = ? " +
                "GROUP BY $KEY_DATE HAVING SUM($KEY_INTOOK_COUNT) > 0"
        val db = this.readableDatabase
        return db.rawQuery(selectQuery, arrayOf(date))
    }

    fun getDailyIntookStats(date: String): Cursor {
        val selectQuery = "SELECT * FROM $TABLE_INTOOK_COUNTER WHERE $KEY_DATE = ?"
        val db = this.readableDatabase
        return db.rawQuery(selectQuery, arrayOf(date))
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
    fun getMinTodayIntookStats(date: String): Int {
        val selectQuery = "SELECT $KEY_INTOOK FROM $TABLE_INTOOK_COUNTER  WHERE $KEY_DATE = ? ORDER BY $KEY_INTOOK_COUNT ASC LIMIT 1"
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

    private fun getAllReached(db: SQLiteDatabase? = null) : Cursor{
        val selectQuery = "SELECT * FROM $TABLE_REACHED"
        val dbToUse = db ?: this.readableDatabase
        return dbToUse.rawQuery(selectQuery, null)
    }

    fun getAllReachedForStats(): MutableLiveData<List<ReachedGoal>> {
        val list : ArrayList<ReachedGoal> = arrayListOf<ReachedGoal>()
        val entry = MutableLiveData<List<ReachedGoal>>()
        getAllReached().use { it ->
            if (it.moveToFirst()) {
                for (i in 0 until it.count) {
                    list.add(ReachedGoal(it.getString(1).toCalendar(), it.getFloat(2)
                        .toReachedStatsString(),it.getString(3)))
                    it.moveToNext()
                }
                entry.postValue(list.sortedBy { it.day })
            }
        }
        return entry
    }
    fun addAvis(date: String) : Long {
        val selectQuery = "SELECT * FROM $TABLE_AVIS WHERE $KEY_DATE = ?"
        if (checkExistance(date,selectQuery) == 0) {
            val values = ContentValues()
            values.put(KEY_DATE, date)
            val db = this.writableDatabase
            val response = db.insert(TABLE_AVIS, null, values)
            db.close()
            return response
        }
        return -1
    }
    fun getAvisDay(date: String): Boolean {
        val selectQuery = "SELECT * FROM $TABLE_AVIS WHERE $KEY_DATE = ?"
        val db = this.readableDatabase
        var day = false
        db.rawQuery(selectQuery, arrayOf(date)).use {
            if (it.moveToFirst()) {
                day = true
            }
        }
        return day
    }
}