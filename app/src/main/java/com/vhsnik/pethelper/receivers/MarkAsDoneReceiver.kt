package com.vhsnik.pethelper.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.vhsnik.pethelper.data.repository.interfaces.HealthObjectRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MarkAsDoneReceiver : BroadcastReceiver() {
    @Inject
    lateinit var healthObjectRepository: HealthObjectRepository

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getLongExtra("ID_KEY", -1)
        val notifId = intent.getIntExtra("NOTIF_ID_KEY", -1)
        val notifManager = NotificationManagerCompat.from(context)
        notifManager.cancel(notifId)
        CoroutineScope(Dispatchers.IO).launch {
            healthObjectRepository.markAsDone(id)
        }
    }
}