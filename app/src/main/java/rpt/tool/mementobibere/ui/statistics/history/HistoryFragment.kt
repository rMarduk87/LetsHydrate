package rpt.tool.mementobibere.ui.statistics.history

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.MainActivity
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.FragmentHistoryBinding
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.data.appmodel.History
import rpt.tool.mementobibere.utils.extensions.toId
import rpt.tool.mementobibere.utils.helpers.SqliteHelper
import rpt.tool.mementobibere.utils.log.i
import rpt.tool.mementobibere.utils.navigation.safeNavController
import rpt.tool.mementobibere.utils.navigation.safeNavigate
import rpt.tool.mementobibere.utils.view.adapters.HistoryAdapter


class HistoryFragment:BaseFragment<FragmentHistoryBinding>(FragmentHistoryBinding::inflate) {

    var histories: ArrayList<History> = ArrayList()
    var adapter: HistoryAdapter? = null
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
            .getString(R.string.str_drink_history)
        binding.include1.leftIconBlock.setOnClickListener { finish() }
        binding.include1.rightIconBlock.visibility = View.GONE

        binding.historyRecyclerView.isNestedScrollingEnabled = false

        adapter = HistoryAdapter(requireActivity(), histories, object : HistoryAdapter.CallBack {
            override fun onClickSelect(history: History?, position: Int) {
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onClickRemove(history: History?, position: Int) {
                val dialog: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
                    .setMessage(requireContext()
            .getString(R.string.str_history_remove_confirm_message))
                    .setPositiveButton(requireContext()
            .getString(R.string.str_yes)
                    ) { dialog, whichButton ->
                        removeForCounter(history)
                        sqliteHelper!!.remove("stats", "id=" + history!!.id)
                        if(checkIfRemoveReached(history.drinkDate)){
                            history.drinkDate?.let {
                                sqliteHelper!!.remove("intake_reached",
                                    "date=?", it
                                )
                            }
                        }
                        if(checkIfRemoveDrinkAll(history.drinkDate)){
                            history.drinkDate?.let {
                                sqliteHelper!!.remove("drink_all",
                                    "date=?", it
                                )
                            }
                        }
                        page = 0
                        isLoading = true
                        histories.clear()
                        load_history(false)

                        adapter!!.notifyDataSetChanged()
                        dialog.dismiss()
                    }
                    .setNegativeButton(requireContext()
            .getString(R.string.str_no)
                    ) { dialog, whichButton -> dialog.dismiss() }

                dialog.show()
            }
        })

        binding.historyRecyclerView.setLayoutManager(
            LinearLayoutManager(
                requireActivity(),
                LinearLayoutManager.VERTICAL,
                false
            )
        )

        binding.historyRecyclerView.setAdapter(adapter)

        load_history(false)

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

                    load_history(true)
                }
            }
        })
    }

    private fun removeForCounter(history: History?) {
        history!!.drinkDate?.let { sqliteHelper!!.addOrUpdateIntookCounter(it,
            if(!AppUtils.checkIsCustom(history.containerValue!!.toFloat()))
            history.containerValue!!.toFloat().toId() else (-2f).toId(),-1,null) }
    }


    private fun checkIfRemoveReached(drinkDate: String?): Boolean {
        val mes_unit: String = AppUtils.WATER_UNIT_VALUE
        val arr_data2: ArrayList<HashMap<String, String>> =
            sqliteHelper!!.getdata("stats",
                "n_date ='$drinkDate'"
            )

        var tot = 0f

        if(arr_data2.size==0){
            return true
        }


        for (j in arr_data2.indices) {
            tot += if (mes_unit.equals(
                    "ml",
                    ignoreCase = true
                )
            )
                arr_data2[j]["n_intook"]!!.toFloat()
            else
                arr_data2[j]["n_intook_OZ"]!!.toFloat()
        }
        val today = if (mes_unit.equals(
                "ml",
                ignoreCase = true
            )
        )
            arr_data2[0]["n_totalintake"]!!.toFloat()
        else
            arr_data2[0]["n_totalintake_OZ"]!!.toFloat()

        return today < tot
    }

    private fun checkIfRemoveDrinkAll(drinkDate: String?): Boolean {
        val arr_data2: ArrayList<HashMap<String, String>> =
            sqliteHelper!!.getdata("stats",
                "n_date ='$drinkDate'"
            )

        return arr_data2.size==0
    }

    private fun finish() {
        startActivity(Intent(requireActivity(),MainActivity::class.java))
    }

    @SuppressLint("NotifyDataSetChanged")
    fun load_history(closeLoader: Boolean) {

        val start_idx = page * perPage

        val query =
            "SELECT * FROM stats ORDER BY datetime(substr(dateTime, 7, 4) || '-' ||" +
                    " substr(dateTime, 4, 2) || '-' || substr(dateTime, 1, 2) || ' ' ||" +
                    " substr(dateTime, 12, 8)) DESC limit $start_idx,$perPage"


        val arr_data = sqliteHelper!!.get(query)


        val mes_unit: String = AppUtils.WATER_UNIT_VALUE

        for (k in arr_data.indices) {
            val history = History()
            history.id = arr_data[k]["id"]

            history.containerMeasure = mes_unit

            history.containerValue = "" + (arr_data[k]["n_intook"]!!.toFloat())
            history.containerValueOZ = "" + (arr_data[k]["n_intook_OZ"]!!.toFloat())

            history.drinkDate = arr_data[k]["n_date"]
            history.drinkTime =
                AppUtils.FormateDateFromString("HH:mm", "hh:mm a",
                    arr_data[k]["time"])

            val arr_data2: ArrayList<HashMap<String, String>> =
                sqliteHelper!!.getdata("stats",
                    "n_date ='" + arr_data[k]["n_date"] + "'")

            var tot = 0f

            for (j in arr_data2.indices) {
                tot += if (mes_unit.equals(
                        "ml",
                        ignoreCase = true
                    )
                )
                    arr_data2[j]["n_intook"]!!.toFloat()
                else
                    arr_data2[j]["n_intook_OZ"]!!.toFloat()
            }

            history.totalML = "" + (tot).toInt() + " " + mes_unit

            histories.add(history)
        }

        afterLoad = histories.size

        isLoading = if (afterLoad == 0) false
        else if (afterLoad > beforeLoad) true
        else false

        if (histories.size > 0)
            binding.lblNoRecordFound.visibility = View.GONE
        else binding.lblNoRecordFound.visibility = View.VISIBLE

        adapter!!.notifyDataSetChanged()
    }
}