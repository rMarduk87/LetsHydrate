package rpt.tool.letshydrate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import rpt.tool.letshydrate.databinding.ActivityMenuNavigationBinding
import rpt.tool.letshydrate.ui.menu.MenuFragmentDirections
import rpt.tool.letshydrate.utils.managers.SharedPreferencesManager
import rpt.tool.letshydrate.utils.navigation.safeNavController
import rpt.tool.letshydrate.utils.navigation.safeNavigate

class MenuNavigationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMenuNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onNavigateUp(): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.menu_activity_nav_host_fragment)
                    as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp()
                || super.onSupportNavigateUp()
    }
}