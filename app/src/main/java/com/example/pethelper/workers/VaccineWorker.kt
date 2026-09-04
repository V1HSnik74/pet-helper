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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit

class VaccineWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val vaccineName = inputData.getString("VACCINE")
        val vaccineId = inputData.getInt("VACCINE_ID", 0)
        val notifManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "vaccine_channel"
        val channel = NotificationChannel(channelId, "Vaccine Reminders", NotificationManager.IMPORTANCE_HIGH)
        notifManager.createNotificationChannel(channel)
        val notif = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.vaccine_dialog)
            .setContentTitle("Upcoming Vaccination")
            .setContentText("$vaccineName is soon!")
            .setAutoCancel(true)
            .build()
        notifManager.notify(vaccineId, notif)
        return Result.success()
    }
}

fun scheduleVaccineNotif(
    context: Context,
    vaccineId: Int,
    vaccineName: String,
    date: String,
    notifDate: String,
    notifTime: String
){
    val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH)
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH)
    val parsedDate = LocalDate.parse(date, dateFormatter)
    val parsedTime = LocalTime.parse(notifTime, timeFormatter)
    val daysBefore = when(notifDate) {
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
        .putInt("VACCINE_ID", vaccineId)
        .putString("VACCINE", vaccineName)
        .build()
    val workRequest = OneTimeWorkRequestBuilder<VaccineWorker>()
        .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
        .setInputData(inputData)
        .build()
    WorkManager.getInstance(context).enqueueUniqueWork(
        "vaccine_work_$vaccineId",
        ExistingWorkPolicy.REPLACE,
        workRequest
    )
}