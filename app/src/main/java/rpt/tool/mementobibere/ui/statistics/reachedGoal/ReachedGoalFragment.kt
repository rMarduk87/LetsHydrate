package rpt.tool.mementobibere.ui.statistics.reachedGoal

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.google.android.material.textfield.TextInputLayout
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import github.com.st235.lib_expandablebottombar.Menu
import github.com.st235.lib_expandablebottombar.MenuItem
import github.com.st235.lib_expandablebottombar.MenuItemDescriptor
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.ReachedGoalStatsFragmentBinding
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.navigation.safeNavController
import rpt.tool.mementobibere.utils.navigation.safeNavigate
import rpt.tool.mementobibere.utils.view.recyclerview.items.reachedGoal.ReachedGoalItem
import rpt.tool.mementobibere.utils.extensions.defaultSetUp
import rpt.tool.mementobibere.utils.helpers.SqliteHelper
import java.text.SimpleDateFormat
import java.util.Calendar

class ReachedGoalFragment:
    BaseFragment<ReachedGoalStatsFragmentBinding>(ReachedGoalStatsFragmentBinding::inflate) {

    private lateinit var bottomMenu: Menu
    private lateinit var sharedPref: SharedPreferences
    private var themeInt : Int = 0
    private var unit : Int = 0
    private val itemAdapter = ItemAdapter<ReachedGoalItem>()
    private val fastAdapter = FastAdapter.with(itemAdapter)

    private val viewModel: ReachedGoalViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sharedPref = requireActivity().getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)
        themeInt = sharedPref.getInt(AppUtils.THEME_KEY,0)
        unit = sharedPref.getInt(AppUtils.UNIT_KEY,0)
        super.onViewCreated(view, savedInstanceState)
        setBackGround()
        initBottomBar()

        binding.recyclerView.defaultSetUp(
            fastAdapter
        )

        addDataToListView()

        binding.addPreviousReached.setOnClickListener {
            val li = LayoutInflater.from(requireContext())
            val promptsView = li.inflate(R.layout.add_reached_custom_input_dialog, null)

            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.setView(promptsView)

            val userInput = promptsView
                .findViewById(R.id.etReachedDay) as TextInputLayout

            userInput.editText!!.setOnClickListener {
                val calendar = Calendar.getInstance()

                val mDatePicker = DatePickerDialog(requireContext(),
                    { _, year, monthOfYear, dayOfMonth ->
                        calendar.set(Calendar.YEAR, year)
                        calendar.set(Calendar.MONTH, monthOfYear)
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                        val myFormat = "dd-MM-yyyy" // mention the format you need
                        val sdf = SimpleDateFormat(myFormat)
                        userInput.editText!!.setText(sdf.format(calendar.time))
                    },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)
                    )
                mDatePicker.datePicker.maxDate = AppUtils.getMaxDate()
                mDatePicker.setTitle("")
                mDatePicker.show()
            }

            val userBtnAdd = promptsView.findViewById(R.id.btnAdd) as Button

            val sqliteHelper = SqliteHelper(requireContext())

            val unit = AppUtils.calculateExtensions(sharedPref.getInt(AppUtils.UNIT_KEY,0))

            userBtnAdd.setOnClickListener {
                when {

                    TextUtils.isEmpty(userInput.editText!!.text.toString()) ->
                        showError(getString(R.string.please_input_a_valid_date))

                    else -> {

                        sqliteHelper.addReachedGoal(userInput.editText!!.text.toString(),sharedPref.getFloat(AppUtils.TOTAL_INTAKE_KEY,0f),unit)
                        sqliteHelper.addAll(userInput.editText!!.text.toString(),
                            sharedPref.getFloat(AppUtils.TOTAL_INTAKE_KEY,0f).toInt(),
                            sharedPref.getFloat(AppUtils.TOTAL_INTAKE_KEY,0f),unit)
                        userInput.editText!!.setText("")
                    }
                }
            }

            alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
                safeNavController?.safeNavigate(
                    ReachedGoalFragmentDirections.actionGoalFragmentToItself())
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

        binding.btnBack.setOnClickListener {
            safeNavController?.safeNavigate(
                ReachedGoalFragmentDirections.actionGoalFragmentToDrinkFragment())
        }
    }

    private fun addDataToListView() {
        viewModel.reachedtItems.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.recyclerView.visibility = View.GONE
                binding.noData.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                binding.noData.visibility = View.GONE
                itemAdapter.set(it)
            }
        }
    }

    private fun initBottomBar() {

        bottomMenu = binding.bottomBarStats.menu

        createMenu()
    }

    private fun createMenu() {

        var colorString = if(themeInt==0){
            "#41B279"
        }
        else{
            "#29704D"
        }

        for (i in AppUtils.listIdsStats.indices) {
            bottomMenu.add(
                MenuItemDescriptor.Builder(
                    requireContext(),
                    AppUtils.listIdsStats[i],
                    AppUtils.listIconStats[i],
                    AppUtils.listStringStats[i],
                    Color.parseColor(colorString)
                )
                    .build()
            )
        }



        binding.bottomBarStats.onItemSelectedListener = { _, i, _ ->
            manageListeners(i)
        }

        binding.bottomBarStats.onItemReselectedListener = { _, i, _ ->
            manageListeners(i)
        }

        bottomMenu.select(R.id.icon_reach)
    }

    private fun manageListeners(i: MenuItem) {
        when(i.id) {
            R.id.icon_all -> goToAllStats()
            R.id.icon_intook -> goToIntookStats()
            R.id.icon_reach -> return
        }
    }

    private fun goToAllStats() {
        safeNavController?.safeNavigate(ReachedGoalFragmentDirections
            .actionGoalFragmentToAllstatsFragment())
    }

    private fun goToIntookStats() {
        safeNavController?.safeNavigate(ReachedGoalFragmentDirections
            .actionGoalFragmentToToIntookFragment())
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setBackGround() {
        when(themeInt){
            0->toLightTheme()
            1->toDarkTheme()
        }
    }

    private fun toDarkTheme() {
        binding.layout.background = requireContext().getDrawable(R.drawable.ic_app_bg_dark)
        binding.bottomBarStats.setBackgroundColorRes(R.color.gray_btn_bg_pressed_color)
    }

    private fun toLightTheme() {
        binding.layout.background = requireContext().getDrawable(R.drawable.ic_app_bg)
        binding.bottomBarStats.setBackgroundColorRes(R.color.colorWhite)
    }

    @SuppressLint("InflateParams")
    private fun showError(error: String) {
        val toast = Toast(requireContext())
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        val customView: View =
            layoutInflater.inflate(R.layout.error_toast_layout, null)

        val text = customView.findViewById<TextView>(R.id.tvMessage)
        text.text = error
        toast.view = customView
        toast.show()
    }
}
