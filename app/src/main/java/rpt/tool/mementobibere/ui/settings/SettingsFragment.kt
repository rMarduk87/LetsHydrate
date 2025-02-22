package rpt.tool.mementobibere.ui.settings

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.MainActivity
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.FragmentSettingsBinding
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.data.appmodel.SoundModel
import rpt.tool.mementobibere.utils.helpers.AlertHelper
import rpt.tool.mementobibere.utils.managers.SharedPreferencesManager
import rpt.tool.mementobibere.utils.navigation.safeNavController
import rpt.tool.mementobibere.utils.navigation.safeNavigate
import rpt.tool.mementobibere.utils.view.adapters.SoundAdapter


class SettingsFragment : BaseFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {

    private lateinit var soundAdapter: SoundAdapter
    var lst_sounds: MutableList<SoundModel> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().window.navigationBarColor = requireContext().resources
            .getColor(R.color.str_green_card)

        body()
    }

    private fun body() {
        binding.include1.lblToolbarTitle.text = requireContext().getString(R.string.str_settings)
        binding.include1.leftIconBlock.setOnClickListener { finish() }
        binding.include1.rightIconBlock.visibility = View.GONE

        binding.weightBlock.setOnClickListener {
            safeNavController?.safeNavigate(SettingsFragmentDirections
                .actionSettingsFragmentToProfileFragment())
        }

        binding.switchNotification.setChecked(SharedPreferencesManager.disableNotificationAtGoal)

        binding.switchNotification.setOnCheckedChangeListener { buttonView, isChecked ->
            SharedPreferencesManager.disableNotificationAtGoal = isChecked
        }

        binding.switchSound.setChecked(SharedPreferencesManager.disableSoundWhenAddWater)

        binding.switchSound.setOnCheckedChangeListener { buttonView, isChecked ->
            SharedPreferencesManager.disableSoundWhenAddWater = isChecked
        }

        binding.txtMessage.text = SharedPreferencesManager.notificationMsg

        binding.editNotificationMessage.setOnClickListener { openMessageDialog() }

        binding.customSoundBlock.setOnClickListener { openSoundMenuPicker() }
    }

    private fun finish() {
        startActivity(Intent(requireActivity(), MainActivity::class.java))
    }

    @SuppressLint("InflateParams")
    private fun openMessageDialog() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.drawable_background_tra)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val view: View = LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_user_name,
            null, false)

        val btn_cancel = view.findViewById<RelativeLayout>(R.id.btn_cancel)
        val btn_add = view.findViewById<RelativeLayout>(R.id.btn_add)
        val img_cancel = view.findViewById<ImageView>(R.id.img_cancel)

        val txt_name = view.findViewById<AppCompatEditText>(R.id.txt_name)

        txt_name.requestFocus()

        btn_cancel.setOnClickListener { dialog.cancel() }

        img_cancel.setOnClickListener { dialog.cancel() }

        txt_name.setText(SharedPreferencesManager.notificationMsg)
        txt_name.setSelection(txt_name.text.toString().trim { it <= ' ' }.length)

        val alertHelper = AlertHelper(requireContext())

        btn_add.setOnClickListener {
            if (AppUtils.checkBlankData(txt_name.text.toString().trim { it <= ' ' })) {
                alertHelper.customAlert(requireContext().getString(R.string.str_not_mess_validation))
            } else {
                SharedPreferencesManager.notificationMsg =
                    txt_name.text.toString().trim { it <= ' ' }

                binding.txtMessage.text = SharedPreferencesManager.notificationMsg

                dialog.dismiss()
            }
        }

        dialog.setContentView(view)

        dialog.show()
    }

    @SuppressLint("InflateParams")
    fun openSoundMenuPicker() {
        loadSounds()

        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.drawable_background_tra)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)


        val view: View = LayoutInflater.from(requireActivity()).inflate(
            R.layout.dialog_sound_pick, null, false)


        val btn_cancel = view.findViewById<RelativeLayout>(R.id.btn_cancel)
        val btn_save = view.findViewById<RelativeLayout>(R.id.btn_save)


        btn_cancel.setOnClickListener { dialog.dismiss() }

        btn_save.setOnClickListener {
            for (k in 0 until lst_sounds.size) {
                if (lst_sounds[k].isSelected) {
                    SharedPreferencesManager.reminderSound = k
                    break
                }
            }
            dialog.dismiss()
        }


        val soundRecyclerView = view.findViewById<RecyclerView>(R.id.soundRecyclerView)

        soundAdapter = SoundAdapter(requireActivity(), lst_sounds, object : SoundAdapter.CallBack {
            @SuppressLint("NotifyDataSetChanged")
            override fun onClickSelect(time: SoundModel?, position: Int) {
                for (k in 0 until lst_sounds.size) {
                    lst_sounds[k].isSelected(false)
                }

                lst_sounds[position].isSelected(true)
                soundAdapter.notifyDataSetChanged()

                playSound(position)
            }
        })

        soundRecyclerView.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

        soundRecyclerView.adapter = soundAdapter

        dialog.setContentView(view)

        dialog.show()
    }

    private fun loadSounds() {
        lst_sounds.clear()

        lst_sounds.add(getSoundModel(0, "Default"))
        lst_sounds.add(getSoundModel(1, "Bell"))
        lst_sounds.add(getSoundModel(2, "Blop"))
        lst_sounds.add(getSoundModel(3, "Bong"))
        lst_sounds.add(getSoundModel(4, "Click"))
        lst_sounds.add(getSoundModel(5, "Echo droplet"))
        lst_sounds.add(getSoundModel(6, "Mario droplet"))
        lst_sounds.add(getSoundModel(7, "Ship bell"))
        lst_sounds.add(getSoundModel(8, "Simple droplet"))
        lst_sounds.add(getSoundModel(9, "Tiny droplet"))
    }

    private fun getSoundModel(index: Int, name: String?): SoundModel {
        val soundModel = SoundModel()
        soundModel.id = index
        soundModel.name = name
        soundModel.isSelected(SharedPreferencesManager.reminderSound == index)

        return soundModel
    }

    fun playSound(idx: Int) {
        var mp: MediaPlayer? = null

        when (idx) {
            0 -> mp = MediaPlayer.create(requireContext(), Settings.System.DEFAULT_NOTIFICATION_URI)
            1 -> mp = MediaPlayer.create(requireContext(), R.raw.bell)
            2 -> mp = MediaPlayer.create(requireContext(), R.raw.blop)
            3 -> mp = MediaPlayer.create(requireContext(), R.raw.bong)
            4 -> mp = MediaPlayer.create(requireContext(), R.raw.click)
            5 -> mp = MediaPlayer.create(requireContext(), R.raw.echo_droplet)
            6 -> mp = MediaPlayer.create(requireContext(), R.raw.mario_droplet)
            7 -> mp = MediaPlayer.create(requireContext(), R.raw.ship_bell)
            8 -> mp = MediaPlayer.create(requireContext(), R.raw.simple_droplet)
            9 -> mp = MediaPlayer.create(requireContext(), R.raw.tiny_droplet)
        }

        mp!!.start()
    }
}