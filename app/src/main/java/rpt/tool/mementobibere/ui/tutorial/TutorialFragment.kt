package rpt.tool.mementobibere.ui.tutorial

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.material.snackbar.Snackbar
import com.skydoves.balloon.BalloonAlign
import com.skydoves.balloon.balloon
import github.com.st235.lib_expandablebottombar.Menu
import github.com.st235.lib_expandablebottombar.MenuItemDescriptor
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.TutorialFragmentBinding
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.balloon.blood.BloodDonorInfoBalloonFactory
import rpt.tool.mementobibere.utils.balloon.tutorial.FifthHelpBalloonFactory
import rpt.tool.mementobibere.utils.balloon.tutorial.FirstHelpBalloonFactory
import rpt.tool.mementobibere.utils.balloon.tutorial.FourthHelpBalloonFactory
import rpt.tool.mementobibere.utils.balloon.tutorial.SecondHelpBalloonFactory
import rpt.tool.mementobibere.utils.balloon.tutorial.SeventhHelpBalloonFactory
import rpt.tool.mementobibere.utils.balloon.tutorial.SixthHelpBalloonFactory
import rpt.tool.mementobibere.utils.balloon.tutorial.ThirdHelpBalloonFactory
import rpt.tool.mementobibere.utils.extensions.toCalculatedValue
import rpt.tool.mementobibere.utils.extensions.toExtractFloat
import rpt.tool.mementobibere.utils.extensions.toMainTheme
import rpt.tool.mementobibere.utils.extensions.toNumberString
import rpt.tool.mementobibere.utils.helpers.SqliteHelper
import rpt.tool.mementobibere.utils.navigation.safeNavController
import rpt.tool.mementobibere.utils.navigation.safeNavigate

