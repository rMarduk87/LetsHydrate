package rpt.tool.mementobibere

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import rpt.tool.mementobibere.utils.view.ViewUtils

abstract class LetsHydrateBaseActivity : AppCompatActivity() {

    fun modifyOrentation(){
        if (!ViewUtils.isTablet(this)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        }
    }
}