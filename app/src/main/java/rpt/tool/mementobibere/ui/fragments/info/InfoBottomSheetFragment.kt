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
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(getString(R.string.send_mail))
            builder.setMessage(getString(R.string.dialog_mail_message))
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

            builder.setPositiveButton(android.R.string.yes) { _, _ ->
                sendMail()
            }

            builder.setNegativeButton(android.R.string.no) { _, _ ->
                Toast.makeText(requireContext(),
                    android.R.string.no, Toast.LENGTH_SHORT).show()
            }

            builder.show()
        }

        binding.btnShowCredits.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(getString(R.string.credits_text))
            builder.setMessage(getString(R.string.credits_list_text))
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

            builder.setPositiveButton(android.R.string.ok) { _, _ ->
            }

            builder.show()
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