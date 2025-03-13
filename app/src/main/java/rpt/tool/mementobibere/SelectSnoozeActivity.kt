package rpt.tool.mementobibere

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.widget.RelativeLayout
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import rpt.tool.mementobibere.databinding.ActivitySelectSnoozeBinding
import rpt.tool.mementobibere.utils.newnotifications.NewAlarmReceiver
import java.util.Calendar
import java.util.Locale

class SelectSnoozeActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySelectSnoozeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setLayout(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT)

        binding = ActivitySelectSnoozeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.cancel(0)

        body()
    }

    @SuppressLint("SetTextI18n", "ScheduleExactAlarm")
    private fun body() {
        binding.one.text = "5 " + getString(R.string.str_minutes)
        binding.two.text = "10 " + getString(R.string.str_minutes)
        binding.three.text = "15 " + getString(R.string.str_minutes)

        binding.one.setOnClickListener {
            setSnooze(5)
            finish()
        }

        binding.two.setOnClickListener {
            setSnooze(10)
            finish()
        }

        binding.three.setOnClickListener {
            setSnooze(15)
            finish()
        }
    }

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    @SuppressLint("ServiceCast", "ScheduleExactAlarm")
    private fun setSnooze(minutes: Int) {
        val snoozeIntent: Intent = Intent(this, NewAlarmReceiver::class.java)
        val snoozePendingIntent = PendingIntent.getBroadcast(this, 99, snoozeIntent,
            PendingIntent.FLAG_IMMUTABLE)
        (getSystemService(ALARM_SERVICE) as AlarmManager)
            .setExact(
                AlarmManager.RTC_WAKEUP, Calendar.getInstance(
                    Locale.US

                ).timeInMillis + minutes * 60000, snoozePendingIntent
            )
    }
}