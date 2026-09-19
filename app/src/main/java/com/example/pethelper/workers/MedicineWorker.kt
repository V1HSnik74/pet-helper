package com.example.pethelper.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.pethelper.R
import com.example.pethelper.compose.patterns.timeParser
import com.example.pethelper.db.AppDatabase
import kotlinx.coroutines.flow.firstOrNull
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class MedicineWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val name = inputData.getString("NAME_KEY")
        val id = inputData.getLong("ID_KEY", 0)
        val notifTime = inputData.getLong("NOTIF_TIME_KEY", 0L)
        val petName = inputData.getString("PET_NAME_KEY")
        val intervalPeriod = inputData.getString("PERIOD_KEY")
        val intervalValue = inputData.getString("INTERVAL_VALUE_KEY")
        val startTime = inputData.getString("START_TIME_KEY")
        val intervalTimes = inputData.getString("INTERVAL_TIMES_KEY")
        val dao = AppDatabase.getInstance(applicationContext).MedicineDao()
        val medicine = dao.getMedicineById(id).firstOrNull() ?: return Result.success()

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
            .setSmallIcon(R.drawable.medicine_button)
            .setContentTitle("Upcoming Medicine")
            .setContentText("$name is soon for $petName")
            .setAutoCancel(true)
            .build()
        val requestCode = "medicine_${id}_$petName".hashCode()
        notifManager.notify(requestCode, notif)

        val startTimeLT = LocalTime.parse(startTime, timeParser)
        val nextGivingDT = when (intervalPeriod) {
            "week" -> getNextGivingDT(intervalValue!!, LocalDateTime.now(), startTimeLT)
            "month" -> getNextGivingDT(intervalValue!!, LocalDateTime.now(), startTimeLT)
            else -> {
                var remainingTimes = intervalTimes!!.toInt() - 1
                val stepHours = intervalValue!!.toLong()
                var givingDateTime = LocalDateTime.parse(medicine.nextTaking)
                while (!givingDateTime.isAfter(LocalDateTime.now())) {
                    if (remainingTimes > 0) {
                        givingDateTime = givingDateTime.plusHours(stepHours)
                        remainingTimes -= 1
                    } else {
                        givingDateTime = LocalDateTime.of(LocalDate.now().plusDays(1), startTimeLT)
                        remainingTimes = intervalTimes.toInt() - 1
                    }
                }
                givingDateTime
            }
        }
        dao.updateNextTaking(nextGivingDT.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), id)
        val nextNotifTargetTime = nextGivingDT.minusMinutes(notifTime)
        val delay = java.time.Duration.between(LocalDateTime.now(), nextNotifTargetTime).toMillis()
        if (delay > 0) {
            val nextWorkRequest = OneTimeWorkRequestBuilder<MedicineWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .build()
            WorkManager.getInstance(applicationContext).enqueueUniqueWork(
                "medicine_${id}_$petName",
                ExistingWorkPolicy.REPLACE,
                nextWorkRequest
            )
        }
        return Result.success()
    }
}

fun scheduleMedicineNotif(
    context: Context,
    id: Long,
    name: String,
    startTime: String,
    intervalPeriod: String,
    timeNotif: String,
    intervalValue: String,
    petName: String,
    isNotif: Boolean,
    intervalTimes: String
): LocalDateTime {
    val timeLT = LocalTime.parse(startTime, timeParser)
    var givingDateTime = LocalDateTime.of(LocalDate.now(), timeLT)
    when (intervalPeriod) {
        "day" -> {
            var remainingTimes = intervalTimes.toInt() - 1
            val stepHours = intervalValue.toLong()
            while (!givingDateTime.isAfter(LocalDateTime.now())) {
                if (remainingTimes > 0) {
                    givingDateTime = givingDateTime.plusHours(stepHours)
                    remainingTimes -= 1
                } else {
                    givingDateTime = LocalDateTime.of(LocalDate.now().plusDays(1), timeLT)
                    remainingTimes = intervalTimes.toInt() - 1
                }
            }
        }

        "week" -> {
            givingDateTime = getNextGivingDT(intervalValue, LocalDateTime.now(), timeLT)
        }

        "month" -> {
            givingDateTime = getNextGivingDTForMonth(intervalValue, LocalDateTime.now(), timeLT)
        }
    }
    if (isNotif) {
        val minutesBefore = when (timeNotif) {
            "5 minutes before" -> 5L
            "10 minutes before" -> 10L
            "1 minute before" -> 1L
            else -> 0L
        }
        val firstNotifDT = givingDateTime.minusMinutes(minutesBefore)
        val rawDelay = java.time.Duration.between(LocalDateTime.now(), firstNotifDT).toMillis()
        val initDelay = maxOf(0L, rawDelay)
        val inputData = Data.Builder()
            .putLong("ID_KEY", id)
            .putString("NAME_KEY", name)
            .putLong("NOTIF_TIME_KEY", minutesBefore)
            .putString("PERIOD_KEY", intervalPeriod)
            .putString("INTERVAL_VALUE_KEY", intervalValue)
            .putString("START_TIME_KEY", startTime)
            .putString("PET_NAME_KEY", petName)
            .putString("INTERVAL_TIMES_KEY", intervalTimes)
            .build()
        val workRequest = OneTimeWorkRequestBuilder<MedicineWorker>()
            .setInitialDelay(initDelay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "medicine_${id}_$petName",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
    return givingDateTime
}

private fun getNextGivingDT(
    intervalValue: String,
    fromDT: LocalDateTime,
    startTime: LocalTime
): LocalDateTime {
    val daysMap = mapOf(
        "Monday" to DayOfWeek.MONDAY,
        "Tuesday" to DayOfWeek.TUESDAY,
        "Wednesday" to DayOfWeek.WEDNESDAY,
        "Thursday" to DayOfWeek.THURSDAY,
        "Friday" to DayOfWeek.FRIDAY,
        "Saturday" to DayOfWeek.SATURDAY,
        "Sunday" to DayOfWeek.SUNDAY
    )
    val selectedDays = intervalValue.split(",").map { daysMap[it] }
    var candidate = LocalDateTime.of(fromDT.toLocalDate(), startTime)
    while (
        !candidate.isAfter(fromDT) ||
        !selectedDays.contains(candidate.dayOfWeek)
    ) {
        candidate = candidate.plusDays(1)
    }
    return candidate
}

private fun getNextGivingDTForMonth(
    intervalValue: String,
    fromDT: LocalDateTime,
    startTime: LocalTime
): LocalDateTime {
    val selectedDays = intervalValue.split(",").map { it.toInt() }
    var candidateDate = fromDT.toLocalDate()
    while (true) {
        val dayOfMonth = candidateDate.dayOfMonth
        if (dayOfMonth in selectedDays) {
            val candidateDT = candidateDate.atTime(startTime)
            if (candidateDT.isAfter(fromDT)) {
                return candidateDT
            }
        }
        candidateDate = candidateDate.plusDays(1)
    }
}