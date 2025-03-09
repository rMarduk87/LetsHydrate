package rpt.tool.mementobibere.ui.statistics.reachedGoal

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.MainActivity
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.FragmentReachedGoalBinding
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.data.appmodel.ReachedGoal
import rpt.tool.mementobibere.utils.helpers.AlertHelper
import rpt.tool.mementobibere.utils.helpers.SqliteHelper
import rpt.tool.mementobibere.utils.log.i
import rpt.tool.mementobibere.utils.managers.SharedPreferencesManager
import rpt.tool.mementobibere.utils.view.adapters.ReachedGoalAdapter
import java.text.SimpleDateFormat
import java.util.Calendar


class ReachedGoalFragment  : BaseFragment<FragmentReachedGoalBinding>(
    FragmentReachedGoalBinding::inflate) {

    var reachedGoals: ArrayList<ReachedGoal> = ArrayList()
    var adapter: ReachedGoalAdapter? = null
    var isLoading: Boolean = true
    var perPage: Int = 20
    var page: Int = 0
    var beforeLoad: Int = 0
    var afterLoad: Int = 0
    var sqliteHelper: SqliteHelper? = null
    var alertHelper: AlertHelper? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sqliteHelper = SqliteHelper(requireContext())
        alertHelper = AlertHelper(requireContext())

        body()
    }

    private fun body() {
        binding.include1.lblToolbarTitle.text = requireContext()
            .getString(R.string.str_daily_goal_reached)
        binding.include1.leftIconBlock.setOnClickListener { finish() }

        binding.include1.rightIconBlock.setOnClickListener { view ->
            showMenu(view)
        }


        binding.reachedRecyclerView.isNestedScrollingEnabled = false

        adapter = ReachedGoalAdapter(requireActivity(), reachedGoals, object : ReachedGoalAdapter.CallBack {
            override fun onClickSelect(reachedGoal: ReachedGoal, position: Int) {
            }
        })

        binding.reachedRecyclerView.setLayoutManager(
            LinearLayoutManager(
                requireActivity(),
                LinearLayoutManager.VERTICAL,
                false
            )
        )

        binding.reachedRecyclerView.setAdapter(adapter)

        load_reached_goal(false)

        binding.nestedScrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                val TAG = "nested_sync"
                if (scrollY > oldScrollY) {
                    i(TAG, "Scroll DOWN")
                }
                if (scrollY < oldScrollY) {
                    i(TAG, "Scroll UP")
                }

                if (scrollY == 0) {
                    i(TAG, "TOP SCROLL")
                }
                if (scrollY == (v.getChildAt(0).measuredHeight - v.measuredHeight)) {
                    i(TAG, "BOTTOM SCROLL")

                    if (isLoading) {
                        isLoading = false

                        page++

                        load_reached_goal(true)
                    }
                }
            })
    }

    private fun showMenu(v: View) {
        val popup: PopupMenu = PopupMenu(requireContext(), v)
        popup.setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener,
            PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(menuItem: MenuItem): Boolean {
                when (menuItem.getItemId()) {
                    R.id.add_item -> {
                        showAddOldReachedDialog()
                        return true
                    }

                    else -> return false
                }
            }
        })
        popup.inflate(R.menu.add_new_reached_menu)
        popup.show()
    }

    @SuppressLint("InflateParams")
    private fun showAddOldReachedDialog() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.drawable_background_tra)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)


        val view: View = LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_add_old_reached,
            null, false)

        val img_cancel = view.findViewById<ImageView>(R.id.img_cancel)
        val img_data = view.findViewById<ImageView>(R.id.img_data)

        val lbl_reached_day = view.findViewById<AppCompatTextView>(R.id.lbl_reached_day)

        val txt_quantity = view.findViewById<AppCompatEditText>(R.id.txt_quantity)

        val btn_add = view.findViewById<RelativeLayout>(R.id.btn_add)

        val rdo_ml = view.findViewById<RadioButton>(R.id.rdo_ml)
        val rdo_floz = view.findViewById<RadioButton>(R.id.rdo_floz)

        var isMl = SharedPreferencesManager.personWeightUnit

        if (SharedPreferencesManager.personWeightUnit) {
            rdo_ml.isChecked = true
            rdo_ml.isClickable = false
            rdo_floz.isClickable = true
        } else {
            rdo_floz.isChecked = true
            rdo_ml.isClickable = true
            rdo_floz.isClickable = false
        }

        rdo_ml.setOnClickListener {
            isMl = true
            rdo_ml.isClickable = false
            rdo_floz.isClickable = true
        }

        rdo_floz.setOnClickListener {
            isMl = false
            rdo_ml.isClickable = true
            rdo_floz.isClickable = false
        }

        img_data.setOnClickListener {
            openCalendarPicker(lbl_reached_day)
        }

        btn_add.setOnClickListener {
            val quantity = txt_quantity.text.toString().toFloat()
            if(isMl){
                if(quantity < 2500f){
                    alertHelper!!.Show_Error_Dialog(requireContext().
                    getString(R.string.str_reached_ml_error))
                    return@setOnClickListener
                }
            }
            else{
                if(quantity < 84.54f){
                    alertHelper!!.Show_Error_Dialog(requireContext().
                    getString(R.string.str_reached_floz_error))
                    return@setOnClickListener
                }
            }

            if(checkIfReachedGoalExists(lbl_reached_day.text.toString())){
                alertHelper!!.Show_Error_Dialog(requireContext().
                getString(R.string.str_just_reached))
                return@setOnClickListener
            }
            addNewReached(quantity,lbl_reached_day.text.toString(),isMl)
        }

        img_cancel.setOnClickListener { dialog.dismiss() }

        dialog.setContentView(view)

        dialog.show()
    }

    private fun checkIfReachedGoalExists(date: String): Boolean{
        val arr_data2: ArrayList<HashMap<String, String>> =
            sqliteHelper!!.getdata("intake_reached",
                "date ='$date'"
            )
        return arr_data2.size > 0
    }

    private fun addNewReached(quantity: Float, date: String, isMl: Boolean) {
        val initialValues = ContentValues()

        initialValues.put(
            "date",
            "" + date
        )

        if (isMl) {
            initialValues.put("qta", "" + quantity)
            initialValues.put(
                "qta_OZ",
                "" + AppUtils.mlToOzUS(quantity)
            )
        } else {
            initialValues.put(
                "qta",
                "" + AppUtils.ozUSToMl(quantity)
            )
            initialValues.put("qta_OZ", "" + quantity)
        }

        sqliteHelper!!.insert("intake_reached", initialValues)
    }

    @SuppressLint("SimpleDateFormat")
    private fun openCalendarPicker(lblReachedDay: AppCompatTextView?) {
        val calendar = Calendar.getInstance()

        val mDatePicker = DatePickerDialog(
            requireContext(),
            { _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd-MM-yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat)
                val avis = sdf.format(calendar.time)
                lblReachedDay!!.text = avis
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        mDatePicker.setTitle("")
        mDatePicker.show()
    }

    private fun finish() {
        startActivity(Intent(requireActivity(), MainActivity::class.java))
    }

    @SuppressLint("NotifyDataSetChanged")
    fun load_reached_goal(closeLoader: Boolean) {

        val start_idx = page * perPage

        val query =
            "SELECT * FROM intake_reached ORDER BY datetime(substr(date, 7, 4) || '-' || " +
                    "substr(date, 4, 2) || '-' || substr(date, 1, 2) || ' ' || " +
                    "substr(date, 12, 8)) DESC limit $start_idx,$perPage"

        val arr_data = sqliteHelper!!.get(query)

        for (k in arr_data.indices) {
            val reachedGoal = ReachedGoal()

            reachedGoal.day = arr_data[k]["date"]
            reachedGoal.quantity = arr_data[k]["qta"]!!.toFloat()
            reachedGoal.quantityOZ = arr_data[k]["qta_OZ"]!!.toFloat()

            reachedGoals.add(reachedGoal)
        }

        afterLoad = reachedGoals.size

        isLoading = if (afterLoad == 0) false
        else if (afterLoad > beforeLoad) true
        else false

        if (reachedGoals.size > 0)
            binding.lblNoRecordFound.visibility = View.GONE
        else binding.lblNoRecordFound.visibility = View.VISIBLE

        adapter!!.notifyDataSetChanged()
    }
}