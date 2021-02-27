package com.dhbw.cas.integra

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Create an intent to open main activity
        val intentNotificationActivity = Intent(context, MainActivity::class.java)
        val pendingIntentNotification: PendingIntent =
            PendingIntent.getActivity(context, 0, intentNotificationActivity, 0)
        // build notification
        val notificationType = intent!!.getStringExtra("ACTION_NOTIFICATION")
        val title: String
        val text: String
        if (notificationType == "PLAN"){
            title = context!!.getString(R.string.notification_title_plan)
            text = context.getString(R.string.notification_text_plan)
        } else {
            title = context!!.getString(R.string.notification_title_exec)
            text = context.getString(R.string.notification_text_exec)
        }
        val builder = NotificationCompat.Builder(context, "INGRA_NOTIF")
            .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(pendingIntentNotification)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        // show notification
        with(NotificationManagerCompat.from(context)) {
            notify(42, builder.build())
        }
    }

}