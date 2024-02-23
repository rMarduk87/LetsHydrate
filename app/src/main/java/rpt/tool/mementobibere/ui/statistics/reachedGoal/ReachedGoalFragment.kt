package rpt.tool.mementobibere.ui.statistics.reachedGoal

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.SharedPreferences
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
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.ReachedGoalStatsFragmentBinding
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.extensions.defaultSetUp
import rpt.tool.mementobibere.utils.helpers.SqliteHelper
import rpt.tool.mementobibere.utils.managers.SharedPreferencesManager
import rpt.tool.mementobibere.utils.navigation.safeNavController
import rpt.tool.mementobibere.utils.navigation.safeNavigate
import rpt.tool.mementobibere.utils.view.recyclerview.items.reachedGoal.ReachedGoalItem
import java.text.SimpleDateFormat
import java.util.Calendar

class ReachedGoalFragment  : BaseFragment<ReachedGoalStatsFragmentBinding>(
    ReachedGoalStatsFragmentBinding::inflate) {

    private val itemAdapter = ItemAdapter<ReachedGoalItem>()
    private val fastAdapter = FastAdapter.with(itemAdapter)

    private val viewModel: ReachedGoalViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        setBackGround()

        binding.recyclerView.defaultSetUp(
            fastAdapter
        )

        addDataToListView()

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

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setBackGround() {
        when (SharedPreferencesManager.themeInt) {
            0 -> toLightTheme()
            1 -> toDarkTheme()
            2 -> toWaterTheme()
            3 -> toGrapeTheme()
            4 -> toBeeTheme()
        }
    }

    private fun toBeeTheme() {
        binding.layout.background = requireContext().getDrawable(R.drawable.ic_app_bg_b)
    }

    private fun toGrapeTheme() {
        binding.layout.background = requireContext().getDrawable(R.drawable.ic_app_bg_g)
    }

    private fun toWaterTheme() {
        binding.layout.background = requireContext().getDrawable(R.drawable.ic_app_bg_w)
    }

    private fun toDarkTheme() {
        binding.layout.background = requireContext().getDrawable(R.drawable.ic_app_bg_dark)
    }

    private fun toLightTheme() {
        binding.layout.background = requireContext().getDrawable(R.drawable.ic_app_bg)
    }

}