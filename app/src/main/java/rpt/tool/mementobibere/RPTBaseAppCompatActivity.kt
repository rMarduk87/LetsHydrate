package rpt.tool.mementobibere

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.snackbar.Snackbar
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.AppUtils.Companion.TypeMessage

open class RPTBaseAppCompatActivity : AppCompatActivity() {

    fun showMessage(message: String, view: View, error:Boolean? = false, type: TypeMessage =TypeMessage.NOTHING) {
        val snackBar = Snackbar.make(view, "", 1500)
        val customSnackView: View =
            when(error){
                true ->layoutInflater.inflate(R.layout.error_toast_layout, null)
                else->layoutInflater.inflate(R.layout.info_toast_layout, null)
            }
        snackBar.view.setBackgroundColor(Color.TRANSPARENT)
        val snackbarLayout = snackBar.view as Snackbar.SnackbarLayout

        val text = customSnackView.findViewById<TextView>(R.id.tvMessage)
        text.text = message

        if(type==TypeMessage.SAVE){
            val anim = customSnackView.findViewById<LottieAnimationView>(R.id.anim)
            anim.setAnimation(R.raw.save)
        }
        else if(type==TypeMessage.SLEEP){
            val anim = customSnackView.findViewById<LottieAnimationView>(R.id.anim)
            anim.setAnimation(R.raw.sleep)
        }

        snackbarLayout.setPadding(0, 0, 0, 0)
        snackbarLayout.addView(customSnackView, 0)
        snackBar.show()
    }

    fun dismiss(){

    }
}