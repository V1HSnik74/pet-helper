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
import com.example.pethelper.compose.patterns.dateParser
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit

class AddNotifNewObjectWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    override fun doWork(): Result {
        val name = inputData.getString("NAME_KEY")
        val id = inputData.getInt("ID_KEY", 0)
        val title = inputData.getString("TITLE_KEY")
        val icon = inputData.getInt("ICON_KEY", 0)
        val petName = inputData.getString("PET_NAME_KEY")
        val notifManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "${title}_channel"
        val channel = NotificationChannel(
            channelId,
            "$title Reminders",
            NotificationManager.IMPORTANCE_HIGH
        )
        notifManager.createNotificationChannel(channel)
        val notif = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(icon)
            .setContentTitle("Upcoming $title")
            .setContentText("$name is soon for $petName!")
            .setAutoCancel(true)
            .build()
        val requestCode = "${title}_$id".hashCode()
        notifManager.notify(requestCode, notif)
        return Result.success()
    }
}

fun scheduleNotif(
    context: Context,
    id: Int,
    name: String,
    date: String,
    notifDate: String,
    notifTime: String,
    title: String,
    icon: Int,
    petName: String
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
        .putInt("ID_KEY", id)
        .putString("NAME_KEY", name)
        .putString("TITLE_KEY", title)
        .putInt("ICON_KEY", icon)
        .putString("PET_NAME_KEY", petName)
        .build()
    val workRequest = OneTimeWorkRequestBuilder<AddNotifNewObjectWorker>()
        .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
        .setInputData(inputData)
        .build()
    WorkManager.getInstance(context).enqueueUniqueWork(
        "${title}_work_$id",
        ExistingWorkPolicy.REPLACE,
        workRequest
    )
}