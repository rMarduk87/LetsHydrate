package rpt.tool.mementobibere.ui.statistics.daily

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rpt.tool.mementobibere.Application
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.helpers.SqliteHelper
import rpt.tool.mementobibere.utils.view.recyclerview.items.daily.DailyItem

class DailyViewModel(): ViewModel() {


    val reachedtItems = liveData {
        withContext(Dispatchers.Default) {
            emitSource(
                SqliteHelper(Application.instance).getAllStatsDaily().map
            {
                it.map { DailyItem(it) }
            })
        }
    }
}