package rpt.tool.mementobibere.ui.drink

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.NotificationManager
import android.appwidget.AppWidgetManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface.OnShowListener
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.InputFilter
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.skydoves.balloon.BalloonAlign
import com.skydoves.balloon.balloon
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.InitUserInfoActivity
import rpt.tool.mementobibere.MainActivity
import rpt.tool.mementobibere.MenuNavigationActivity
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.FragmentDrinkBinding
import rpt.tool.mementobibere.ui.widget.NewAppWidget
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.balloon.blood.BloodDonorInfoBalloonFactory
import rpt.tool.mementobibere.utils.data.appmodel.Container
import rpt.tool.mementobibere.utils.data.appmodel.Menu
import rpt.tool.mementobibere.utils.extensions.toId
import rpt.tool.mementobibere.utils.helpers.AlarmHelper
import rpt.tool.mementobibere.utils.helpers.AlertHelper
import rpt.tool.mementobibere.utils.helpers.IntentHelper
import rpt.tool.mementobibere.utils.helpers.SqliteHelper
import rpt.tool.mementobibere.utils.log.d
import rpt.tool.mementobibere.utils.log.e
import rpt.tool.mementobibere.utils.log.v
import rpt.tool.mementobibere.utils.managers.MigrationManager
import rpt.tool.mementobibere.utils.managers.SharedPreferencesManager
import rpt.tool.mementobibere.utils.navigation.safeNavController
import rpt.tool.mementobibere.utils.navigation.safeNavigate
import rpt.tool.mementobibere.utils.view.adapters.ContainerAdapterNew
import rpt.tool.mementobibere.utils.view.adapters.MenuAdapter
import rpt.tool.mementobibere.utils.view.inputfilter.InputFilterWeightRange
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Random


class DrinkFragment : BaseFragment<FragmentDrinkBinding>(FragmentDrinkBinding::inflate) {

    private var enabled: Boolean = true
    private lateinit var viewWindow: View
    private var totalIntake: Float = 0f
    private lateinit var sqliteHelper: SqliteHelper
    private lateinit var dateNow: String
    private var notificStatus: Boolean = false
    private val avisBalloon by balloon<BloodDonorInfoBalloonFactory>()
    var menu_name: MutableList<Menu> = ArrayList<Menu>()
    var menuAdapter: MenuAdapter? = null
    var filter_cal: Calendar? = null
    var today_cal: Calendar? = null
    var yesterday_cal: Calendar? = null
    var containerArrayList: MutableList<Container> = ArrayList()
    var adapter: ContainerAdapterNew? = null
    var drink_water: Float = 0f
    var old_drink_water: Float = 0f
    var selected_pos: Int = 0
    var bottomSheetDialog: BottomSheetDialog? = null
    var handler: Handler? = null
    var runnable: Runnable? = null
    var max_bottle_height: Int = 870
    var progress_bottle_height: Int = 0
    var cp: Int = 0
    var np: Int = 0
    var ringtone: Ringtone? = null
    var btnclick: Boolean = true
    lateinit var alertHelper: AlertHelper
    var isAll: Boolean = false
    var isAvisDay: Boolean = false



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sqliteHelper = SqliteHelper(requireContext())
        alertHelper = AlertHelper(requireContext())
        dateNow = AppUtils.getCurrentOnlyDate()!!
        super.onViewCreated(view, savedInstanceState)

        if(SharedPreferencesManager.bloodDonorKey==1 &&
            !sqliteHelper.getAvisDay(dateNow)){
            binding.bloodDonorContainer.visibility = VISIBLE
            binding.imgBloodDonorHelp.visibility = GONE
            isAvisDay = false
            manageLblColor(requireContext().getColor(R.color.black))
        }
        else if(SharedPreferencesManager.bloodDonorKey==1 &&
            sqliteHelper.getAvisDay(dateNow)){
            binding.bloodDonorContainer.visibility = VISIBLE
            binding.imgBloodDonorHelp.visibility = VISIBLE
            isAvisDay = true
            manageLblColor(requireContext().getColor(R.color.red))
        }
        else{
            binding.bloodDonorContainer.visibility = GONE
            isAvisDay = false
            manageLblColor(requireContext().getColor(R.color.black))
        }

        totalIntake = SharedPreferencesManager.totalIntake

         if (totalIntake <= 0) {
             SharedPreferencesManager.isMigration = false
             startActivity(Intent(requireContext(), InitUserInfoActivity::class.java))
             requireActivity().finish()
        }

        if (SharedPreferencesManager.totalIntake == 0f) {
            AppUtils.DAILY_WATER_VALUE = 2500f
        } else {
            AppUtils.DAILY_WATER_VALUE = SharedPreferencesManager.totalIntake
        }

        if (AppUtils.checkBlankData(SharedPreferencesManager.unitString)) {
            AppUtils.WATER_UNIT_VALUE = "ml"
        } else {
            AppUtils.WATER_UNIT_VALUE = SharedPreferencesManager.unitString
        }

        animate()

