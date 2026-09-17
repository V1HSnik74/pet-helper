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
import com.example.pethelper.compose.patterns.timeParser
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class MedicineWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    override fun doWork(): Result {
        val name = inputData.getString("NAME_KEY")
        val id = inputData.getLong("ID_KEY", 0)
        val time = inputData.getString("TIME_KEY")
        val petName = inputData.getString("PET_NAME_KEY")
        val notifManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "medicine_channel"
        val channel =
            NotificationChannel(
                channelId,
                "Medicine Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
        notifManager.createNotificationChannel(channel)
        val notif = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.supplement_icon)
            .setContentTitle("Upcoming Medicine")
            .setContentText("$name is soon for $petName")
            .setAutoCancel(true)
            .build()
        val requestCode = "${id}_medicine_$time".hashCode()
        notifManager.notify(requestCode, notif)
        return Result.success()
    }
}

fun scheduleMedicineNotif (
    context: Context,
    id: Long,
    name: String,
    startTime: String,
    intervalTimes: String,
    intervalPeriod: String,
    timeNotif: String,
    petName: String,
    isNotif: Boolean
) : LocalDateTime {
    val timeLT = LocalTime.parse(startTime, timeParser)
    val curDate = LocalDate.now()
    val minutesBefore = when (timeNotif) {
        "1 hour before" -> 60L
        "5 minutes before" -> 5L
        "10 minutes before" -> 10L
        "30 minutes before" -> 30L
        else -> 0L
    }
    val scheduledDateTimes = mutableListOf<LocalDateTime>()
    when (intervalPeriod) {
        "day" -> {
            val scheduledIntervals = calculateNotifTimesForDay(timeLT, intervalTimes.toInt())
            scheduledIntervals.forEachIndexed { index, time ->
                val timeLT = LocalTime.parse(time, timeParser)
                var targetDateTime = LocalDateTime.of(curDate, timeLT).minusMinutes(minutesBefore)
                while (targetDateTime.isBefore(LocalDateTime.now()))
                    targetDateTime = targetDateTime.plusDays(1)
                scheduledDateTimes.add(targetDateTime.plusMinutes(minutesBefore))
                if (isNotif) enqueueRequest(context, id, name, time, petName, targetDateTime, index)
            }
        }

        "week" -> {
            val daysStep = (7 / intervalTimes.toInt()).toLong()
            (0 until intervalTimes.toInt()).forEach {
                val targetDate = curDate.plusDays(it * daysStep)
                var targetDateTime =
                    LocalDateTime.of(targetDate, timeLT).minusMinutes(minutesBefore)
                while (targetDateTime.isBefore(LocalDateTime.now()))
                    targetDateTime = targetDateTime.plusWeeks(1)
                scheduledDateTimes.add(targetDateTime.plusMinutes(minutesBefore))
                if (isNotif) enqueueRequest(context, id, name, startTime, petName, targetDateTime, it)
            }
        }

        "month" -> {
            val daysStep =
                (YearMonth.now().lengthOfMonth() / intervalTimes.toInt()).toLong()
            (0 until intervalTimes.toInt()).forEach {
                val targetDate = curDate.plusDays(it * daysStep)
                var targetDateTime =
                    LocalDateTime.of(targetDate, timeLT).minusMinutes(minutesBefore)
                while (targetDateTime.isBefore(LocalDateTime.now()))
                    targetDateTime = targetDateTime.plusMonths(1)
                scheduledDateTimes.add(targetDateTime.plusMinutes(minutesBefore))
                if (isNotif) enqueueRequest(context, id, name, startTime, petName, targetDateTime, it)
            }
        }
    }
   return scheduledDateTimes.min()
}

private fun enqueueRequest(
    context: Context,
    id: Long,
    name: String,
    time: String,
    petName: String,
    targetDateTime: LocalDateTime,
    index: Int
) {
    val targetTimeMillis =
        targetDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val curTimeMillis = System.currentTimeMillis()
    val delayMillis = targetTimeMillis - curTimeMillis
    if (delayMillis > 0) {
        val inputData = Data.Builder()
            .putLong("ID_KEY", id)
            .putString("NAME_KEY", name)
            .putString("TIME_KEY", time)
            .putString("PET_NAME_KEY", petName)
            .build()
        val workRequest = OneTimeWorkRequestBuilder<MedicineWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "medicine_work_${id}_time_$index",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
}


private fun calculateNotifTimesForDay(
    startTime: LocalTime,
    intervalTimes: Int
): List<String> {
    if (intervalTimes == 1)
        return listOf(startTime.format(timeParser))
    val intervals = (24 / intervalTimes).toLong()
    return (0 until intervalTimes).map {
        startTime.plusHours(intervals * it).format(timeParser)
    }
}