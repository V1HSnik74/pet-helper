package com.example.pethelper.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.example.pethelper.db.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MarkAsDoneReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra("ID_KEY", -1)
        val notifId = intent.getIntExtra("NOTIF_ID_KEY", -1)
        val notifManager = NotificationManagerCompat.from(context)
        notifManager.cancel(notifId)
        val db = AppDatabase.getInstance(context)
        CoroutineScope(Dispatchers.IO).launch {
            when (intent.action) {
                "vaccineDone" -> db.VaccineDao().markVaccineAsDone(id)
                "preventionDone" -> db.PreventionDao().markPreventionAsDone(id)
                "checkUpDone" -> db.CheckUpDao().markCheckUpAsDone(id)
            }
        }
    }
}