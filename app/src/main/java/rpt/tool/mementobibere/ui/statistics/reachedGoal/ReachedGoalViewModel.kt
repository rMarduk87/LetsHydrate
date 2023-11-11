package rpt.tool.mementobibere.ui.statistics.reachedGoal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rpt.tool.mementobibere.Application
import rpt.tool.mementobibere.utils.helpers.SqliteHelper
import rpt.tool.mementobibere.utils.view.recyclerview.items.reachedGoal.ReachedGoalItem

class ReachedGoalViewModel : ViewModel() {

    val reachedtItems = liveData {
        withContext(Dispatchers.Default) {
            emitSource(SqliteHelper(Application.instance).getAllReached().map
            {
                it.map { ReachedGoalItem(it) }
            })
        }
    }
}