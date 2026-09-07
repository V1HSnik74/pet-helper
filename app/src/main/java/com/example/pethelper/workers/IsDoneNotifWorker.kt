package com.example.pethelper.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.pethelper.R
import com.example.pethelper.compose.patterns.dateParser
import com.example.pethelper.compose.patterns.timeParser
import com.example.pethelper.receivers.MarkAsDoneReceiver
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class IsDoneNotifWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val title = inputData.getString("TITLE_KEY")
        val icon = inputData.getInt("ICON_KEY", 0)
        val contentText = inputData.getString("CONTENT_TEXT_KEY")
        val markAsFun = inputData.getString("FUN_KEY")
        val id = inputData.getInt("ID_KEY", 0)
        val notifManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "${title}_channel"
        val channel = NotificationChannel(
            channelId,
            "$title Reminders",
            NotificationManager.IMPORTANCE_HIGH
        )
        notifManager.createNotificationChannel(channel)
        val intent = Intent(applicationContext, MarkAsDoneReceiver::class.java)
            .apply {
                action = markAsFun
                putExtra("ID_KEY", id)
            }
        val requestCode = "${title}_${id}_done".hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notif = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(icon)
            .setContentTitle("$title Reminder")
            .setContentText(contentText)
            .setAutoCancel(false)
            .setOngoing(true)
            .addAction(
                R.drawable.overall_button,
                "Mark as Done",
                pendingIntent
            )
        notifManager.notify(requestCode, notif.build())
        return Result.success()
    }
}

fun scheduleMarkAsDoneNotif(
    context: Context,
    id: Int,
    date: String,
    title: String,
    icon: Int,
    time: String? = "12:00",
    contentText: String,
    markAsFun: String
) {
    val parsedDate = LocalDate.parse(date, dateParser)
    val parsedTime = LocalTime.parse(time, timeParser)
    val notifDateTime = LocalDateTime.of(parsedDate, parsedTime)
    val targetTimeMillis = notifDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val currentTimeMillis = System.currentTimeMillis()
    val delayMillis = targetTimeMillis - currentTimeMillis
    if (delayMillis <= 0) return
    val inputData = Data.Builder()
        .putInt("ID_KEY", id)
        .putString("TITLE_KEY", title)
        .putInt("ICON_KEY", icon)
        .putString("CONTENT_TEXT_KEY", contentText)
        .putString("FUN_KEY", markAsFun)
        .build()
    val workRequest = OneTimeWorkRequestBuilder<IsDoneNotifWorker>()
        .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
        .setInputData(inputData)
        .build()
    WorkManager.getInstance(context).enqueueUniqueWork(
        "${title}_mark_as_done_work_$id",
        ExistingWorkPolicy.REPLACE,
        workRequest
    )
}