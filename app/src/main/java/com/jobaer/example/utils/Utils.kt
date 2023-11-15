package com.jobaer.example.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object Utils {
    // Function to calculate X days ago from the current date
    fun calculateXDaysAgo(x:Int): Date {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.add(Calendar.DAY_OF_YEAR, -x)
        return calendar.time
    }

    // Function to format a Date to the "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" format
    fun formatDateToISOString(date: Date): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(date)
    }

    //Convert ISO date to Local time and formatted like 'Wednesday, 14 August 2024 at 4:00 AM'
    fun convertDate(inputDate: String?): String {
        if (inputDate.isNullOrEmpty()) return "Date not found"
        // Input date format
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        // Output date format
        val outputFormat = SimpleDateFormat("EEEE, d MMMM yyyy 'at' h:mm a", Locale.US)

        // Parse the input date
        val date = inputFormat.parse(inputDate)

        // Format the date into the desired output format
        return if (date!=null) {
            outputFormat.format(date)
        } else {
            "Unable to convert date"
        }
    }

    //Get current orientation of the device
    fun getScreenOrientation(context: Context): Int {
        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration
        return configuration.orientation
    }

    fun getScreenWidth(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        return displayMetrics.widthPixels

    }
}