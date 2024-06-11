package rpt.tool.mementobibere.ui.statistics.reachedGoal

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.ReachedGoalStatsFragmentBinding
import rpt.tool.mementobibere.utils.data.appmodel.ReachedGoal
import rpt.tool.mementobibere.utils.extensions.toCalendar
import rpt.tool.mementobibere.utils.extensions.toReachedStatsString
import rpt.tool.mementobibere.utils.helpers.SqliteHelper
import rpt.tool.mementobibere.utils.managers.SharedPreferencesManager
import rpt.tool.mementobibere.utils.view.adapters.ReachedGoalAdapter


class ReachedGoalFragment  : BaseFragment<ReachedGoalStatsFragmentBinding>(
    ReachedGoalStatsFragmentBinding::inflate) {

    var reachedList: MutableList<ReachedGoal> = arrayListOf()
    var adapter: ReachedGoalAdapter? = null
    var isLoading: Boolean = true
    private lateinit var sqliteHelper: SqliteHelper
    var beforeLoad: Int = 0
    var afterLoad: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        setBackGround()

        sqliteHelper = SqliteHelper(requireContext())
        adapter = ReachedGoalAdapter(requireActivity(), reachedList, object : ReachedGoalAdapter.CallBack {

            override fun onClickSelect(history: ReachedGoal?, position: Int) {
            }

        })

        binding.recyclerView.setLayoutManager(
            LinearLayoutManager(
                requireActivity(),
                LinearLayoutManager.VERTICAL,
                false
            )
        )

        binding.recyclerView.setAdapter(adapter)

        loadReached(false)
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setBackGround() {
        when (SharedPreferencesManager.themeInt) {
            0 -> toLightTheme()
            1 -> toDarkTheme()
        }
    }

    private fun toDarkTheme() {
        binding.layout.background = requireContext().getDrawable(R.drawable.ic_app_bg_dark)
    }

    private fun toLightTheme() {
        binding.layout.background = requireContext().getDrawable(R.drawable.ic_app_bg)
    }

    private fun loadReached(closeLoader: Boolean) {

       val c: Cursor = sqliteHelper.getAllReached(true)

        val arr_data = ArrayList<HashMap<String, String>>()

        if (c.moveToFirst()) {
            do {
                val map = HashMap<String, String>()
                for (i in 0 until c.columnCount) {
                    map[c.getColumnName(i)] = c.getString(i)
                }

                arr_data.add(map)
            } while (c.moveToNext())
        }

        for (k in arr_data.indices) {
            val reached = ReachedGoal()
            reached.day = arr_data[k]["date"]!!.toCalendar()
            reached.quantity = arr_data[k]["qta"]!!.toFloat().toReachedStatsString()
            reached.unit = arr_data[k]["unit"]

            reachedList.add(reached)
        }

        afterLoad = reachedList.size

        isLoading = if (afterLoad == 0) false
        else if (afterLoad > beforeLoad) true
        else false

        if (reachedList.size > 0) {
            binding.noData.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
        else{
            binding.noData.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        }

        adapter!!.notifyDataSetChanged()
    }

}