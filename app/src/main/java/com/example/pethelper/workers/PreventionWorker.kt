package com.example.pethelper.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.pethelper.R
import com.example.pethelper.compose.dateParser
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit

class PreventionWorker(context: Context,
    params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val actionName = inputData.getString("ACTION")
        val preventionId = inputData.getInt("PREVENTION_ID", 0)
        val notifManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "prevention_channel"
        val channel = NotificationChannel(channelId, "Prevention Reminders", NotificationManager.IMPORTANCE_HIGH)
        notifManager.createNotificationChannel(channel)
        val notif = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.parasites_dialog)
            .setContentTitle("Upcoming Prevention")
            .setContentText("$actionName is soon!")
            .setAutoCancel(true)
            .build()
        notifManager.notify(preventionId, notif)
        return Result.success()
    }
}

fun schedulePreventionNotif(
    context: Context,
    preventionId: Int,
    action: String,
    date: String,
    notifDate: String,
    notifTime: String
) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH)
    val parsedDate = LocalDate.parse(date, dateParser)
    val parsedTime = LocalTime.parse(notifTime, timeFormatter)
    val daysBefore = when (notifDate) {
        "1 day before" -> 1L
        "2 days before" -> 2L
        "3 days before" -> 3L
        "1 week before" -> 7L
        else -> 0L
    }
    val notifDateTime = LocalDateTime.of(parsedDate.minusDays(daysBefore), parsedTime)
    val targetTimeMillis = notifDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val currentTimeMillis = System.currentTimeMillis()
    val delayMillis = targetTimeMillis - currentTimeMillis
    if (delayMillis <= 0) return
    val inputData = Data.Builder()
        .putInt("PREVENTION_ID", preventionId)
        .putString("ACTION", action)
        .build()
    val workRequest = OneTimeWorkRequestBuilder<VaccineWorker>()
        .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
        .setInputData(inputData)
        .build()
    WorkManager.getInstance(context).enqueueUniqueWork(
        "prevention_work_$preventionId",
        ExistingWorkPolicy.REPLACE,
        workRequest
    )
}