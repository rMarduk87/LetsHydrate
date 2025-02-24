package rpt.tool.letshydrate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import rpt.tool.letshydrate.databinding.ActivityInitUserInfoBinding

class InitUserInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInitUserInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityInitUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onNavigateUp(): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.init_user_info_activity_nav_host_fragment)
                    as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp()
                || super.onSupportNavigateUp()
    }

}