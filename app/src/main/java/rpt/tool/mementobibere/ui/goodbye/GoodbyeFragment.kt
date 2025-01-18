package rpt.tool.mementobibere.ui.goodbye

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.databinding.FragmentGoodbyeBinding

class GoodbyeFragment:BaseFragment<FragmentGoodbyeBinding>(FragmentGoodbyeBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.link.justificationMode = JUSTIFICATION_MODE_INTER_WORD

        binding.link.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=rpt.tool.waterdiary")))
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=rpt.tool.waterdiary")))
            }
        }
    }
}