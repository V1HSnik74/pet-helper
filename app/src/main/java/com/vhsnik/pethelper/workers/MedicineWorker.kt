package com.vhsnik.pethelper.workers

import android.app.*
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.vhsnik.pethelper.R
import com.vhsnik.pethelper.data.repository.interfaces.HealthObjectRepository
import com.vhsnik.pethelper.ui.compose.utils.*
import dagger.hilt.*
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.firstOrNull
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class MedicineWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            MedicineWorkerEntryPoint::class.java
        )
        val healthObjectRepository = entryPoint.healthObjectRepository()
        val name = inputData.getString("NAME_KEY")
        val id = inputData.getLong("ID_KEY", 0)
        val notifTime = inputData.getLong("NOTIF_TIME_KEY", 0L)
        val petName = inputData.getString("PET_NAME_KEY")
        val animal = inputData.getString("ANIMAL_KEY")
        val isNotif = inputData.getBoolean("IS_NOTIF_KEY", true)
        val intervalPeriod = inputData.getString("PERIOD_KEY")
        val intervalValue = inputData.getString("INTERVAL_VALUE_KEY").orEmpty()
        val startTime = inputData.getString("START_TIME_KEY")
        val intervalTimes = inputData.getString("INTERVAL_TIMES_KEY").orEmpty()
        val curRemaining = inputData.getInt("REMAINING_KEY", intervalTimes.toInt())
        val medicine =
            healthObjectRepository.getHealthObjectById(id).firstOrNull() ?: return Result.success()
        if (isNotif) {
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
                .setSmallIcon(if (animal == "Dog") R.drawable.dog else R.drawable.cat)
                .setContentTitle("Upcoming Medicine")
                .setContentText("$name is soon for $petName")
                .setAutoCancel(true)
                .build()
            val requestCode = "medicine_${id}_$petName".hashCode()
            notifManager.notify(requestCode, notif)
        }

        val startTimeLT = LocalTime.parse(startTime, timeParser)
        val lastGivingDT = LocalDateTime.parse(medicine.nextTaking)
        val (nextGivingDT, updatedRemTimes) = when (intervalPeriod) {
            "week" -> Pair(getNextGivingDT(intervalValue, lastGivingDT, startTimeLT), 0)
            "month" -> Pair(getNextGivingDTForMonth(intervalValue, lastGivingDT, startTimeLT), 0)
            else -> getNextGivingDTForDay(
                lastGivingDT,
                startTimeLT,
                intervalValue.toLong(),
                intervalTimes.toInt(),
                curRemaining
            )
        }
        healthObjectRepository.updateNextTaking(
            nextGivingDT.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            id
        )
        val nextNotifTargetTime = nextGivingDT.minusMinutes(notifTime)
        val delay = Duration.between(LocalDateTime.now(), nextNotifTargetTime).toMillis()
        val nextInputData = Data.Builder()
            .putAll(inputData)
            .putInt("REMAINING_KEY", updatedRemTimes)
            .build()
        if (delay > 0) {
            val nextWorkRequest = OneTimeWorkRequestBuilder<MedicineWorker>()
                .setInitialDelay(maxOf(0L, delay), TimeUnit.MILLISECONDS)
                .setInputData(nextInputData)
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
    intervalTimes: String,
    animal: String
): LocalDateTime {
    val timeLT = LocalTime.parse(startTime, timeParser)
    var givingDateTime = LocalDateTime.of(LocalDate.now(), timeLT)
    var initRemaining = intervalTimes.toInt()
    when (intervalPeriod) {
        "day" -> {
            val stepHours = intervalValue.toLong()
            while (!givingDateTime.isAfter(LocalDateTime.now())) {
                if (initRemaining > 1) {
                    givingDateTime = givingDateTime.plusHours(stepHours)
                    initRemaining--
                } else {
                    givingDateTime =
                        LocalDateTime.of(givingDateTime.toLocalDate().plusDays(1), timeLT)
                    initRemaining = intervalTimes.toInt()
                }
            }
        }

        "week" -> givingDateTime = getNextGivingDT(intervalValue, LocalDateTime.now(), timeLT)
        "month" -> givingDateTime =
            getNextGivingDTForMonth(intervalValue, LocalDateTime.now(), timeLT)

    }
    val minutesBefore = when (timeNotif) {
        "5 minutes before" -> 5L
        "10 minutes before" -> 10L
        "1 minute before" -> 1L
        else -> 0L
    }
    val firstNotifDT = givingDateTime.minusMinutes(minutesBefore)
    val rawDelay = Duration.between(LocalDateTime.now(), firstNotifDT).toMillis()
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
        .putInt("REMAINING_KEY", initRemaining)
        .putString("ANIMAL_KEY", animal)
        .putBoolean("IS_NOTIF_KEY", isNotif)
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

private fun getNextGivingDTForDay(
    lastGivingDT: LocalDateTime,
    startTime: LocalTime,
    stepHours: Long,
    totalTimes: Int,
    curRemaining: Int
): Pair<LocalDateTime, Int> {
    return if (curRemaining > 1)
        Pair(lastGivingDT.plusHours(stepHours), curRemaining - 1)
    else {
        val nextDay = lastGivingDT.toLocalDate().plusDays(1)
        Pair(LocalDateTime.of(nextDay, startTime), totalTimes)
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface MedicineWorkerEntryPoint {
    fun healthObjectRepository(): HealthObjectRepository
}

fun cancelMedicineNotif(context: Context, id: Long, petName: String) {
    WorkManager.getInstance(context).cancelUniqueWork("medicine_${id}_$petName")
}