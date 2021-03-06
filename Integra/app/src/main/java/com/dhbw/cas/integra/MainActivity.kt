package com.dhbw.cas.integra

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.preference.PreferenceManager
import com.dhbw.cas.integra.databinding.ActivityMainBinding
import com.dhbw.cas.integra.ui.MainViewModel
import com.google.android.material.navigation.NavigationView
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // get view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_catalogue, R.id.nav_areas
            ), binding.drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        val navView: NavigationView = binding.navView
        navView.setupWithNavController(navController)

        // create notification channel to be able to send notifications
        createNotificationChannel()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val descriptionText = getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("INGRA_NOTIF", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onStop() {
        super.onStop()
        val notificationType: String = if (mainViewModel.activeSprint != null) {
            "EXECUTE"
        } else {
            "PLAN"
        }

        // check whether notifications are active in settings
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val notifPlanActive =
            preferences.getBoolean(getString(R.string.notifications_planning_pref_key), true)
        val notifExecActive =
            preferences.getBoolean(getString(R.string.notifications_execution_pref_key), true)

        if (notificationType == "PLAN" && !notifPlanActive)
            return
        else if (notificationType == "EXECUTE" && !notifExecActive)
            return

        // Set alarm start time from now
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            this.add(Calendar.SECOND, 10)
        }
        // get alarm manager
        val alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // register alarm receiver
        val receiver = AlarmReceiver()
        val filter = IntentFilter("ALARM_ACTION")
        registerReceiver(receiver, filter)
        //create intent
        val intent = Intent("ALARM_ACTION")
        intent.putExtra("ACTION_NOTIFICATION", notificationType)
        val pendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        // set alaram with specified time and intent
        alarmMgr.cancel(pendingIntent) // cancel existing alarms
        alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_settings) {
            navController.navigate(R.id.nav_settings)
            true
        } else super.onOptionsItemSelected(item)
    }
}