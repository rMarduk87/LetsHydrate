package rpt.tool.mementobibere

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import rpt.tool.mementobibere.databinding.ActivityIntookCalculationBinding

class IntookCalculationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntookCalculationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityIntookCalculationBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}