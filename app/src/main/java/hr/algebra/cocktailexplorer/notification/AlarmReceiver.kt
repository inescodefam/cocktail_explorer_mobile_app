package hr.algebra.cocktailexplorer.notification

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import hr.algebra.cocktailexplorer.HostActivity
import hr.algebra.cocktailexplorer.R

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_SHOW_REMINDER = "hr.algebra.cocktailexplorer.ACTION_SHOW_REMINDER"
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_SHOW_REMINDER -> {
                val cocktailName = intent.getStringExtra(NotificationHelper.EXTRA_COCKTAIL_NAME) ?: "a cocktail"
                val cocktailId = intent.getIntExtra(NotificationHelper.EXTRA_COCKTAIL_ID, -1)
                showReminderNotification(context, cocktailName, cocktailId)
            }
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showReminderNotification(context: Context, cocktailName: String, cocktailId: Int) {

        val notificationHelper = NotificationHelper(context)
        notificationHelper.createNotificationChannel()

        if (!notificationHelper.hasNotificationPermission()) {
            return
        }

        val intent = Intent(context, HostActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(NotificationHelper.EXTRA_COCKTAIL_ID, cocktailId)
            putExtra(NotificationHelper.EXTRA_NOTIFICATION_TYPE, NotificationHelper.TYPE_REMINDER)
        }


        val pendingIntent = PendingIntent.getActivity(
            context,
            cocktailId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_cocktail)
            .setContentTitle(context.getString(R.string.notification_reminder_title))
            .setContentText(context.getString(R.string.notification_reminder_message, cocktailName))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(
            NotificationHelper.NOTIFICATION_ID_REMINDER + cocktailId,
            notification
        )
    }
}
