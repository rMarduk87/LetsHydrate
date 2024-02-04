package rpt.tool.mementobibere

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import rpt.tool.mementobibere.databinding.ActivityInitUserInfoBinding
import rpt.tool.mementobibere.databinding.ActivityInitUserInfoMissedBinding
import rpt.tool.mementobibere.utils.extensions.toAppTheme
import rpt.tool.mementobibere.utils.managers.SharedPreferencesManager

class InitUserInfoMissedActivity : LetsHydrateBaseActivity() {
    private lateinit var binding: ActivityInitUserInfoMissedBinding
    private var themeInt : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        themeInt = SharedPreferencesManager.themeInt
        setTheme()
        super.onCreate(savedInstanceState)
        binding = ActivityInitUserInfoMissedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        modifyOrentation()
    }

    private fun setTheme() {
        val theme = themeInt.toAppTheme()
        setTheme(theme)
    }

    override fun onNavigateUp(): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.init_user_missed_activity_nav_host_fragment)
                    as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp()
                || super.onSupportNavigateUp()
    }
}