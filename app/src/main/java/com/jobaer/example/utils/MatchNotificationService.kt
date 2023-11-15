package com.jobaer.example.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import com.jobaer.example.models.Match
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class MatchNotificationService(
    private val context: Context
) {

    fun scheduleNotification(match: Match): Boolean {
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra(MATCH_NAME, "${match.home} VS ${match.away}")

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            match.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val alarmService = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        utcCalendar.time = dateFormat.parse(match.date!!)!!
        val time = utcCalendar.time.time

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            when {
                // If permission is granted, proceed with scheduling exact alarms.
                alarmService.canScheduleExactAlarms() -> {
                    setAlarm(alarmService, time, pendingIntent)
                }

                else -> {
                    context.startActivity(Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                    return false
                }
            }
        } else {
            setAlarm(alarmService, time, pendingIntent)
        }
        return true

    }

    private fun setAlarm(alarmService: AlarmManager, time: Long, pendingIntent: PendingIntent) {
        try {
            alarmService.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time,
                pendingIntent
            )
        } catch (exception: SecurityException) {
            println(exception)
        }
    }

    companion object {
        const val CHANNEL_ID = "com.jobaer.kickoff"
        const val MATCH_NAME = "matchName"
    }
}