package rpt.tool.mementobibere

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.snackbar.Snackbar
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.Inflate
import rpt.tool.mementobibere.utils.extensions.toAppTheme


abstract class BaseFragment<VB : ViewBinding>(private val inflate: Inflate<VB>) : Fragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    private lateinit var sharedPref: SharedPreferences
    private var themeInt : Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedPref = requireActivity().getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)
        themeInt = sharedPref.getInt(AppUtils.THEME_KEY,0)
        setTheme()
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    private fun setTheme() {
        val theme = themeInt.toAppTheme()
        requireActivity().setTheme(theme)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("InflateParams")
    fun showMessage(message: String, view: View, error:Boolean? = false, type: AppUtils.Companion.TypeMessage = AppUtils.Companion.TypeMessage.NOTHING) {
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

        if(type== AppUtils.Companion.TypeMessage.SAVE){
            val anim = customSnackView.findViewById<LottieAnimationView>(R.id.anim)
            anim.setAnimation(R.raw.save)
        }

        snackbarLayout.setPadding(0, 0, 0, 0)
        snackbarLayout.addView(customSnackView, 0)
        snackBar.show()
    }
}