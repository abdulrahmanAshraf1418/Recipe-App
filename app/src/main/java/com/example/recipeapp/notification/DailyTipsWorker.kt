package com.example.recipeapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.recipeapp.R
import com.example.recipeapp.ui.RecipeActivity

class DailyTipsWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        if (!NotificationPrefs.isEnabled || !NotificationPrefs.dailyTips) {
            return Result.success()
        }

        showNotification(
            title = "Daily Meal Tip",
            text = getRandomTip()
        )

        return Result.success()
    }

    private fun showNotification(title: String, text: String) {
        val channelId = "daily_tips_channel"
        val nm = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Daily Tips",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            nm.createNotificationChannel(channel)
        }

        val intent = Intent(applicationContext, RecipeActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        nm.notify((System.currentTimeMillis() % 10000).toInt(), notification)
    }

    private fun getRandomTip(): String {
        val tips = listOf(
            "Try adding lemon to your salad for a fresh kick.",
            "Use leftover rice to make fried rice with veggies.",
            "Spice up your meal with a pinch of smoked paprika."
        )
        return tips.random()
    }
}
