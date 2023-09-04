package rpt.tool.mementobibere.ui.fragments.info

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.InfoBottomSheetFragmentBinding
import rpt.tool.mementobibere.ui.libraries.alert.dialog.SweetAlertDialog


class InfoBottomSheetFragment: BottomSheetDialogFragment() {

    private lateinit var binding: InfoBottomSheetFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = InfoBottomSheetFragmentBinding.inflate(layoutInflater,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSendMail.setOnClickListener {

            SweetAlertDialog(requireContext(), SweetAlertDialog.NORMAL_TYPE)
                .setTitleText(getString(R.string.send_mail))
                .setContentText(getString(R.string.dialog_mail_message))
                .setCancelText("No")
                .setConfirmText("Ok")
                .showCancelButton(true)
                .setCancelClickListener { _ -> // reuse previous dialog instance, keep widget user state, reset them if you need
                    Toast.makeText(requireContext(),
                        android.R.string.no, Toast.LENGTH_SHORT).show()

                }
                .setConfirmClickListener { _ ->
                    sendMail()
                }
                .show()
        }

        binding.btnShowCredits.setOnClickListener {

            SweetAlertDialog(requireContext(), SweetAlertDialog.NORMAL_TYPE)
                .setTitleText(getString(R.string.credits_text))
                .setContentText(getString(R.string.credits_list_text))
                .setConfirmText("Ok")
                .showCancelButton(true)
                .setConfirmClickListener { _ ->
                }
                .show()
        }

    }

    private fun sendMail() {
        val email = Intent(Intent.ACTION_SENDTO)
        email.data = Uri.parse(getString(R.string.mailto_riccardo_pezzolati_gmail_com))
        email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.suggestions_for_memento_bibere))
        email.putExtra(Intent.EXTRA_TEXT, getString(R.string.your_message_here))
        startActivity(email)
    }
}