class TutorialFragment : BaseFragment<TutorialFragmentBinding>(TutorialFragmentBinding::inflate) {
    private var start: Boolean = false
    private var saveIntook: Float = 0f
    private var isTutorial: Boolean = false
    private lateinit var unit: String
    private lateinit var menuNotify: Menu
    private lateinit var menuNotNotify: Menu
    private lateinit var outValue: TypedValue
    private lateinit var viewWindow: View
    private var totalIntake: Float = 0f
    private var inTook: Float = 0f
    private lateinit var sharedPref: SharedPreferences
    private lateinit var sqliteHelper: SqliteHelper
    private lateinit var dateNow: String
    private var selectedOption: Float? = null
    private var snackbar: Snackbar? = null
    private var themeInt: Int = 0
    private var current_unitInt: Int = 0
    private var new_unitInt: Int = 0
    private var value_50: Float = 50f
    private var value_100: Float = 100f
    private var value_150: Float = 150f
    private var value_200: Float = 200f
    private var value_250: Float = 250f
    private var value_300: Float = 300f
    private var value_350: Float = 350f
    private var btnSelected: Int? = null
    private val firstHelpBalloon by balloon<FirstHelpBalloonFactory>()
    private val secondHelpBalloon by balloon<SecondHelpBalloonFactory>()
    private val thirdHelpBalloon by balloon<ThirdHelpBalloonFactory>()
    private val fourthHelpBalloon by balloon<FourthHelpBalloonFactory>()
    private val fifthHelpBalloon by balloon<FifthHelpBalloonFactory>()
    private val sixthHelpBalloon by balloon<SixthHelpBalloonFactory>()
    private val seventhHelpBalloon by balloon<SeventhHelpBalloonFactory>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sharedPref = requireActivity().getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)
        themeInt = sharedPref.getInt(AppUtils.THEME_KEY,0)
        current_unitInt = sharedPref.getInt(AppUtils.UNIT_KEY,0)
        new_unitInt = sharedPref.getInt(AppUtils.UNIT_NEW_KEY,0)
        value_50 = sharedPref.getFloat(AppUtils.VALUE_50_KEY,50f)
        value_100 = sharedPref.getFloat(AppUtils.VALUE_100_KEY,100f)
        value_150 = sharedPref.getFloat(AppUtils.VALUE_150_KEY,150f)
        value_200 = sharedPref.getFloat(AppUtils.VALUE_200_KEY,200f)
        value_250 = sharedPref.getFloat(AppUtils.VALUE_250_KEY,250f)
        value_300 = sharedPref.getFloat(AppUtils.VALUE_300_KEY,300f)
        value_350 = sharedPref.getFloat(AppUtils.VALUE_350_KEY,350f)
        sqliteHelper = SqliteHelper(requireContext())
        dateNow = AppUtils.getCurrentOnlyDate()!!
        setTheme()
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        setBackGround()

        if(sharedPref.getInt(AppUtils.BLOOD_DONOR_KEY,0)==1 &&
            !sqliteHelper.getAvisDay(dateNow)){
            binding.calendarAvis.visibility = View.VISIBLE
            binding.calendarAvisHelp!!.visibility = View.VISIBLE
        }
        else if(sharedPref.getInt(AppUtils.BLOOD_DONOR_KEY,0)==1 &&
            sqliteHelper.getAvisDay(dateNow)){
            binding.calendarAvis.visibility = View.VISIBLE
            binding.infoAvis!!.visibility = View.VISIBLE
            binding.calendarAvisHelp!!.visibility = View.VISIBLE
        }
        else{
            binding.calendarAvis.visibility = View.GONE
            binding.infoAvis!!.visibility = View.GONE
            binding.calendarAvisHelp!!.visibility = View.GONE
        }

        totalIntake = try {
            sharedPref.getFloat(AppUtils.TOTAL_INTAKE_KEY, 0f)
        }catch (ex:Exception){
            var totalIntakeOld = sharedPref.getInt(AppUtils.TOTAL_INTAKE_KEY,0)
            var editor = sharedPref.edit()
            editor.remove(AppUtils.TOTAL_INTAKE_KEY)
            editor.putFloat(AppUtils.TOTAL_INTAKE_KEY,totalIntakeOld.toFloat())
            editor.apply()
            sharedPref.getFloat(AppUtils.TOTAL_INTAKE_KEY, 0f)
                .toCalculatedValue(current_unitInt,new_unitInt)
        }

        viewWindow = requireActivity().window.decorView.findViewById<View>(android.R.id.content)
        initBottomBar()
        if (!sharedPref.getBoolean(AppUtils.FIRST_RUN_KEY, true) || totalIntake > 0) {
            initIntookValue()
            setValueForDrinking()
        }

        binding.mainActivityParent.isClickable = false
        binding.bottomBarNotify.isClickable = false
        binding.bottomBarNotNotify.isClickable = false
        binding.cardView.isClickable = false
        binding.calendarAvis.isClickable = false
        binding.calendarAvisHelp.isClickable = false
        binding.infoAvis!!.isClickable = false
        binding.op50ml!!.isClickable = false
        binding.op100ml!!.isClickable = false
        binding.op150ml!!.isClickable = false
        binding.op200ml!!.isClickable = false
        binding.op250ml!!.isClickable = false
        binding.op300ml!!.isClickable = false
        binding.opCustom.isClickable = false
        binding.opDrinkAll.isClickable = false
        binding.op350ml!!.isClickable = false
    }

    private fun initIntookValue() {
        unit = AppUtils.calculateExtensions(current_unitInt)
        binding.ml50!!.text = "${value_50.toNumberString()} $unit"
        binding.ml100!!.text = "${value_100.toNumberString()} $unit"
        binding.ml150!!.text = "${value_150.toNumberString()} $unit"
        binding.ml200!!.text = "${value_200.toNumberString()} $unit"
        binding.ml250!!.text = "${value_250.toNumberString()} $unit"
        binding.ml300!!.text = "${value_300.toNumberString()} $unit"
        binding.ml350!!.text = "${value_350.toNumberString()} $unit"
    }

    private fun initBottomBar() {

        menuNotify = binding.bottomBarNotify.menu
        menuNotNotify = binding.bottomBarNotNotify.menu

        createMenu()

    }

    private fun createMenu() {

        var colorString = if(themeInt==0){
            "#41B279"
        }
        else if(themeInt==1){
            "#29704D"
        }
        else if(themeInt ==2){
            "#4167B2"
        }
        else{
            "#FF6200EE"
        }

        for (i in AppUtils.listIds.indices) {
            menuNotify.add(
                MenuItemDescriptor.Builder(
                    requireContext(),
                    AppUtils.listIds[i],
                    AppUtils.listIconNotify[i],
                    AppUtils.listStringNotify[i],
                    Color.parseColor(colorString)
                )
                    .build()
            )
        }

        for (i in AppUtils.listIds.indices) {
            menuNotNotify.add(
                MenuItemDescriptor.Builder(
                    requireContext(),
                    AppUtils.listIds[i],
                    AppUtils.listIconNotNotify[i],
                    AppUtils.listStringNotNotify[i],
                    Color.parseColor(colorString)
                )
                    .build()
            )
        }
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    private fun addDrinkedWater() {

        if(start){
            inTook += selectedOption!!
        }
        else{
            inTook = 0f
        }
        setWaterLevel(inTook, totalIntake)
        showMessage(
            getString(R.string.your_water_intake_was_saved), viewWindow, false,
            AppUtils.Companion.TypeMessage.SAVE
        )
    }

    private fun setBackGround() {
        when(themeInt){
            0->toLightTheme()
            1->toDarkTheme()
            2->toWaterTheme()
            3->toGrapeTheme()
        }
    }

    private fun toGrapeTheme() {
        binding.mainActivityParent.background = requireContext().getDrawable(R.drawable.ic_app_bg_g)
        if(sqliteHelper.getAvisDay(dateNow)){
            binding.tvIntook.setTextColor(resources.getColor(R.color.red))
            binding.tvTotalIntake.setTextColor(resources.getColor(R.color.red))
        }
        else{
            binding.tvIntook.setTextColor(requireContext().getColor(R.color.colorBlack))
            binding.tvTotalIntake.setTextColor(requireContext().getColor(R.color.colorBlack))
        }
        binding.intakeProgress.colorBackground = requireContext().getColor(R.color.teal_700)
    }

    private fun toWaterTheme() {
        binding.mainActivityParent.background = requireContext().getDrawable(R.drawable.ic_app_bg_w)
        if(sqliteHelper.getAvisDay(dateNow)){
            binding.tvIntook.setTextColor(resources.getColor(R.color.red))
            binding.tvTotalIntake.setTextColor(resources.getColor(R.color.red))
        }
        else{
            binding.tvIntook.setTextColor(requireContext().getColor(R.color.colorWhite))
            binding.tvTotalIntake.setTextColor(requireContext().getColor(R.color.colorWhite))
        }
        binding.bottomBarNotify.setBackgroundColorRes(R.color.colorWhite)
        binding.bottomBarNotNotify.setBackgroundColorRes(R.color.colorWhite)
        binding.intakeProgress.colorBackground = requireContext().getColor(R.color.teal_700)
    }

    private fun toDarkTheme() {
        binding.mainActivityParent.background = requireContext().getDrawable(R.drawable.ic_app_bg_dark)
        if(sqliteHelper.getAvisDay(dateNow)){
            binding.tvIntook.setTextColor(resources.getColor(R.color.red))
            binding.tvTotalIntake.setTextColor(resources.getColor(R.color.red))
        }
        else{
            binding.tvIntook.setTextColor(requireContext().getColor(R.color.colorBlack))
            binding.tvTotalIntake.setTextColor(requireContext().getColor(R.color.colorBlack))
        }
        binding.intakeProgress.colorBackground = requireContext().getColor(R.color.teal_700)
    }

    private fun toLightTheme() {
        binding.mainActivityParent.background = requireContext().getDrawable(R.drawable.ic_app_bg)
        if(sqliteHelper.getAvisDay(dateNow)){
            binding.tvIntook.setTextColor(resources.getColor(R.color.red))
            binding.tvTotalIntake.setTextColor(resources.getColor(R.color.red))
        }
        else{
            binding.tvIntook.setTextColor(requireContext().getColor(R.color.colorWhite))
            binding.tvTotalIntake.setTextColor(requireContext().getColor(R.color.colorWhite))
        }
        binding.intakeProgress.colorBackground = requireContext().getColor(R.color.teal_700)
    }

    private fun setTheme() {
        val theme = themeInt.toMainTheme()
        requireActivity().setTheme(theme)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()

        setBackGround()

        if(sqliteHelper.getAvisDay(dateNow)){
            binding.tvIntook.setTextColor(resources.getColor(R.color.red))
            binding.tvTotalIntake.setTextColor(resources.getColor(R.color.red))
        }

        totalIntake = try {
            sharedPref.getFloat(AppUtils.TOTAL_INTAKE_KEY, 0f)
        }catch (ex:Exception){
            var totalIntakeOld = sharedPref.getInt(AppUtils.TOTAL_INTAKE_KEY,0)
            var editor = sharedPref.edit()
            editor.remove(AppUtils.TOTAL_INTAKE_KEY)
            editor.putFloat(AppUtils.TOTAL_INTAKE_KEY,totalIntakeOld.toFloat())
            editor.apply()
            sharedPref.getFloat(AppUtils.TOTAL_INTAKE_KEY, 0f)
                .toCalculatedValue(current_unitInt,new_unitInt)
        }

        setValueForDrinking()

        var background = if(themeInt!=2){
            R.drawable.option_select_bg
        }
        else{
            R.drawable.option_select_bg_w
        }

        outValue = TypedValue()
        requireContext().applicationContext.theme.resolveAttribute(
            android.R.attr.selectableItemBackground,
            outValue,
            true
        )

        if (!sharedPref.getBoolean(AppUtils.FIRST_RUN_KEY, true) || totalIntake > 0) {
            setValueForDrinking()
        }

        startTutorial(binding.tutorial!!,background)
    }

    private fun startTutorial(view: View, background: Int) {
        isTutorial = true
        start = true
        saveIntook = inTook

        Handler(Looper.getMainLooper()).postDelayed({
            firstHelpBalloon.showAlign(
                align = BalloonAlign.BOTTOM,
                mainAnchor = binding.tutorial as View,
                subAnchorList = listOf(view),
            )
        },2000)


        Handler(Looper.getMainLooper()).postDelayed({
            firstHelpBalloon.dismiss()
            goToSecond(view,background)
        }, 5000)
    }

    private fun goToSecond(view: View, background: Int) {
        secondHelpBalloon.showAlign(
            align = BalloonAlign.BOTTOM,
            mainAnchor = binding.op50ml as View,
            subAnchorList = listOf(view),
        )

        Handler(Looper.getMainLooper()).postDelayed({
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            btnSelected = 0
            selectedOption = binding.ml50!!.text.toString().toExtractFloat()
            binding.op50ml.background = requireContext().getDrawable(background)
            binding.op100ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op150ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op200ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op250ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op300ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.opCustom.background = requireContext().getDrawable(outValue.resourceId)
            binding.opDrinkAll.background = requireContext().getDrawable(outValue.resourceId)
            binding.op350ml.background = requireContext().getDrawable(outValue.resourceId)
            addDrinkedWater()
        }, 2500)

        Handler(Looper.getMainLooper()).postDelayed({
            secondHelpBalloon.dismiss()
            goToThird(view,background)
        }, 3300)
    }

    private fun goToThird(view: View, background: Int) {
        thirdHelpBalloon.showAlign(
            align = BalloonAlign.BOTTOM,
            mainAnchor = binding.opCustom as View,
            subAnchorList = listOf(view),
        )

        Handler(Looper.getMainLooper()).postDelayed({
            thirdHelpBalloon.dismiss()
            goToFourth(view,background)
        }, 2800)


    }

    private fun goToFourth(view: View, background: Int) {
        fourthHelpBalloon.showAlign(
            align = BalloonAlign.BOTTOM,
            mainAnchor = binding.opDrinkAll as View,
            subAnchorList = listOf(view),
        )

        Handler(Looper.getMainLooper()).postDelayed({
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            btnSelected = 7
            selectedOption = AppUtils.calculateOption(inTook,totalIntake)!!
            binding.op50ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op100ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op150ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op200ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op250ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op300ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.opCustom.background = requireContext().getDrawable(outValue.resourceId)
            binding.opDrinkAll.background = requireContext().getDrawable(background)
            binding.op350ml.background = requireContext().getDrawable(outValue.resourceId)
            addDrinkedWater()
        }, 3400)

        Handler(Looper.getMainLooper()).postDelayed({
            binding.opDrinkAll.background = requireContext().getDrawable(outValue.resourceId)
            fourthHelpBalloon.dismiss()
            inTook = saveIntook
            start = false
            addDrinkedWater()
            isTutorial = false
            goToFifth(view)
        }, 5500)
    }

    private fun goToFifth(view: View) {
        fifthHelpBalloon.showAlign(
            align = BalloonAlign.BOTTOM,
            mainAnchor = binding.btnUndo as View,
            subAnchorList = listOf(view),
        )

        Handler(Looper.getMainLooper()).postDelayed({
            fifthHelpBalloon.dismiss()
            goToSixth(view)
        }, 2650)
    }

    private fun goToSixth(view: View) {
        sixthHelpBalloon.showAlign(
            align = BalloonAlign.BOTTOM,
            mainAnchor = binding.btnRedo as View,
            subAnchorList = listOf(view),
        )

        Handler(Looper.getMainLooper()).postDelayed({
            sixthHelpBalloon.dismiss()
            goToSeventh(view)
        }, 2650)
    }

    private fun goToSeventh(view: View) {
        seventhHelpBalloon.showAlign(
            align = BalloonAlign.BOTTOM,
            mainAnchor = binding.btnRefresh as View,
            subAnchorList = listOf(view),
        )

        Handler(Looper.getMainLooper()).postDelayed({
            seventhHelpBalloon.dismiss()
            var editor = sharedPref.edit()
            editor.putBoolean(AppUtils.START_TUTORIAL_KEY, false)
            editor.apply()
            safeNavController?.safeNavigate(TutorialFragmentDirections
                .actionTutorialFragmentToDrinkFragment())
        }, 2650)
    }

    override fun onResume() {
        super.onResume()

        themeInt = sharedPref.getInt(AppUtils.THEME_KEY,0)
        current_unitInt = sharedPref.getInt(AppUtils.UNIT_KEY,0)
        new_unitInt = sharedPref.getInt(AppUtils.UNIT_NEW_KEY,0)
        setTheme()
        setBackGround()
        val editor = sharedPref.edit()
        editor.putInt(AppUtils.UNIT_KEY,new_unitInt)
        editor.apply()
    }


    private fun setWaterLevel(inTook: Float, totalIntake: Float) {

        YoYo.with(Techniques.SlideInDown)
            .duration(500)
            .playOn(binding.tvIntook)
        binding.tvIntook.text = "" + inTook.toNumberString()
        binding.tvTotalIntake.text = "/"+totalIntake.toNumberString()+ " " + "$unit"
        val progress = ((inTook / totalIntake) * 100).toInt()
        YoYo.with(Techniques.Pulse)
            .duration(500)
            .playOn(binding.intakeProgress)
        binding.intakeProgress.progress = progress.toFloat()
        if(progress <= 140){
            binding.intakeProgress.setOnProgressChangeListener { binding.intakeProgress.labelText = "${
                getString(
                    R.string.drink
                )} ${it.toInt()}%" }
            binding.intakeProgress.labelText = "${
                getString(
                    R.string.drink
                )} ${progress}%"
        }

        if ((inTook * 100 / totalIntake) > 140) {
            showMessage(getString(R.string.you_achieved_the_goal), binding.mainActivityParent)
            sqliteHelper.addReachedGoal(dateNow,inTook,unit)
        }
    }

    private fun setValueForDrinking() {
        unit = AppUtils.calculateExtensions(new_unitInt)
        value_50 = sharedPref.getFloat(AppUtils.VALUE_50_KEY,50f)
        value_100 = sharedPref.getFloat(AppUtils.VALUE_100_KEY,100f)
        value_150 = sharedPref.getFloat(AppUtils.VALUE_150_KEY,150f)
        value_200 = sharedPref.getFloat(AppUtils.VALUE_200_KEY,200f)
        value_250 = sharedPref.getFloat(AppUtils.VALUE_250_KEY,250f)
        value_300 = sharedPref.getFloat(AppUtils.VALUE_300_KEY,300f)
        binding.ml50!!.text = "${value_50.toNumberString()} $unit"
        binding.ml100!!.text = "${value_100.toNumberString()} $unit"
        binding.ml150!!.text = "${value_150.toNumberString()} $unit"
        binding.ml200!!.text = "${value_200.toNumberString()} $unit"
        binding.ml250!!.text = "${value_250.toNumberString()} $unit"
        binding.ml300!!.text = "${value_300.toNumberString()} $unit"
        binding.tvTotalIntake!!.text = "/" +totalIntake.toNumberString() + " " + "$unit"
    }
}