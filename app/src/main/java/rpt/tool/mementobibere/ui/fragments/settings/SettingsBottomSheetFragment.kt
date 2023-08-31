package rpt.tool.mementobibere.ui.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import rpt.tool.mementobibere.databinding.SettingsBottomSheetFragmentBinding

class SettingsBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: SettingsBottomSheetFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SettingsBottomSheetFragmentBinding.inflate(layoutInflater,container,false)
        return binding.root

    }
}