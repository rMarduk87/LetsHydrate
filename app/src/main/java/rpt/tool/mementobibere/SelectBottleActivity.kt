package rpt.tool.mementobibere

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import rpt.tool.mementobibere.databinding.ActivitySelectBottleBinding
import rpt.tool.mementobibere.ui.widget.NewAppWidget
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.data.appmodel.Container
import rpt.tool.mementobibere.utils.helpers.AlertHelper
import rpt.tool.mementobibere.utils.helpers.SqliteHelper
import rpt.tool.mementobibere.utils.managers.SharedPreferencesManager


class SelectBottleActivity : AppCompatActivity() {

    lateinit var binding: ActivitySelectBottleBinding
    var containerArrayList: MutableList<Container> = ArrayList()
    var selected_pos: Int = 0
    var drink_water: Float = 0f
    var sqliteHelper: SqliteHelper? = null
    var alertHelper: AlertHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectBottleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setLayout(RelativeLayout.LayoutParams.FILL_PARENT,
            RelativeLayout.LayoutParams.FILL_PARENT);

        sqliteHelper = SqliteHelper(this)
        alertHelper = AlertHelper(this)
        
        if(SharedPreferencesManager.totalIntake==0f)
        {
            AppUtils.DAILY_WATER_VALUE=2500f
        }
        else
        {
            AppUtils.DAILY_WATER_VALUE=SharedPreferencesManager.totalIntake
        }

        if(AppUtils.checkBlankData(""+SharedPreferencesManager.unitString))
        {
            AppUtils.WATER_UNIT_VALUE="ml"
        }
        else
        {
            AppUtils.WATER_UNIT_VALUE=SharedPreferencesManager.unitString
        }
        body()
    }

    private fun saveDefaultContainer() {
        if (!SharedPreferencesManager.hideWelcomeScreen) {
            SharedPreferencesManager.personWeightUnit = true
            SharedPreferencesManager.personWeight = "80"
            SharedPreferencesManager.userName = ""
            intent = Intent(this, InitUserInfoActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            var addRecord = true

            var str = ""
            str =
                if (AppUtils.WATER_UNIT_VALUE.equals("ml",true))
                    getString(R.string.str_you_should_not_drink_more_then_target)
                    .replace("$1", "8000 ml")
                else getString(R.string.str_you_should_not_drink_more_then_target)
                    .replace("$1", "270 fl oz")

            val arr_data: ArrayList<HashMap<String, String>> = sqliteHelper!!.getdata(
                "stats",
                ("n_date ='" + AppUtils.getCurrentDate(AppUtils.DATE_FORMAT)) + "'"
            )

            drink_water = 0f
            for (k in arr_data.indices) {
                if (AppUtils.WATER_UNIT_VALUE.equals("ml",true))
                        drink_water += ("" + arr_data[k]["n_intook"]).toFloat()
                else drink_water += ("" + arr_data[k]["n_intook_OZ"]).toFloat()
            }

            if (AppUtils.WATER_UNIT_VALUE.equals("ml",true)
                && drink_water > 8000
            ) {
                alertHelper!!.customAlert(str)
                addRecord = false
            } else if (!(AppUtils.WATER_UNIT_VALUE.equals("ml",true))
                && drink_water > 270
            ) {
                alertHelper!!.customAlert(str)
                addRecord = false
            }


            var count_drink_after_add_current_water = drink_water

            if (AppUtils.WATER_UNIT_VALUE.equals("ml",true)) 
                count_drink_after_add_current_water += 
                    ("" + containerArrayList[selected_pos].containerValue).toFloat()
            else if (!(AppUtils.WATER_UNIT_VALUE.equals("ml",true))) 
                count_drink_after_add_current_water += 
                    ("" + containerArrayList[selected_pos].containerValueOZ).toFloat()

            if (AppUtils.WATER_UNIT_VALUE.equals("ml",true)
                && count_drink_after_add_current_water > 8000
            ) {
                if (drink_water >= 8000) alertHelper!!.customAlert(str)
                else if (AppUtils.DAILY_WATER_VALUE < (8000 - 
                            ("" + containerArrayList[selected_pos].containerValue).toFloat()))
                    alertHelper!!.customAlert(
                    str
                )
            } else if (!(AppUtils.WATER_UNIT_VALUE.equals("ml",true))
                && count_drink_after_add_current_water > 270
            ) {
                if (drink_water >= 270) alertHelper!!.customAlert(str)
                else if (AppUtils.DAILY_WATER_VALUE < (270 - 
                            ("" + containerArrayList[selected_pos].containerValueOZ).toFloat()))
                    alertHelper!!.customAlert(
                    str
                )
            }

            if (drink_water == 8000f && AppUtils.WATER_UNIT_VALUE.
                equals("ml",true)) {
                addRecord = false
            } else if (drink_water == 270f && 
                !AppUtils.WATER_UNIT_VALUE.equals("ml",true)) {
                addRecord = false
            }

            if (addRecord) {
                val initialValues = ContentValues()

                initialValues.put(
                    "n_intook",
                    "" + containerArrayList[selected_pos].containerValue
                )
                initialValues.put(
                    "n_intook_OZ",
                    "" + containerArrayList[selected_pos].containerValueOZ
                )
                initialValues.put("unit", "" + SharedPreferencesManager.unitString)
                initialValues.put("n_date", "" + AppUtils.getCurrentDate("dd-MM-yyyy"))
                initialValues.put("time", "" + AppUtils.getCurrentTime(true))
                initialValues.put("dateTime", "" +
                        AppUtils.getCurrentDate("dd-MM-yyyy HH:mm:ss"))

                if (AppUtils.WATER_UNIT_VALUE.equals("ml",true)) {
                    initialValues.put("n_totalintake", "" + AppUtils.DAILY_WATER_VALUE)
                    initialValues.put(
                        "n_totalintake_OZ",
                        "" + AppUtils.mlToOzUS(AppUtils.DAILY_WATER_VALUE)
                    )
                } else {
                    initialValues.put(
                        "n_totalintake",
                        "" + AppUtils.ozUSToMl(AppUtils.DAILY_WATER_VALUE)
                    )
                    initialValues.put("n_totalintake_OZ", "" + AppUtils.DAILY_WATER_VALUE)
                }

                sqliteHelper!!.insert("stats", initialValues)
            }


            //==============================
            val intent = Intent(this, NewAppWidget::class.java)
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
            val ids = AppWidgetManager.getInstance(this).getAppWidgetIds(
                ComponentName(
                    this,
                    NewAppWidget::class.java
                )
            )
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            this.sendBroadcast(intent)

            AppClose.exitApplication(this)
        }
    }

    private fun body() {
        var selected_container_id = "1"

        selected_container_id = if (SharedPreferencesManager.selectedContainer == 0) "1"
        else "" + SharedPreferencesManager.selectedContainer

        val arr_container: ArrayList<HashMap<String, String>> =
            sqliteHelper!!.getdata("container", "is_custom", 1)

        for (k in arr_container.indices) {
            val container = Container()
            container.containerId = arr_container[k]["containerID"]
            container.containerValue = arr_container[k]["containerValue"]
            container.containerValueOZ = arr_container[k]["containerValueOZ"]
            container.isOpen(
                arr_container[k]["is_open"].equals(
                        "1",
                        ignoreCase = true
                    )
            )

            container.isSelected(
                selected_container_id.equals(
                        arr_container[k]["containerID"],
                        ignoreCase = true
                    )
            )
            if (container.isSelected) selected_pos = k //+1

            containerArrayList.add(container)
        }

        saveDefaultContainer()
    }
}