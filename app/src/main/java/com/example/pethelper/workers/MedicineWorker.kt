package com.example.pethelper.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.pethelper.R
import com.example.pethelper.compose.patterns.timeParser
import com.example.pethelper.db.AppDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.minutes

class MedicineWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val name = inputData.getString("NAME_KEY")
        val id = inputData.getLong("ID_KEY", 0)
        val notifTime = inputData.getLong("NOTIF_TIME_KEY", 0L)
        val petName = inputData.getString("PET_NAME_KEY")
        val stepHours = inputData.getLong("STEP_KEY", 24L)
        val dao = AppDatabase.getInstance(applicationContext).MedicineDao()
        val medicine = dao.getMedicineById(id).firstOrNull() ?: return Result.success()
        val curGivingDT =
            LocalDateTime.parse(medicine.nextTaking, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val notifTargetTime = curGivingDT.minusMinutes(notifTime)
        if (LocalDateTime.now().isBefore(notifTargetTime))
            return Result.success()
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
        val requestCode = "medicine_${id}_$petName".hashCode()
        notifManager.notify(requestCode, notif)
        if (notifTime > 0) {
            delay(notifTime.minutes)
        }
        val nextGivingDT = curGivingDT.plusHours(stepHours)
        dao.updateNextTaking(nextGivingDT.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), id)
        return Result.success()
    }
}

fun scheduleMedicineNotif(
    context: Context,
    id: Long,
    name: String,
    startTime: String,
    intervalTimes: String,
    intervalPeriod: String,
    timeNotif: String,
    petName: String,
    isNotif: Boolean
): LocalDateTime {
    val timeLT = LocalTime.parse(startTime, timeParser)
    val minutesBefore = when (timeNotif) {
        "5 minutes before" -> 5L
        "10 minutes before" -> 10L
        "1 minute before" -> 1L
        else -> 0L
    }
    val stepHours = when (intervalPeriod) {
        "day" -> 12L / intervalTimes.toInt()
        "week" -> (7 * 24L) / intervalTimes.toInt()
        "month" -> (YearMonth.now().lengthOfMonth() * 24L) / intervalTimes.toInt()
        else -> 24L / intervalTimes.toInt()
    }
    var givingDateTime = LocalDateTime.of(LocalDate.now(), timeLT)
    var firstNotifDT = givingDateTime.minusMinutes(minutesBefore)
    while (firstNotifDT.isBefore(LocalDateTime.now())) {
        firstNotifDT = firstNotifDT.plusHours(stepHours)
        givingDateTime = givingDateTime.plusHours(stepHours)
    }
    if (isNotif) {
        val initDelay = java.time.Duration.between(LocalDateTime.now(), firstNotifDT).toMillis()
        val inputData = Data.Builder()
            .putLong("ID_KEY", id)
            .putString("NAME_KEY", name)
            .putLong("NOTIF_TIME_KEY", minutesBefore)
            .putString("PET_NAME_KEY", petName)
            .putLong("STEP_KEY", stepHours)
            .build()
        val workRequest = PeriodicWorkRequestBuilder<MedicineWorker>(
            stepHours, TimeUnit.HOURS
        )
            .setInitialDelay(initDelay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "medicine_${id}_$petName",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
    return givingDateTime
}