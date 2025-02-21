package rpt.tool.mementobibere.utils.helpers

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.extensions.toMonth
import rpt.tool.mementobibere.utils.extensions.toYear


class SqliteHelper(val context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME, null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_VERSION = 8
        private const val DATABASE_NAME = "RptBibere"
        private const val TABLE_STATS = "stats"
        private const val TABLE_INTOOK_COUNTER = "intook_count"
        private const val TABLE_REACHED = "intake_reached"
        private const val TABLE_AVIS = "avis_day"
        private const val TABLE_CONTAINER = "container"
        private const val KEY_ID = "id"
        private const val KEY_DATE = "date"
        private const val KEY_N_DATE = "n_date"
        private const val KEY_INTOOK = "intook"
        private const val KEY_UNIT = "unit"
        private const val KEY_INTOOK_COUNT = "intook_count"
        private const val KEY_QTA = "qta"
        private const val KEY_QTA_OZ = "qta_OZ"
        private const val KEY_MONTH = "month"
        private const val KEY_YEAR = "year"
        private const val KEY_N_INTOOK = "n_intook"
        private const val KEY_N_TOTAL_INTAKE = "n_totalintake"
        private const val KEY_CONTAINER_ID = "containerID"
        private const val KEY_CONTAINER_VALUE = "containerValue"
        private const val KEY_CONTAINER_VALUE_OZ = "containerValueOZ"
        private const val KEY_IS_OPEN = "is_open"
        private const val KEY_IS_CUSTOM = "is_custom"
        private const val KEY_N_INTOOK_OZ = "n_intook_OZ"
        private const val KEY_N_TOTAL_INTAKE_OZ = "n_totalintake_OZ"
        private const val KEY_TIME = "time"
        private const val KEY_DATE_TIME = "dateTime"

    }

    override fun onCreate(db: SQLiteDatabase?) {

        addFirstTable(db)

        addNewTables(db)

        addAvisTable(db)

        addContainerTable(db)

        addValueToContainer(db)

    }

    private fun addFirstTable(db: SQLiteDatabase?) {
        val createStatTable = ("CREATE TABLE " + TABLE_STATS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_N_DATE + " TEXT,"
                + KEY_TIME + " TEXT,"
                + KEY_DATE_TIME + " TEXT,"
                + KEY_N_INTOOK + " FLOAT," + KEY_N_TOTAL_INTAKE + " FLOAT,"
                + KEY_N_INTOOK_OZ + " FLOAT,"
                + KEY_N_TOTAL_INTAKE_OZ + " FLOAT "+")")
        db?.execSQL(createStatTable)
    }

    private fun addNewTables(db: SQLiteDatabase?) {

        val createReachedTable = ("CREATE TABLE " + TABLE_REACHED + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_DATE +
                " TEXT UNIQUE," + KEY_QTA + " FLOAT," + KEY_QTA_OZ + " FLOAT)")
        db?.execSQL(createReachedTable)
    }

    private fun addAvisTable(db: SQLiteDatabase?) {
        val createAvisTable = ("CREATE TABLE " + TABLE_AVIS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_DATE + " TEXT)")
        db?.execSQL(createAvisTable)
    }

    private fun addContainerTable(db: SQLiteDatabase?) {
        val createAvisTable = ("CREATE TABLE " + TABLE_CONTAINER + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_CONTAINER_ID + " INTEGER DEfAULT 0," +
                KEY_CONTAINER_VALUE + " TEXT," +
                KEY_CONTAINER_VALUE_OZ + " TEXT," +
                KEY_IS_OPEN + " TEXT," +
                KEY_IS_CUSTOM + " INTEGER DEFAULT 0)" )
        db?.execSQL(createAvisTable)
    }

    private fun addValueToContainer(db: SQLiteDatabase?) {
        if (totalRow(TABLE_CONTAINER,db) > 0) return

        val cval = arrayOf(50, 100, 150, 200, 250, 300, 500, 600, 700, 800, 900, 1000)
        val cval2 = arrayOf(1.6907f, 3.3814f, 5.0721f, 6.7628f, 8.45351f, 10.1442f, 16.907f,
            20.2884f, 23.6698f, 27.0512f, 30.4326f, 33.814f)
        val iop = arrayOf(1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0)

        for (k in cval.indices) {
            val initialValues = ContentValues()

            initialValues.put(KEY_CONTAINER_ID, "" + (k + 1))
            initialValues.put(KEY_CONTAINER_VALUE, "" + cval[k])
            initialValues.put(KEY_CONTAINER_VALUE_OZ, "" + cval2[k])
            initialValues.put(KEY_IS_OPEN, "" + iop[k])

            insert(TABLE_CONTAINER, initialValues,db)
        }
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) =
        if(newVersion > oldVersion){
            var counter = oldVersion
            if(counter==1){
                counter += 1
                db!!.execSQL("ALTER TABLE $TABLE_STATS ADD COLUMN $KEY_UNIT VARCHAR(250)")
                db.execSQL("UPDATE $TABLE_STATS set $KEY_UNIT = \"ml\"")
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
                if(tableExists(db,TABLE_INTOOK_COUNTER)){
                    db!!.execSQL("UPDATE $TABLE_INTOOK_COUNTER set $KEY_INTOOK_COUNT = 7 WHERE $KEY_INTOOK_COUNT = 5")
                }
            }
            if (counter<6){
                counter += 1
                db!!.execSQL("ALTER TABLE $TABLE_STATS ADD COLUMN $KEY_MONTH VARCHAR(200)")
                db.execSQL("ALTER TABLE $TABLE_STATS ADD COLUMN $KEY_YEAR VARCHAR(200)")
                updateValuesOfStats(db)
                updateValuesOfCounter(db)
            }
            if (counter<7){
                counter += 1
                db!!.execSQL("ALTER TABLE $TABLE_STATS ADD COLUMN $KEY_N_INTOOK FLOAT")
                db.execSQL("ALTER TABLE $TABLE_STATS ADD COLUMN $KEY_N_TOTAL_INTAKE FLOAT")
                updateValuesOfIntake(db)
            }
            if (counter<8){
                counter += 1
                db!!.execSQL("DROP TABLE IF EXISTS $TABLE_INTOOK_COUNTER")
                db.execSQL("ALTER TABLE $TABLE_STATS ADD COLUMN $KEY_N_INTOOK_OZ FLOAT")
                db.execSQL("ALTER TABLE $TABLE_STATS ADD COLUMN $KEY_N_TOTAL_INTAKE_OZ FLOAT")
                db.execSQL("ALTER TABLE $TABLE_STATS ADD COLUMN $KEY_N_DATE TEXT")
                db.execSQL("ALTER TABLE $TABLE_STATS ADD COLUMN $KEY_TIME TEXT")
                db.execSQL("ALTER TABLE $TABLE_STATS ADD COLUMN $KEY_DATE_TIME TEXT")
                db.execSQL("ALTER TABLE $TABLE_REACHED ADD COLUMN $KEY_QTA_OZ FLOAT")
                addContainerTable(db)
                addValueToContainer(db)
                refactoryReached(db)
                updateIntake(db)
                updateReached(db)
            }
            else {
            }
        }
        else{
            db!!.execSQL("DROP TABLE IF EXISTS $TABLE_STATS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_INTOOK_COUNTER")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_REACHED")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_AVIS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTAINER")
            onCreate(db)
        }

    private fun refactoryReached(db: SQLiteDatabase) {
        val selectQuery = "SELECT * FROM $TABLE_STATS ORDER BY $KEY_DATE DESC"
        db.rawQuery(selectQuery, null).use{ it ->
            if (it.moveToFirst()) {
                for (i in 0 until it.count) {
                    val date = it.getString(1)
                    val intook = it.getString(7)
                    val today = it.getString(8)
                    val unit = it.getString(4)
                    if(intook >= today){
                        val initialValues = ContentValues()

                        initialValues.put(
                            KEY_DATE,
                            "" + date
                        )

                        initialValues.put(KEY_QTA, "" + intook)
                        initialValues.put(KEY_UNIT, unit)
                        insert("intake_reached", initialValues,db)
                    }
                    it.moveToNext()
                }
            }
        }
    }

    private fun tableExists(db: SQLiteDatabase?, tableName: String?): Boolean {
        if (tableName == null || db == null || !db.isOpen) {
            return false
        }
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?",
            arrayOf("table", tableName)
        )
        if (!cursor.moveToFirst()) {
            cursor.close()
            return false
        }
        val count = cursor.getInt(0)
        cursor.close()
        return count > 0
    }

    private fun updateIntake(db: SQLiteDatabase) {
        val selectQuery = "SELECT * FROM $TABLE_STATS ORDER BY $KEY_DATE DESC"
        db.rawQuery(selectQuery, null).use{ it ->
            if (it.moveToFirst()) {
                for (i in 0 until it.count) {
                    val date = it.getString(1)
                    val intook = it.getString(7)
                    val today = it.getString(8)
                    val unit = it.getString(4)
                    val id = it.getInt(0)
                    var intookML = 0f
                    var intookOZ = 0f
                    var todayML = 0f
                    var todayOZ = 0f
                    val time = "00:00:00"
                    val daytime = "$date $time"
                    when (unit) {
                        "ml" -> {
                            intookML = intook.toFloat()
                            todayML = today.toFloat()
                            intookOZ = AppUtils.mlToOzUK(intook.toFloat())
                            todayOZ = AppUtils.mlToOzUK(today.toFloat())
                        }
                        "0z UK" -> {
                            intookML = AppUtils.ozUKToMl(intook.toFloat())
                            todayML = AppUtils.ozUKToMl(today.toFloat())
                            intookOZ = intook.toFloat()
                            todayOZ = today.toFloat()
                        }
                        else -> {
                            intookML = AppUtils.ozUSToMl(intook.toFloat())
                            todayML = AppUtils.ozUSToMl(today.toFloat())
                            intookOZ =AppUtils.ozUSToozUK(intook.toFloat())
                            todayOZ = AppUtils.ozUSToozUK(today.toFloat())
                        }
                    }
                    db.execSQL("UPDATE $TABLE_STATS set $KEY_MONTH = \"\", $KEY_YEAR = \"\"," +
                            " $KEY_UNIT = \"\""+
                            " ,$KEY_TIME = \"$time\"," + "$KEY_N_DATE = \"$date\"" +
                            " ,$KEY_DATE_TIME = \"$daytime\"," +" $KEY_N_INTOOK = $intookML" +
                            " ,$KEY_N_INTOOK_OZ = $intookOZ" +" ,$KEY_N_TOTAL_INTAKE = $todayML" +
                            " ,$KEY_N_TOTAL_INTAKE_OZ = $todayOZ" +
                            " WHERE $KEY_ID = $id")
                    it.moveToNext()
                }
            }
        }
    }

    private fun updateReached(db: SQLiteDatabase) {
        val reached = getAllReached(false,db)
        if (reached.moveToFirst()) {
            for (i in 0 until reached.count) {
                val qta = reached.getString(2)
                val unit = reached.getString(3)
                val id = reached.getInt(0)
                var qtaML = 0f
                var qtaOZ = 0f
                when (unit) {
                    "ml" -> {
                        qtaML = qta.toFloat()
                        qtaOZ = AppUtils.mlToOzUK(qta.toFloat())
                    }
                    "0z UK" -> {
                        qtaML = AppUtils.ozUKToMl(qta.toFloat())
                        qtaOZ = qta.toFloat()
                    }
                    else -> {
                        qtaML = AppUtils.ozUSToMl(qta.toFloat())
                        qtaOZ =AppUtils.ozUSToozUK(qta.toFloat())
                    }
                }
                db.execSQL("UPDATE $TABLE_REACHED SET $KEY_UNIT = \"\""+
                        " ,$KEY_QTA = $qtaML" +
                        " ,$KEY_QTA_OZ = $qtaOZ" +
                        " WHERE $KEY_ID = $id")
                reached.moveToNext()
            }
        }
    }


    private fun updateValuesOfStats(db: SQLiteDatabase) {
        val selectQuery = "SELECT * FROM $TABLE_STATS ORDER BY $KEY_DATE DESC"
        db.rawQuery(selectQuery, null).use{ it ->
            if (it.moveToFirst()) {
                for (i in 0 until it.count) {
                    val month = it.getString(1).toMonth()
                    val year = it.getString(1).toYear()
                    val id = it.getInt(0)
                    db.execSQL("UPDATE $TABLE_STATS set $KEY_MONTH =" +
                            " $month, $KEY_YEAR = $year WHERE $KEY_ID = $id")
                    it.moveToNext()
                }
            }
        }
    }

    private fun updateValuesOfCounter(db: SQLiteDatabase) {
        getAllReached(db = db).use{ reached ->
            if (reached.moveToFirst()) {
                for (i in 0 until reached.count) {
                    val selectQuery = "SELECT $KEY_INTOOK FROM $TABLE_INTOOK_COUNTER WHERE $KEY_DATE = ?"
                    db.rawQuery(selectQuery, arrayOf(reached.getString(1))).use {
                        if (!it.moveToFirst()) {
                            val data = reached.getString(1)
                            val script ="INSERT INTO $TABLE_INTOOK_COUNTER " +
                                    "($KEY_DATE, $KEY_INTOOK, $KEY_INTOOK_COUNT) " +
                                    "VALUES (\"$data\", 6,1)"
                            db.execSQL(script)
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
                    val intook = it.getInt(2)
                    val total = it.getInt(3)
                    val id = it.getInt(0)
                    db.execSQL("UPDATE $TABLE_STATS set $KEY_INTOOK = -1, TOTAL_INTAKE = -1," +
                            "$KEY_N_INTOOK = $intook, $KEY_N_TOTAL_INTAKE = " +
                            "$total WHERE $KEY_ID = $id")
                    it.moveToNext()
                }
            }
        }
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

    private fun checkExistance(date: String, query: String): Int {
        val db = this.readableDatabase
        db.rawQuery(query, arrayOf(date)).use {
            if (it.moveToFirst()) {
                return it.count
            }
        }
        return 0
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

    private fun getAllReached(orderBy: Boolean = false, db: SQLiteDatabase? = null) : Cursor{
        var selectQuery = "SELECT * FROM $TABLE_REACHED"
        if(orderBy){
            selectQuery += " ORDER BY datetime(substr(date, 7, 4) || '-' ||" +
                    " substr(date, 4, 2) || '-' || substr(date, 1, 2) || ' ' || " +
                    "substr(date, 12, 8)) ASC"
        }
        val dbToUse = db ?: this.readableDatabase
        return dbToUse.rawQuery(selectQuery, null)
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

    private fun insert(table_name: String?, fields: ContentValues?, db: SQLiteDatabase?) {
        if (table_name != null) {
            db!!.insert(table_name, null, fields)
        }
    }

    fun insert(table_name: String?, fields: HashMap<String?, String?>,db: SQLiteDatabase?) {

        val initialValues = ContentValues()

        val myVeryOwnIterator: Iterator<*> = fields.keys.iterator()
        while (myVeryOwnIterator.hasNext()) {
            val key = myVeryOwnIterator.next() as String
            val value = fields[key]
            initialValues.put(key, value)
        }

        if (table_name != null) {
            db!!.insert(table_name, null, initialValues)
        }
    }

    @SuppressLint("Recycle")
    fun totalRow(table_name: String, db: SQLiteDatabase?): Int {
        val query = "SELECT * FROM $table_name"
        val c: Cursor = db!!.rawQuery(query, null)

        return c.count
    }

    fun totalRow(table_name: String, where_con: String,db: SQLiteDatabase?): Int {
        var query = "SELECT * FROM $table_name"

        if (!AppUtils.checkBlankData(where_con)) query += " WHERE $where_con"

        val c: Cursor = db!!.rawQuery(query, null)

        val count = c.count

        c.close()

        return count
    }

    @SuppressLint("Recycle")
    fun getdata(table_name: String, where_con: String): ArrayList<HashMap<String, String>> {
        val maplist = ArrayList<HashMap<String, String>>()

        var query = "SELECT * FROM $table_name"

        if (!AppUtils.checkBlankData(where_con)) query += " where $where_con"

        val c: Cursor = this.readableDatabase.rawQuery(query, null)

        println("SELECT QUERY : $query")

        if (c.moveToFirst()) {
            do {
                val map = HashMap<String, String>()
                for (i in 0 until c.columnCount) {
                    map[c.getColumnName(i)] = c.getString(i)
                }

                maplist.add(map)
            } while (c.moveToNext())
        }

        return maplist
    }

    @SuppressLint("Recycle")
    fun getdata(
        table_name: String,
        order_field: String,
        order_by: Int
    ): ArrayList<HashMap<String, String>> {
        val maplist = ArrayList<HashMap<String, String>>()

        var query = "SELECT * FROM $table_name"

        if (!AppUtils.checkBlankData(order_field)) {
            query += if (order_by == 0) " ORDER BY $order_field ASC"
            else " ORDER BY $order_field DESC"
        }

        val c: Cursor = this.readableDatabase.rawQuery(query, null)

        println("DESC QUERY:$query")

        if (c.moveToFirst()) {
            do {
                val map = HashMap<String, String>()
                for (i in 0 until c.columnCount) {
                    map[c.getColumnName(i)] = c.getString(i)
                }

                maplist.add(map)
            } while (c.moveToNext())
        }

        return maplist
    }

    @SuppressLint("Recycle")
    fun getdata(
        table_name: String,
        where_con: String,
        order_field: String,
        order_by: Int
    ): ArrayList<HashMap<String, String>> {
        val maplist = ArrayList<HashMap<String, String>>()

        var query = "SELECT * FROM $table_name"

        if (!AppUtils.checkBlankData(where_con)) query += " WHERE $where_con"

        if (!AppUtils.checkBlankData(order_field)) {
            query += if (order_by == 0) " ORDER BY $order_field ASC"
            else " ORDER BY $order_field DESC"
        }

        val c: Cursor = this.readableDatabase.rawQuery(query, null)

        if (c.moveToFirst()) {
            do {
                val map = HashMap<String, String>()
                for (i in 0 until c.columnCount) {
                    map[c.getColumnName(i)] = c.getString(i)
                }

                maplist.add(map)
            } while (c.moveToNext())
        }

        return maplist
    }

    @SuppressLint("Recycle")
    fun getdata(
        field_name: String,
        table_name: String,
        where_con: String
    ): ArrayList<HashMap<String, String>> {
        val maplist = ArrayList<HashMap<String, String>>()

        var query = "SELECT $field_name FROM $table_name"

        if (!AppUtils.checkBlankData(where_con)) query += " where $where_con"

        print("JOIN QUERY:$query")
        
        val c: Cursor = this.readableDatabase.rawQuery(query, null)


        if (c.moveToFirst()) {
            do {
                val map = HashMap<String, String>()
                for (i in 0 until c.columnCount) {
                    map[c.getColumnName(i)] = c.getString(i)
                }

                maplist.add(map)
            } while (c.moveToNext())
        }

        return maplist
    }

    @SuppressLint("Recycle")
    fun getdata(
        field_name: String,
        table_name: String,
        where_con: String,
        order_field: String,
        order_by: Int
    ): ArrayList<HashMap<String, String>> {
        val maplist = ArrayList<HashMap<String, String>>()

        var query = "SELECT $field_name FROM $table_name"

        if (!AppUtils.checkBlankData(where_con)) query += " where $where_con"

        if (!AppUtils.checkBlankData(order_field)) {
            query += if (order_by == 0) " ORDER BY $order_field ASC"
            else " ORDER BY $order_field DESC"
        }

        println("HISTORY JOIN QUERY:$query")

        val c: Cursor = this.readableDatabase.rawQuery(query, null)

        if (c.moveToFirst()) {
            do {
                val map = HashMap<String, String>()
                for (i in 0 until c.columnCount) {
                    map[c.getColumnName(i)] = c.getString(i)
                }

                maplist.add(map)
            } while (c.moveToNext())
        }

        return maplist
    }

    fun insert(tableName: String, initialValues: ContentValues) {
        this.writableDatabase.insert(tableName, null, initialValues)
    }

    @SuppressLint("Recycle")
    fun get(query: String): ArrayList<HashMap<String,String>> {
        val arr_data = ArrayList<HashMap<String,String>>()
        val c: Cursor = this.readableDatabase.rawQuery(query, null)
        if (c.moveToFirst()) {
            do {
                val map = HashMap<String, String>()
                for (i in 0 until c.columnCount) {
                    map[c.getColumnName(i)] = c.getString(i)
                }

                arr_data.add(map)
            } while (c.moveToNext())
        }
        return  arr_data
    }

    fun remove(tableName:String,where:String) : Int {
        val db = this.writableDatabase
        val response = db.delete(tableName,where,null)
        db.close()
        return response
    }

    fun getMax(query: String): Cursor {
        return this.readableDatabase.rawQuery(query,null)
    }

    fun update(table_name: String?, fields: HashMap<String?, String?>, where_con: String?) {

        val initialValues = ContentValues()

        val myVeryOwnIterator: Iterator<*> = fields.keys.iterator()
        while (myVeryOwnIterator.hasNext()) {
            val key = myVeryOwnIterator.next() as String
            val value = fields[key]
            initialValues.put(key, value)
        }

        if (table_name != null) {
            this.writableDatabase.update(table_name, initialValues, where_con, null)
        }
    }

    fun update(table_name: String?, fields: ContentValues?, where_con: String?) {
        if (table_name != null) {
            this.writableDatabase.update(table_name, fields, where_con, null)
        }
    }

    fun checkReachedAndDelete(dailyWaterValue: Float, date: String, unitString: String) {
        val reachedGoal = getdata("intake_reached",
            "date='$date'"
        )
        if(reachedGoal.size>0){
            val goal = if(unitString.equals("ml",true))
                reachedGoal[0]["qta"]!!.toFloat() else reachedGoal[0]["qta_OZ"]!!.toFloat()
            if(goal < dailyWaterValue){
                remove("intake_reached","date='$date'")
            }
        }
    }
}