package com.jobaer.example.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.jobaer.example.R
import com.jobaer.example.ui.MainActivity
import kotlin.random.Random

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            intent?.getStringExtra(MatchNotificationService.MATCH_NAME)?.let { name ->
                showNotification(it, name)
            }
        }
    }

    private fun showNotification(context: Context, content:String) {
        createNotificationChannel(context)
        val activityIntent = Intent(context, MainActivity::class.java)

        val activityPendingIntent = PendingIntent.getActivity(
            context,
            1,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE
        )


        val notification = NotificationCompat.Builder(
            context,
            MatchNotificationService.CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Match whistle blown!")
            .setContentText(content)
            .setContentIntent(activityPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()


        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(Random.nextInt(), notification)
    }
    private fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Kickoff By Zuju"
            val descriptionText = "Football App"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(MatchNotificationService.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}