        ringtone = RingtoneManager.getRingtone(
            requireContext(),
            Uri.parse(("android.resource://" + requireContext().packageName)
                    + "/" + R.raw.fill_water_sound)
        )
        ringtone!!.isLooping = false
    }

    private fun manageLblColor(color: Int) {
        binding.lblTotalGoal.setTextColor(color)
        binding.lblTotalDrunk.setTextColor(color)
    }

    private fun animate() {
        binding.contentFrame.viewTreeObserver
            .addOnGlobalLayoutListener { // TODO Auto-generated method stub
                try{
                    val w: Int = binding.contentFrame.width
                    val h: Int = binding.contentFrame.height
                    v("getWidthHeight", "$w   -   $h")
                }
                catch (e:Exception){
                    e.message?.let { d("Test", it) }
                }
            }

        binding.contentFrameTest.viewTreeObserver
            .addOnGlobalLayoutListener { // TODO Auto-generated method stub
                try{
                    val w: Int = binding.contentFrameTest.width
                    val h: Int = binding.contentFrameTest.height
                    v("getWidthHeight test", "$w   -   $h")
                    max_bottle_height = h - 30
                }
                catch(e:Exception){
                    e.message?.let { d("Test", it) }
                }
            }
    }

    private fun loadPhoto() {
        Glide.with(requireActivity()).load(
            if (SharedPreferencesManager.gender == 1)
                R.drawable.female_white
            else
                R.drawable.male_white
        ).apply(RequestOptions.circleCropTransform())
            .into(binding.imgUser)
    }

    private fun manageNotification() {
        val alarm = AlarmHelper()
        if(enabled){
            notificStatus = !notificStatus
            SharedPreferencesManager.notificationStatus =  notificStatus
            if (notificStatus) {
                Snackbar.make(viewWindow, getString(R.string.notification_enabled), Snackbar.LENGTH_SHORT).show()
                alarm.setAlarm(
                    requireContext(),
                    SharedPreferencesManager.notificationFreq.toLong())
            } else {
                Snackbar.make(viewWindow, getString(R.string.notification_disabled), Snackbar.LENGTH_SHORT).show()
                alarm.cancelAlarm(requireContext())
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onStart() {
        super.onStart()

        if(sqliteHelper.getAvisDay(dateNow)){
            isAvisDay = true
            manageLblColor(resources.getColor(R.color.red))
        }
        else{
            isAvisDay = false
            manageLblColor(requireContext().getColor(R.color.black))
        }

        if(SharedPreferencesManager.userName.isEmpty() ||
            SharedPreferencesManager.personHeight.isEmpty()){
            SharedPreferencesManager.isMigration = true
            val migrationManager = MigrationManager()
            migrationManager.migrate()
            startActivity(Intent(requireActivity(),InitUserInfoActivity::class.java))
        }

        notificStatus = SharedPreferencesManager.notificationStatus
        val alarm = AlarmHelper()
        if (!alarm.checkAlarm(requireContext()) && notificStatus) {
            val isAvisDay = sqliteHelper.getAvisDay(dateNow)
            var freq = SharedPreferencesManager.notificationFreq.toLong()
            if(isAvisDay){
                freq /= 2
            }
            else{
                freq = SharedPreferencesManager.notificationFreq.toLong()
            }
            alarm.setAlarm(
                requireContext(),freq
            )
        }

        binding.imgBloodDonorHelp.setOnClickListener {
            avisBalloon.showAlign(
                align = BalloonAlign.BOTTOM,
                mainAnchor = binding.calendarBloodBlock as View,
                subAnchorList = listOf(it),
            )
        }

        binding.calendarBloodBlock.setOnClickListener{
            val calendar = Calendar.getInstance()

            val mDatePicker = DatePickerDialog(
                requireContext(),
                { _, year, monthOfYear, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, monthOfYear)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val calendarPre = calendar
                    calendarPre.add(Calendar.DAY_OF_MONTH, -1)

                    val myFormat = "dd-MM-yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat)
                    val preAvis = sdf.format(calendarPre.time)
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                    val avis = sdf.format(calendar.time)
                    sqliteHelper.addAvis(preAvis)
                    sqliteHelper.addAvis(avis)
                    if(sdf.format(calendar.time) == dateNow){
                        if(totalIntake < 2000){
                            SharedPreferencesManager.totalIntake = 2000f
                        }
                    }
                    safeNavController?.safeNavigate(DrinkFragmentDirections
                        .actionDrinkFragmentToSelfFragment())
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            mDatePicker.datePicker.minDate = AppUtils.getMinDate()
            mDatePicker.setTitle("")
            mDatePicker.show()
        }
    }
    
    override fun onResume() {
        super.onResume()
        refreshAlarm(SharedPreferencesManager.notificationStatus)
        if (AppUtils.RELOAD_DASHBOARD) {
            init()
        } else {
            AppUtils.RELOAD_DASHBOARD = true
        }
    }

    fun init() {

        initMenuScreen()
        body()
    }

    @SuppressLint("RtlHardcoded")
    private fun initMenuScreen() {

        filter_cal = Calendar.getInstance(Locale.getDefault());
        today_cal = Calendar.getInstance(Locale.getDefault());
        yesterday_cal = Calendar.getInstance(Locale.getDefault());
        yesterday_cal!!.add(Calendar.DATE, -1);

        loadPhoto()

        binding.include1.lblToolbarTitle.text = requireContext().getString(R.string.str_today)
        binding.lblUserName.text = SharedPreferencesManager.userName
        
        menu_name.clear()
        menu_name.add(Menu(requireContext().getString(R.string.str_home), true))
        menu_name.add(Menu(requireContext().getString(R.string.str_drink_history), false))
        menu_name.add(Menu(requireContext().getString(R.string.str_drink_report), false))
        menu_name.add(Menu(requireContext().getString(R.string.str_settings), false))
        menu_name.add(Menu(requireContext().getString(R.string.str_faqs), false))
        menu_name.add(Menu(requireContext().getString(R.string.str_privacy_policy), false))
        menu_name.add(Menu(requireContext().getString(R.string.str_tell_a_friend), false))

        menuAdapter = MenuAdapter(requireActivity(), menu_name as ArrayList<Menu>, object : MenuAdapter.CallBack {
            @SuppressLint("RtlHardcoded")
            override fun onClickSelect(menu: Menu?, position: Int) {
                binding.drawerLayout.closeDrawer(Gravity.LEFT)

                when (position) {
                    1 -> {
                        SharedPreferencesManager.menu = 1
                        startActivity(Intent(requireActivity(), MenuNavigationActivity::class.java))
                    }
                    2 -> {
                        SharedPreferencesManager.menu = 2
                        startActivity(Intent(requireActivity(), MenuNavigationActivity::class.java))
                    }
                    3 -> {
                        SharedPreferencesManager.menu = 3
                        startActivity(Intent(requireActivity(), MenuNavigationActivity::class.java))
                    }
                    4 -> {
                        SharedPreferencesManager.menu = 4
                        startActivity(Intent(requireActivity(), MenuNavigationActivity::class.java))
                    }
                    5 -> {
                        val i = Intent(Intent.ACTION_VIEW)
                        i.setData(Uri.parse(AppUtils.PRIVACY_POLICY_ULR))
                        startActivity(i)
                    }
                    6 -> {
                        val str: String = requireContext().getString(R.string.app_share_txt)
                            .replace("#1", AppUtils.APP_SHARE_URL)

                        val ih = IntentHelper(requireContext(),requireActivity())

                        ih.ShareText(getApplicationName(requireContext()), str)
                    }
                }
            }
        })

        binding.btnRateUs.setOnClickListener {
            val appPackageName: String = requireContext().packageName
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$appPackageName")
                    )
                )
            } catch (anfe: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(
                            "https://play.google.com/store/apps/details?id=$appPackageName")
                    )
                )
            }
        }

        binding.btnContactUs.setOnClickListener {
            try {
                val intent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" +
                            requireContext().getString(R.string.mailto_riccardo_pezzolati_gmail_com)))
                intent.putExtra(Intent.EXTRA_SUBJECT, "")
                intent.putExtra(Intent.EXTRA_TEXT, "")
                startActivity(intent)
            } catch (ex: java.lang.Exception) {
                ex.message?.let { it1 -> e(Throwable(ex), it1) }
            }
        }

        binding.leftDrawer.setLayoutManager(
            LinearLayoutManager(
                activity,
                LinearLayoutManager.VERTICAL,
                false
            )
        )
        
        binding.leftDrawer.setAdapter(menuAdapter)
        
        binding.openProfile.setOnClickListener {
            try {
                if (binding.drawerLayout.isDrawerOpen(Gravity.LEFT)) 
                    binding.drawerLayout.closeDrawer(Gravity.LEFT)
            } catch (e: java.lang.Exception) {
                e.message?.let { it1 -> e(Throwable(e), it1) }
            }
            SharedPreferencesManager.menu = 5
            startActivity(Intent(requireActivity(), MenuNavigationActivity::class.java))
        }
        
        binding.include1.btnAlarm.setOnClickListener { showReminderDialog() }
        
        binding.include1.btnMenu.setOnClickListener {
            try {
                if (binding.drawerLayout.isDrawerOpen(Gravity.LEFT)) binding.drawerLayout.closeDrawer(Gravity.LEFT)
                else binding.drawerLayout.openDrawer(Gravity.LEFT)
            } catch (e: java.lang.Exception) {
                e.message?.let { it1 -> e(Throwable(e), it1) }
            }
        }
        
        binding.include1.imgPre.setOnClickListener {
            filter_cal!!.add(Calendar.DATE, -1)
            if (AppUtils.getDate(filter_cal!!.timeInMillis, AppUtils.DATE_FORMAT).equals(
                    AppUtils.getDate(
                        yesterday_cal!!.timeInMillis, AppUtils.DATE_FORMAT
                    ), true
                )
            ) binding.include1.lblToolbarTitle.text =
                requireContext().getString(R.string.str_yesterday)
            else binding.include1.lblToolbarTitle.text = AppUtils.getDate(
                filter_cal!!.timeInMillis,
                AppUtils.DATE_FORMAT
            )
            setCustomDate(AppUtils.getDate(filter_cal!!.timeInMillis, AppUtils.DATE_FORMAT))
        }

        binding.include1.imgNext.setOnClickListener(View.OnClickListener {
            filter_cal!!.add(Calendar.DATE, 1)
            if (filter_cal!!.timeInMillis > today_cal!!.timeInMillis) {
                filter_cal!!.add(Calendar.DATE, -1)
                return@OnClickListener
            }

            if (AppUtils.getDate(filter_cal!!.timeInMillis, AppUtils.DATE_FORMAT).equals(
                    AppUtils.getDate(
                        today_cal!!.timeInMillis, AppUtils.DATE_FORMAT
                    ),true
                )
            ) binding.include1.lblToolbarTitle.text = requireContext().getString(R.string.str_today)
            else if (AppUtils.getDate(filter_cal!!.timeInMillis, AppUtils.DATE_FORMAT)
                    .equals(
                        AppUtils.getDate(
                            yesterday_cal!!.timeInMillis, AppUtils.DATE_FORMAT
                        ),true
                    )
            ) binding.include1.lblToolbarTitle.text = requireContext().getString(R.string.str_yesterday)
            else binding.include1.lblToolbarTitle.text = AppUtils.getDate(
                filter_cal!!.timeInMillis,
                AppUtils.DATE_FORMAT
            )
            setCustomDate(AppUtils.getDate(filter_cal!!.timeInMillis, AppUtils.DATE_FORMAT))
        })
    }

    fun getApplicationName(context: Context): String {
        val applicationInfo = context.applicationInfo
        val stringId = applicationInfo.labelRes
        return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(
            stringId
        )
    }

    @SuppressLint("UseCompatLoadingForDrawables", "InflateParams")
    private fun showReminderDialog() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.drawable_background_tra)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)


        val view: View = LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_reminder,
            null, false)

        val img_cancel = view.findViewById<ImageView>(R.id.img_cancel)

        val off_block = view.findViewById<RelativeLayout>(R.id.off_block)

        val img_off = view.findViewById<ImageView>(R.id.img_off)

        val advance_settings = view.findViewById<AppCompatTextView>(R.id.advance_settings)

        advance_settings.setOnClickListener {
            dialog.dismiss()
            SharedPreferencesManager.menu = 7
            startActivity(Intent(requireActivity(), MenuNavigationActivity::class.java))
        }

        if (!SharedPreferencesManager.notificationStatus) {
            off_block.background =
                requireContext().resources.getDrawable(R.drawable.drawable_circle_selected)
            img_off.setImageResource(R.drawable.ic_off_selected)
        } else {
            off_block.background =
                requireContext().resources.getDrawable(R.drawable.drawable_circle_unselected)
            img_off.setImageResource(R.drawable.ic_off_normal)
        }

        off_block.setOnClickListener {
            off_block.background =
                requireContext().resources.getDrawable(R.drawable.drawable_circle_selected)
            img_off.setImageResource(R.drawable.ic_off_selected)
            SharedPreferencesManager.notificationStatus =
                !SharedPreferencesManager.notificationStatus
            manageNotification()
        }

        img_cancel.setOnClickListener { dialog.dismiss() }

        dialog.setContentView(view)

        dialog.show()
    }

    private fun setCustomDate(date: String) {
        count_specific_day_drink(date)
    }

    private fun count_specific_day_drink(custom_date: String) {
        val arr_dataO: ArrayList<HashMap<String, String>> = sqliteHelper.getdata(
            "stats",
            "n_date ='$custom_date'"
        )
        old_drink_water = 0f
        for (k in arr_dataO.indices) {
            old_drink_water += if (AppUtils.WATER_UNIT_VALUE.equals("ml",true))
                ("" + arr_dataO[k]["n_intook"]).toFloat()
            else
                ("" + arr_dataO[k]["n_intook_OZ"]).toFloat()
        }

        val arr_data22: ArrayList<HashMap<String, String>> = sqliteHelper.getdata(
            "stats",
            "n_date ='$custom_date'", "id", 1
        )

        var total_drink = 0.0

        if (arr_data22.size > 0) {
            total_drink =
                if (AppUtils.WATER_UNIT_VALUE.equals("ml",true))
                    arr_data22[0]["n_totalintake"]!!
                    .toDouble()
                else arr_data22[0]["n_totalintake_OZ"]!!.toDouble()
        }


        val arr_data: ArrayList<HashMap<String, String>> = sqliteHelper.getdata(
            "stats",
            "n_date ='$custom_date'"
        )

        drink_water = 0f
        for (k in arr_data.indices) {
            drink_water += if (AppUtils.WATER_UNIT_VALUE.equals("ml",true))
                arr_data[k]["n_intook"]!!
                .toInt()
            else arr_data[k]["n_intook_OZ"]!!.toInt()
            
        }
        if (custom_date.equals(
                AppUtils.getCurrentDate(AppUtils.DATE_FORMAT),
                ignoreCase = true
            )
        ) AppUtils.DAILY_WATER_VALUE = SharedPreferencesManager.totalIntake
        else if (total_drink > 0) AppUtils.DAILY_WATER_VALUE = ("" + total_drink).toFloat()
        else AppUtils.DAILY_WATER_VALUE = SharedPreferencesManager.totalIntake

        binding.lblTotalDrunk.text = getData("" + (drink_water).toInt() + " " +
                AppUtils.WATER_UNIT_VALUE)
        binding.lblTotalGoal.text = getData("" + (AppUtils.DAILY_WATER_VALUE) +
                " " + AppUtils.WATER_UNIT_VALUE)

        if(drink_water >= AppUtils.DAILY_WATER_VALUE){
            isAvisDay = false
            isAll = true
            manageLblColor(requireContext().getColor(R.color.fbutton_color_orange))
        }
        else{
            isAll = false
            manageLblColor(requireContext().getColor(R.color.black))
        }

        bloodDonorUI(custom_date)

        refresh_bottle(false, false)
    }

    private fun bloodDonorUI(date: String) {
        if(SharedPreferencesManager.bloodDonorKey==1 &&
            !sqliteHelper.getAvisDay(date)){
            binding.bloodDonorContainer.visibility = VISIBLE
            binding.imgBloodDonorHelp.visibility = GONE
            isAvisDay = false
            if(isAll){
                manageLblColor(requireContext().getColor(R.color.fbutton_color_orange))
            }
            else{
                manageLblColor(requireContext().getColor(R.color.black))
            }
        }
        else if(SharedPreferencesManager.bloodDonorKey==1 &&
            sqliteHelper.getAvisDay(date)){
            binding.bloodDonorContainer.visibility = VISIBLE
            binding.imgBloodDonorHelp.visibility = VISIBLE
            isAvisDay = true
            if(isAll){
                manageLblColor(requireContext().getColor(R.color.fbutton_color_orange))
            }
            else{
                manageLblColor(requireContext().getColor(R.color.red))
            }
        }
        else{
            binding.bloodDonorContainer.visibility = GONE
            isAvisDay = false
            if(isAll){
                manageLblColor(requireContext().getColor(R.color.fbutton_color_orange))
            }
            else{
                manageLblColor(requireContext().getColor(R.color.black))
            }
        }
    }

    private fun getData(str: String): String {
        return str.replace(",", ".")
    }

    private fun refresh_bottle(isFromCurrentProgress: Boolean, isRegularAnimation: Boolean) {
        val animationDuration = (if (isRegularAnimation) 50 else 5).toLong()

        if (handler != null && runnable != null) handler!!.removeCallbacks(runnable!!)

        btnclick = false

        cp = progress_bottle_height
        np = Math.round((drink_water * max_bottle_height) / AppUtils.DAILY_WATER_VALUE)


        if (cp <= np && isFromCurrentProgress) {
            binding.animationView.visibility = VISIBLE
            runnable = Runnable {
                if (cp > max_bottle_height) {
                    btnclick = true
                    callDialog()
                } else if (cp < np) {
                    cp += 6
                    binding.contentFrame.layoutParams.height = cp
                    binding.contentFrame.requestLayout()
                    handler!!.postDelayed(runnable!!, animationDuration)
                } else {
                    if(isAll){
                        binding.contentFrame.layoutParams.height = max_bottle_height
                        binding.contentFrame.requestLayout()
                        handler!!.postDelayed(runnable!!, animationDuration)
                    }
                    btnclick = true
                    callDialog()
                }
            }
            handler = Handler()
            handler!!.postDelayed(runnable!!, animationDuration)
        } else if (np == 0) {
            binding.animationView.visibility = GONE
            binding.contentFrame.layoutParams.height = np
            binding.contentFrame.requestLayout()
            btnclick = true
            callDialog()
        } else {
            binding.contentFrame.layoutParams.height = 0
            cp = 0
            binding.animationView.visibility = VISIBLE
            runnable = Runnable {
                if (cp > max_bottle_height) {
                    btnclick = true
                    callDialog()
                } else if (cp < np) {
                    cp += 6
                    binding.contentFrame.layoutParams.height = cp
                    binding.contentFrame.requestLayout()
                    handler!!.postDelayed(runnable!!, animationDuration)
                } else {
                    btnclick = true
                    callDialog()
                }
            }
            handler = Handler()
            handler!!.postDelayed(runnable!!, animationDuration)
        }

        progress_bottle_height = np

        if (np > 0) binding.animationView.visibility = VISIBLE
        else binding.animationView.visibility = GONE
    }

    private fun callDialog() {
        if (old_drink_water < AppUtils.DAILY_WATER_VALUE) {
            if (drink_water >= AppUtils.DAILY_WATER_VALUE) {
                addReached()
                showDailyGoalReachedDialog()
            }
        }
        old_drink_water = drink_water
    }

    private fun addReached() {
        val initialValues = ContentValues()

        initialValues.put(
            "date",
            "" + AppUtils.getDate(filter_cal!!.timeInMillis, AppUtils.DATE_FORMAT)
        )

        if (AppUtils.WATER_UNIT_VALUE.equals("ml",true)) {
            initialValues.put("qta", "" + drink_water)
            initialValues.put(
                "qta_OZ",
                "" + AppUtils.mlToOzUS(drink_water)
            )
        } else {
            initialValues.put(
                "qta",
                "" + AppUtils.ozUSToMl(drink_water)
            )
            initialValues.put("qta_OZ", "" + drink_water)
        }

        sqliteHelper.insert("intake_reached", initialValues)
    }

    @SuppressLint("InflateParams")
    fun showDailyGoalReachedDialog() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.drawable_background_tra)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val view: View = LayoutInflater.from(requireActivity())
            .inflate(R.layout.dialog_goal_reached, null, false)


        val img_cancel = view.findViewById<ImageView>(R.id.img_cancel)
        val btn_share = view.findViewById<RelativeLayout>(R.id.btn_share)

        img_cancel.setOnClickListener { dialog.dismiss() }

        btn_share.setOnClickListener {
            dialog.dismiss()
            val appPackageName: String = requireContext().packageName

            var share_text: String = requireContext().getString(R.string.str_share_text)
                .replace("$1", "" + (drink_water).toInt() + " " +
                        AppUtils.WATER_UNIT_VALUE)

            share_text = share_text.replace("$2", "@ " + AppUtils.APP_SHARE_URL)
            val ih = IntentHelper(requireContext(),requireActivity())
            ih.ShareText(getApplicationName(requireContext()), share_text)
        }

        dialog.setOnDismissListener { }

        dialog.setContentView(view)

        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun body() {
        val arr_data: ArrayList<HashMap<String, String>> = sqliteHelper.getdata(
            "stats",
            ("n_date ='" + AppUtils.getCurrentDate(AppUtils.DATE_FORMAT)) + "'"
        )
        old_drink_water = 0f
        for (k in arr_data.indices) {

            old_drink_water += if (AppUtils.WATER_UNIT_VALUE.equals("ml",true))
                ("" + arr_data[k]["n_intook"]).toFloat()
            else ("" + arr_data[k]["n_intook_OZ"]).toFloat()

        }

        count_today_drink(false)

        binding.selectedContainerBlock.setOnClickListener { openChangeContainerPicker() }

        binding.openHistory.setOnClickListener {
            SharedPreferencesManager.menu = 1
            startActivity(Intent(requireActivity(), MenuNavigationActivity::class.java))
        }

        binding.openReachedGoal.setOnClickListener {
            SharedPreferencesManager.menu = 6
            startActivity(Intent(requireActivity(), MenuNavigationActivity::class.java))
        }

        binding.openClickedContainer.setOnClickListener {
            SharedPreferencesManager.menu = 8
            startActivity(Intent(requireActivity(), MenuNavigationActivity::class.java))
        }
        
        binding.addWater.setOnClickListener(View.OnClickListener {
            if (containerArrayList.size > 0) {
                if (!AppUtils.getDate(filter_cal!!.timeInMillis, AppUtils.DATE_FORMAT)
                        .equals(
                            AppUtils.getDate(
                                today_cal!!.timeInMillis,
                                AppUtils.DATE_FORMAT
                            ),true
                        )
                ) {
                    return@OnClickListener
                }

                if (!btnclick) return@OnClickListener

                btnclick = false

                val random: Random = Random()

                if (random.nextBoolean()) {
                    AppUtils.RELOAD_DASHBOARD = false
                    execute_add_water()
                    AppUtils.RELOAD_DASHBOARD = true
                } else {
                    execute_add_water()
                }
            }
        })

        binding.drinkAllBlock.setOnClickListener {
            if(notExistsDrinkAll() && drink_water <= AppUtils.DAILY_WATER_VALUE){
                if (AppUtils.WATER_UNIT_VALUE.equals("ml",true)
                    && drink_water > 8000
                ) {
                    showDailyMoreThanTargetDialog()
                    btnclick = true
                    return@setOnClickListener
                } else if (!(AppUtils.WATER_UNIT_VALUE.equals("ml",true))
                    && drink_water > 270
                ) {
                    showDailyMoreThanTargetDialog()
                    btnclick = true
                    return@setOnClickListener
                }

                var count_drink_after_add_current_water = drink_water
                count_drink_after_add_current_water += AppUtils.calculateWaterOption(
                    count_drink_after_add_current_water,AppUtils.DAILY_WATER_VALUE)

                val option = count_drink_after_add_current_water-drink_water

                if (AppUtils.WATER_UNIT_VALUE.equals("ml",true)
                    && count_drink_after_add_current_water > 8000
                ) {
                    if (drink_water >= 8000) showDailyMoreThanTargetDialog()
                    else if (AppUtils.DAILY_WATER_VALUE < (8000 -
                                ("" + containerArrayList[selected_pos].containerValue).toFloat()))
                        showDailyMoreThanTargetDialog()
                } else if (!(AppUtils.WATER_UNIT_VALUE.equals("ml",true))
                    && count_drink_after_add_current_water > 270
                ) {
                    if (drink_water >= 270) showDailyMoreThanTargetDialog()
                    else if (AppUtils.DAILY_WATER_VALUE < (270 -
                                ("" + containerArrayList[selected_pos].containerValueOZ).toFloat()))
                        showDailyMoreThanTargetDialog()
                }

                if (drink_water == 8000f && AppUtils.WATER_UNIT_VALUE.equals("ml",true)) {
                    btnclick = true
                    // remove pending notifications
                    val mNotificationManager : NotificationManager = requireActivity()
                        .getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
                    mNotificationManager.cancelAll()
                    return@setOnClickListener
                } else if (drink_water == 270f && !AppUtils.WATER_UNIT_VALUE.equals("ml",true)) {
                    btnclick = true
                    // remove pending notifications
                    val mNotificationManager : NotificationManager = requireActivity()
                        .getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
                    mNotificationManager.cancelAll()
                    return@setOnClickListener
                }

                if (!SharedPreferencesManager.disableSoundWhenAddWater) {
                    ringtone!!.stop()
                    ringtone!!.play()
                }

                val initialValues = ContentValues()

                initialValues.put("n_intook", "" + if(AppUtils.WATER_UNIT_VALUE.equals("ml",true))
                option else AppUtils.ozUSToMl(option))
                initialValues.put(
                    "n_intook_OZ",
                    "" + if(AppUtils.WATER_UNIT_VALUE.equals("ml",true))
                        AppUtils.mlToOzUS(option) else option)

                initialValues.put(
                    "n_date",
                    "" + AppUtils.getDate(filter_cal!!.timeInMillis, AppUtils.DATE_FORMAT)
                )
                initialValues.put("time", "" + AppUtils.getCurrentTime(true))
                initialValues.put(
                    "dateTime", (("" + AppUtils.getDate(filter_cal!!.timeInMillis, AppUtils.DATE_FORMAT)
                            ) + " " + AppUtils.getCurrentDate("HH:mm:ss"))
                )

                if (AppUtils.WATER_UNIT_VALUE.equals("ml",true)) {
                    initialValues.put("n_totalintake", "" + AppUtils.DAILY_WATER_VALUE)
                    initialValues.put(
                        "n_totalintake_OZ",
                        "" + AppUtils.mlToOzUS(AppUtils.DAILY_WATER_VALUE)
                    )
                } else {
                    initialValues.put(
                        "n_totalintake",
                        "" + AppUtils.ozUSToMl(AppUtils.DAILY_WATER_VALUE)
                    )
                    initialValues.put("n_totalintake_OZ", "" + AppUtils.DAILY_WATER_VALUE)
                }

                sqliteHelper.insert("stats", initialValues)

                val initialValuesDrinkAll = ContentValues()

                initialValuesDrinkAll.put(
                    "date",
                    "" + AppUtils.getDate(filter_cal!!.timeInMillis, AppUtils.DATE_FORMAT)
                )

                sqliteHelper.insert("drink_all", initialValuesDrinkAll)

                addReachedUpdate(containerArrayList[selected_pos])

                addClicked("-2",AppUtils.getDate(filter_cal!!.timeInMillis, AppUtils.DATE_FORMAT))

                count_today_drink(true)

                val intent = Intent(requireActivity(), NewAppWidget::class.java)
                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
                val ids = AppWidgetManager.getInstance(requireActivity()).getAppWidgetIds(
                    ComponentName(
                        requireActivity(),
                        NewAppWidget::class.java
                    )
                )
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                requireActivity().sendBroadcast(intent)
            }
        }

        load_all_container()

        val unit: String = SharedPreferencesManager.unitString

        if (unit.equals("ml", ignoreCase = true)) {
            binding.containerName.text = "" + 
                    containerArrayList[selected_pos].containerValue + " " + unit
            if (containerArrayList[selected_pos].isCustom) Glide.with(requireContext())
                .load(R.drawable.ic_custom_ml).into(binding.imgSelectedContainer)
            else Glide.with(requireContext())
                .load(containerArrayList[selected_pos].containerValue?.let { getImage(it) })
                .into(binding.imgSelectedContainer)
        } else {
            binding.containerName.text = "" + containerArrayList[selected_pos].containerValueOZ + 
                    " " + unit
            if (containerArrayList[selected_pos].isCustom) Glide.with(requireContext())
                .load(R.drawable.ic_custom_ml).into(binding.imgSelectedContainer)
            else Glide.with(requireContext())
                .load(containerArrayList[selected_pos].containerValueOZ?.let { getImage(it) })
                .into(binding.imgSelectedContainer)
        }

        adapter =
            ContainerAdapterNew(requireActivity(), containerArrayList, object :
                ContainerAdapterNew.CallBack {
                @SuppressLint("NotifyDataSetChanged")
                override fun onClickSelect(menu: Container?, position: Int) {
                    bottomSheetDialog!!.dismiss()

                    selected_pos = position
                    
                    SharedPreferencesManager.selectedContainer = menu!!.containerId!!.toInt()

                    for (k in containerArrayList.indices) {
                        containerArrayList[k].isSelected(false)
                    }

                    containerArrayList[position].isSelected(true)

                    adapter!!.notifyDataSetChanged()

                    val unit: String = SharedPreferencesManager.unitString

                    if (unit.equals("ml", ignoreCase = true)) {
                        binding.containerName.text = "" + menu.containerValue + " " + unit
                        if (menu.isCustom) Glide.with(requireContext())
                            .load(R.drawable.ic_custom_ml)
                            .into(binding.imgSelectedContainer)
                        else Glide.with(requireContext()).load(menu.containerValue?.let {
                            getImage(
                                it
                            )
                        })
                            .into(binding.imgSelectedContainer)
                    } else {
                        binding.containerName.text = "" + menu.containerValueOZ + " " + unit
                        if (menu.isCustom) Glide.with(requireContext())
                            .load(R.drawable.ic_custom_ml)
                            .into(binding.imgSelectedContainer)
                        else Glide.with(requireContext()).load(menu.containerValueOZ?.let {
                            getImage(
                                it
                            )
                        })
                            .into(binding.imgSelectedContainer)
                    }
                }
            })
    }

    private fun notExistsDrinkAll(): Boolean {
        val arr_data: ArrayList<HashMap<String, String>> = sqliteHelper.getdata(
            "drink_all",
            "date ='$dateNow'"
        )
        return arr_data.size==0
    }

    private fun count_today_drink(isRegularAnimation: Boolean) {
        val arr_data: ArrayList<HashMap<String, String>> = sqliteHelper.getdata(
            "stats",
            ("n_date ='" + AppUtils.getDate(
                filter_cal!!.timeInMillis,
                AppUtils.DATE_FORMAT
            )) + "'"
        )

        drink_water = 0f
        for (k in arr_data.indices) {
            drink_water += if (AppUtils.WATER_UNIT_VALUE.equals("ml",true))
                ("" + arr_data[k]["n_intook"]).toFloat()
            else ("" + arr_data[k]["n_intook_OZ"]).toFloat()
        }

        binding.lblTotalDrunk.text = getData("" + (drink_water).toInt()
                + " " + AppUtils.WATER_UNIT_VALUE)
        binding.lblTotalGoal.text = getData("" + (AppUtils.DAILY_WATER_VALUE)
                + " " + AppUtils.WATER_UNIT_VALUE)

        if(drink_water >= AppUtils.DAILY_WATER_VALUE){
            manageLblColor(requireContext().getColor(R.color.fbutton_color_orange))
        }
        else{
            if(isAvisDay){
                manageLblColor(requireContext().getColor(R.color.red))
            }
            else{
                manageLblColor(requireContext().getColor(R.color.black))
            }
        }

        isAll = drink_water >= AppUtils.DAILY_WATER_VALUE

        refresh_bottle(true, isRegularAnimation)
    }

    private fun openChangeContainerPicker() {
        bottomSheetDialog = BottomSheetDialog(requireActivity())

        bottomSheetDialog!!.setOnShowListener(OnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet =
                d.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
                    ?: return@OnShowListener
            val bottomSheetBehavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
            bottomSheet.background = null
        })

        val layoutInflater = LayoutInflater.from(requireActivity())
        val view: View = layoutInflater.inflate(R.layout.bottom_sheet_change_container, null, false)

        val containerRecyclerViewN = view.findViewById<RecyclerView>(R.id.containerRecyclerView)
        val add_custom_container = view.findViewById<RelativeLayout>(R.id.add_custom_container)

        val manager = GridLayoutManager(requireActivity(),
            3, GridLayoutManager.VERTICAL, false)
        containerRecyclerViewN.layoutManager = manager
        containerRecyclerViewN.adapter = adapter

        add_custom_container.setOnClickListener {
            bottomSheetDialog!!.dismiss()
            openCustomContainerPicker()
        }

        bottomSheetDialog!!.setContentView(view)

        bottomSheetDialog!!.show()
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun openCustomContainerPicker() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.drawable_background_tra)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)


        val view: View = LayoutInflater.from(requireActivity())
            .inflate(R.layout.bottom_sheet_add_custom_container, null, false)

        val btn_cancel = view.findViewById<RelativeLayout>(R.id.btn_cancel)
        val btn_add = view.findViewById<RelativeLayout>(R.id.btn_add)
        val img_cancel = view.findViewById<ImageView>(R.id.img_cancel)

        val txt_value = view.findViewById<AppCompatEditText>(R.id.txt_value)
        val lbl_unit = view.findViewById<AppCompatTextView>(R.id.lbl_unit)

        lbl_unit.text = requireContext().getString(R.string.str_capacity)
            .replace("$1", AppUtils.WATER_UNIT_VALUE)

        if (AppUtils.WATER_UNIT_VALUE.equals("ml",true)) txt_value.filters =
            arrayOf<InputFilter>(InputFilterWeightRange(1.0, 8000.0))
        else txt_value.filters = arrayOf<InputFilter>(InputFilterWeightRange(1.0, 270.0))

        txt_value.requestFocus()

        btn_cancel.setOnClickListener { dialog.cancel() }

        img_cancel.setOnClickListener { dialog.cancel() }

        btn_add.setOnClickListener {
            if (AppUtils.checkBlankData(txt_value.text.toString().trim { it <= ' ' })) {
                alertHelper.customAlert(requireContext().getString(R.string.str_enter_value_validation))
            } else if (txt_value.text.toString().trim { it <= ' ' }.toInt() == 0) {
                alertHelper.customAlert(requireContext().getString(R.string.str_enter_value_validation))
            } else {
                var tml = 0.0f
                var tfloz = 0.0f

                if (AppUtils.WATER_UNIT_VALUE.equals("ml",true)) {
                    tml = txt_value.text.toString().trim { it <= ' ' }.toFloat()
                    tfloz = AppUtils.mlToOzUS(tml)
                } else {
                    tfloz = txt_value.text.toString().trim { it <= ' ' }.toFloat()
                    tml = AppUtils.ozUSToMl(tfloz)
                }

                d("HeightWeightHelper", "$tml @@@ $tfloz")

                val c: Cursor = sqliteHelper.getMax(
                    "SELECT MAX(ContainerID) FROM tbl_container_details"
                )
                var nextContainerID = 0

                try {
                    if (c.count > 0) {
                        c.moveToNext()
                        nextContainerID = c.getString(0).toInt() + 1
                    }
                } catch (e: java.lang.Exception) {
                    e.message?.let { it1 -> e(Throwable(e), it1) }
                }

                val initialValues = ContentValues()

                initialValues.put("containerID", "" + nextContainerID)
                initialValues.put("containerValue", "" + Math.round(tml))
                initialValues.put("containerValueOZ", "" + Math.round(tfloz))
                initialValues.put("is_open", "1")
                initialValues.put("is_custom", "1")

                sqliteHelper.insert("container", initialValues)

                load_all_container()

                SharedPreferencesManager.selectedContainer = nextContainerID

                var tmp_pos = -1

                for (k in containerArrayList.indices) {
                    try {
                        if (nextContainerID == containerArrayList[k].containerId!!.toInt()) {
                            containerArrayList[k].isSelected(true)
                            tmp_pos = k
                        } else containerArrayList[k].isSelected(false)
                    } catch (e: java.lang.Exception) {
                        containerArrayList[k].isSelected(false)
                    }
                }

                val unit: String = SharedPreferencesManager.unitString

                if (tmp_pos >= 0) {
                    selected_pos = tmp_pos

                    val menu = containerArrayList[tmp_pos]

                    if (unit.equals("ml", ignoreCase = true)) {
                        binding.containerName.text = "" + menu.containerValue + " " + unit
                        if (menu.isCustom) Glide.with(requireContext()).load(R.drawable.ic_custom_ml)
                            .into(binding.imgSelectedContainer
                            )
                        else Glide.with(requireContext()).load(getImage(menu.containerValue!!))
                            .into(
                                binding.imgSelectedContainer
                            )
                    } else {
                        binding.containerName.text = "" + menu.containerValueOZ + " " + unit
                        if (menu.isCustom) Glide.with(requireContext()).load(R.drawable.ic_custom_ml)
                            .into(
                                binding.imgSelectedContainer
                            )
                        else Glide.with(requireContext()).load(getImage(menu.containerValueOZ!!))
                            .into(
                                binding.imgSelectedContainer
                            )
                    }
                }

                adapter!!.notifyDataSetChanged()

                dialog.dismiss()
            }
        }

        dialog.setContentView(view)

        dialog.show()
    }

    private fun execute_add_water() {

        if (AppUtils.WATER_UNIT_VALUE.equals("ml",true)
            && drink_water > 8000
        ) {
            showDailyMoreThanTargetDialog()
            btnclick = true
            return
        } else if (!(AppUtils.WATER_UNIT_VALUE.equals("ml",true))
            && drink_water > 270
        ) {
            showDailyMoreThanTargetDialog()
            btnclick = true
            return
        }

        var count_drink_after_add_current_water = drink_water

        if (AppUtils.WATER_UNIT_VALUE.equals("ml",true))
            count_drink_after_add_current_water +=
                ("" + containerArrayList[selected_pos].containerValue).toFloat()
        else if (!(AppUtils.WATER_UNIT_VALUE.equals("ml",true)))
            count_drink_after_add_current_water +=
                ("" + containerArrayList[selected_pos].containerValueOZ).toFloat()

        d(
            "above8000", (("" + AppUtils.WATER_UNIT_VALUE + " @@@  " + drink_water
                    + " @@@ " + count_drink_after_add_current_water)
        ))


        if (AppUtils.WATER_UNIT_VALUE.equals("ml",true)
            && count_drink_after_add_current_water > 8000
        ) {
            if (drink_water >= 8000) showDailyMoreThanTargetDialog()
            else if (AppUtils.DAILY_WATER_VALUE < (8000 -
                        ("" + containerArrayList[selected_pos].containerValue).toFloat()))
                showDailyMoreThanTargetDialog()
        } else if (!(AppUtils.WATER_UNIT_VALUE.equals("ml",true))
            && count_drink_after_add_current_water > 270
        ) {
            if (drink_water >= 270) showDailyMoreThanTargetDialog()
            else if (AppUtils.DAILY_WATER_VALUE < (270 -
                        ("" + containerArrayList[selected_pos].containerValueOZ).toFloat()))
                showDailyMoreThanTargetDialog()
        }

        if (drink_water == 8000f && AppUtils.WATER_UNIT_VALUE.equals("ml",true)) {
            btnclick = true
            // remove pending notifications
            val mNotificationManager : NotificationManager = requireActivity()
            .getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
             mNotificationManager.cancelAll()
            return
        } else if (drink_water == 270f && !AppUtils.WATER_UNIT_VALUE.equals("ml",true)) {
            btnclick = true
            // remove pending notifications
            val mNotificationManager : NotificationManager = requireActivity()
                .getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.cancelAll()
            return
        }

        if (!SharedPreferencesManager.disableSoundWhenAddWater) {
            ringtone!!.stop()
            ringtone!!.play()
        }

        val initialValues = ContentValues()

        initialValues.put("n_intook", "" + containerArrayList[selected_pos].containerValue)
        initialValues.put(
            "n_intook_OZ",
            "" + containerArrayList[selected_pos].containerValueOZ
        )

        initialValues.put(
            "n_date",
            "" + AppUtils.getDate(filter_cal!!.timeInMillis, AppUtils.DATE_FORMAT)
        )
        initialValues.put("time", "" + AppUtils.getCurrentTime(true))
        initialValues.put(
            "dateTime", (("" + AppUtils.getDate(filter_cal!!.timeInMillis, AppUtils.DATE_FORMAT)
                    ) + " " + AppUtils.getCurrentDate("HH:mm:ss"))
        )

        if (AppUtils.WATER_UNIT_VALUE.equals("ml",true)) {
            initialValues.put("n_totalintake", "" + AppUtils.DAILY_WATER_VALUE)
            initialValues.put(
                "n_totalintake_OZ",
                "" + AppUtils.mlToOzUS(AppUtils.DAILY_WATER_VALUE)
            )
        } else {
            initialValues.put(
                "n_totalintake",
                "" + AppUtils.ozUSToMl(AppUtils.DAILY_WATER_VALUE)
            )
            initialValues.put("n_totalintake_OZ", "" + AppUtils.DAILY_WATER_VALUE)
        }

        sqliteHelper.insert("stats", initialValues)

        addClicked(containerArrayList[selected_pos].containerValue,
            AppUtils.getDate(filter_cal!!.timeInMillis, AppUtils.DATE_FORMAT))

        addReachedUpdate(containerArrayList[selected_pos])

        count_today_drink(true)

        val intent = Intent(requireActivity(), NewAppWidget::class.java)
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
        val ids = AppWidgetManager.getInstance(requireActivity()).getAppWidgetIds(
            ComponentName(
                requireActivity(),
                NewAppWidget::class.java
            )
        )
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        requireActivity().sendBroadcast(intent)
    }

    private fun addClicked(containerValue: String?, date: String) {

        sqliteHelper.addOrUpdateIntookCounter(date,containerValue!!.toFloat().toId(),1,null)
    }

    private fun addReachedUpdate(container: Container) {

        val reachedGoal = sqliteHelper.getdata("intake_reached",
            "date='"+AppUtils.getDate(filter_cal!!.timeInMillis, AppUtils.DATE_FORMAT)+"'")

        if(reachedGoal.size>0){
            val initialValues = ContentValues()

            val new = container.containerValue!!.toFloat() +
                    reachedGoal[0]["qta"]!!.toFloat()

            val newOZ = container.containerValueOZ!!.toFloat() +
                    reachedGoal[0]["qta_OZ"]!!.toFloat()

            initialValues.put("qta", "" + new)
            initialValues.put(
                "qta_OZ",
                "" + newOZ)

            sqliteHelper.update("intake_reached",initialValues,
                "date='"+AppUtils.getDate(filter_cal!!.timeInMillis,
                    AppUtils.DATE_FORMAT)+"'")

        }

    }

    @SuppressLint("InflateParams")
    private fun showDailyMoreThanTargetDialog() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.drawable_background_tra)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)


        val view: View =
            LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_goal_target_reached,
                null, false)


        val lbl_desc = view.findViewById<AppCompatTextView>(R.id.lbl_desc)
        val img_cancel = view.findViewById<ImageView>(R.id.img_cancel)
        val btn_share = view.findViewById<RelativeLayout>(R.id.btn_share)
        val img_bottle = view.findViewById<ImageView>(R.id.img_bottle)

        if (AppUtils.WATER_UNIT_VALUE.equals("ml",true))
            img_bottle.setImageResource(R.drawable.ic_limit_ml)
        else img_bottle.setImageResource(R.drawable.ic_limit_oz)

        val desc =
            if (AppUtils.WATER_UNIT_VALUE.equals("ml",true)) "8000 ml" else
                "270 fl oz"

        lbl_desc.text = requireContext().getString(
            R.string.str_you_should_not_drink_more_then_target)
            .replace("$1", desc)

        img_cancel.setOnClickListener { dialog.dismiss() }

        dialog.setContentView(view)

        dialog.show()
    }

    private fun load_all_container() {
        containerArrayList.clear()

        val arr_container: ArrayList<HashMap<String, String>> =
            sqliteHelper.getdata("container", "is_custom", 1)

        var selected_container_id = "1"

        selected_container_id = if (SharedPreferencesManager.selectedContainer == 0) "1"
        else "" + SharedPreferencesManager.selectedContainer

        for (k in arr_container.indices) {
            val container = Container()
            container.containerId = arr_container[k]["containerID"]
            container.containerValue = arr_container[k]["containerValue"]
            container.containerValueOZ = arr_container[k]["containerValueOZ"]
            container.isOpen(
                arr_container[k]["is_open"].equals(
                    "1",
                    ignoreCase = true
                )
            )
            container.isSelected(
                selected_container_id.equals(
                    arr_container[k]["containerID"],
                    ignoreCase = true
                )
            )
            container.isCustom(
                arr_container[k]["is_custom"].equals(
                    "1",
                    ignoreCase = true
                )
            )
            if (container.isSelected) selected_pos = k //+1

            containerArrayList.add(container)
        }
    }

    fun getImage(`val`: String): Int {
        var drawable: Int = R.drawable.ic_custom_ml

        if (AppUtils.WATER_UNIT_VALUE.equals("ml",true)) {
            if (`val`.toFloat() == 50f) drawable = R.drawable.ic_50_ml
            else if (`val`.toFloat() == 100f) drawable = R.drawable.ic_100_ml
            else if (`val`.toFloat() == 150f) drawable = R.drawable.ic_150_ml
            else if (`val`.toFloat() == 200f) drawable = R.drawable.ic_200_ml
            else if (`val`.toFloat() == 250f) drawable = R.drawable.ic_250_ml
            else if (`val`.toFloat() == 300f) drawable = R.drawable.ic_300_ml
            else if (`val`.toFloat() == 500f) drawable = R.drawable.ic_500_ml
            else if (`val`.toFloat() == 600f) drawable = R.drawable.ic_600_ml
            else if (`val`.toFloat() == 700f) drawable = R.drawable.ic_700_ml
            else if (`val`.toFloat() == 800f) drawable = R.drawable.ic_800_ml
            else if (`val`.toFloat() == 900f) drawable = R.drawable.ic_900_ml
            else if (`val`.toFloat() == 1000f) drawable = R.drawable.ic_1000_ml
        } else {
            if (`val`.toFloat() == 1.6907f) drawable =
                R.drawable.ic_50_ml
            else if (`val`.toFloat() == 3.3814f) drawable =
                R.drawable.ic_100_ml
            else if (`val`.toFloat() == 5.0721f) drawable =
                R.drawable.ic_150_ml
            else if (`val`.toFloat() == 6.7628f) drawable =
                R.drawable.ic_200_ml
            else if (`val`.toFloat() == 8.45351f) drawable =
                R.drawable.ic_250_ml
            else if (`val`.toFloat() == 10.1442f) drawable =
                R.drawable.ic_300_ml
            else if (`val`.toFloat() == 16.907f) drawable =
                R.drawable.ic_500_ml
            else if (`val`.toFloat() == 20.2884f) drawable =
                R.drawable.ic_600_ml
            else if (`val`.toFloat() == 23.6698f) drawable =
                R.drawable.ic_700_ml
            else if (`val`.toFloat() == 27.0512f) drawable =
                R.drawable.ic_800_ml
            else if (`val`.toFloat() == 30.4326f) drawable =
                R.drawable.ic_900_ml
            else if (`val`.toFloat() == 33.814f) drawable =
                R.drawable.ic_1000_ml
        }

        return drawable
    }


    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val alarm = AlarmHelper()
        when (requestCode) {
            123 -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    Snackbar.make(viewWindow, getString(R.string.notification_enabled), Snackbar.LENGTH_SHORT).show()
                    alarm.setAlarm(
                        requireContext(),
                        SharedPreferencesManager.notificationFreq.toLong())

                } else {
                    Snackbar.make(viewWindow, getString(R.string.notification_disabled), Snackbar.LENGTH_SHORT).show()
                    alarm.cancelAlarm(requireContext())
                }
            }
        }
    }

    private fun refreshAlarm(notify: Boolean){
        val alarm = AlarmHelper()
        if(notify){
            alarm.setAlarm(
                requireContext(),
                SharedPreferencesManager.notificationFreq.toLong())
        }
        else{
            alarm.cancelAlarm(requireContext())
            val activity: Activity? = activity
            if (activity != null && activity is MainActivity) {
                activity.initPermissions()
            }
        }
    }
}