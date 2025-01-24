package rpt.tool.mementobibere.ui.goodbye


import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD
import android.os.Bundle
import android.view.View
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.FragmentGoodbyeBinding
import rpt.tool.mementobibere.utils.AppUtils

class GoodbyeFragment:BaseFragment<FragmentGoodbyeBinding>(FragmentGoodbyeBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textView.justificationMode = JUSTIFICATION_MODE_INTER_WORD

        val t = requireContext().getText(R.string.migrate_app).toString()  + " " +
                AppUtils.date(requireContext()) + " " +
                requireContext().getText(R.string.migrate_app_final)

        binding.textView.text = t
    }
}