package rpt.tool.mementobibere

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
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
        themeInt = sharedPref.getInt(AppUtils.THEME,0)
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
}