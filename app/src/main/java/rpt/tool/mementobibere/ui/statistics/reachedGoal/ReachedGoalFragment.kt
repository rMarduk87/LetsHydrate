package rpt.tool.mementobibere.ui.statistics.reachedGoal

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.FragmentReachedGoalBinding
import rpt.tool.mementobibere.ui.statistics.history.HistoryFragmentDirections
import rpt.tool.mementobibere.utils.data.appmodel.ReachedGoal
import rpt.tool.mementobibere.utils.helpers.SqliteHelper
import rpt.tool.mementobibere.utils.log.i
import rpt.tool.mementobibere.utils.navigation.safeNavController
import rpt.tool.mementobibere.utils.navigation.safeNavigate
import rpt.tool.mementobibere.utils.view.adapters.ReachedGoalAdapter


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sqliteHelper = SqliteHelper(requireContext())

        body()
    }

    private fun body() {
        binding.include1.lblToolbarTitle.text = requireContext()
            .getString(R.string.str_daily_goal_reached)
        binding.include1.leftIconBlock.setOnClickListener { finish() }
        binding.include1.rightIconBlock.visibility = View.GONE

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

    private fun finish() {
        safeNavController?.safeNavigate(ReachedGoalFragmentDirections.
        actionReachedGoalFragmentToDrinkFragment())
    }

    @SuppressLint("NotifyDataSetChanged")
    fun load_reached_goal(closeLoader: Boolean) {

        val start_idx = page * perPage

        val query =
            "SELECT * FROM intake_reached ORDER BY datetime(date) DESC limit $start_idx,$perPage"

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