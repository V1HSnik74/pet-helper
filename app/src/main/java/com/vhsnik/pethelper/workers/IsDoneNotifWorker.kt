package com.vhsnik.pethelper.workers

import android.app.*
import android.content.*
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.vhsnik.pethelper.R
import com.vhsnik.pethelper.receivers.MarkAsDoneReceiver
import com.vhsnik.pethelper.ui.compose.utils.timeParser
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit

class IsDoneNotifWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val title = inputData.getString("TITLE_KEY")
        val icon = inputData.getInt("ICON_KEY", 0)
        val contentText = inputData.getString("CONTENT_TEXT_KEY")
        val id = inputData.getLong("ID_KEY", 0)
        val notifManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "${title}_channel"
        val channel = NotificationChannel(
            channelId,
            "$title Reminders",
            NotificationManager.IMPORTANCE_HIGH
        )
        notifManager.createNotificationChannel(channel)
        val requestCode = "${title}_${id}_done".hashCode()
        val intent = Intent(applicationContext, MarkAsDoneReceiver::class.java)
            .apply {
                putExtra("ID_KEY", id)
                putExtra("NOTIF_ID_KEY", requestCode)
            }
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
    id: Long,
    date: String,
    title: String,
    icon: Int,
    time: String? = "12:00",
    contentText: String
) {
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)
    val parsedDate = LocalDate.parse(date, dateFormatter)
    val parsedTime = LocalTime.parse(time, timeParser)
    val notifDateTime = LocalDateTime.of(parsedDate, parsedTime)
    val targetTimeMillis = notifDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val currentTimeMillis = System.currentTimeMillis()
    val delayMillis = targetTimeMillis - currentTimeMillis
    if (delayMillis <= 0) return
    val inputData = Data.Builder()
        .putLong("ID_KEY", id)
        .putString("TITLE_KEY", title)
        .putInt("ICON_KEY", icon)
        .putString("CONTENT_TEXT_KEY", contentText)
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