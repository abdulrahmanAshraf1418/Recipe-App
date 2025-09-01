package com.example.recipeapp.notification

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.recipeapp.R
import com.google.firebase.messaging.FirebaseMessaging
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NotificationsFragment : Fragment() {
    private lateinit var switchGeneral: SwitchCompat
    private lateinit var switchDaily: SwitchCompat
    private lateinit var switchNewRecipes: SwitchCompat
    private lateinit var switchOffers: SwitchCompat

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_notifications, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        switchGeneral = view.findViewById(R.id.switch_general)
        switchDaily = view.findViewById(R.id.switch_daily_tips)
        switchNewRecipes = view.findViewById(R.id.switch_new_recipes)
        switchOffers = view.findViewById(R.id.switch_special_offers)

        switchGeneral.isChecked = NotificationPrefs.isEnabled
        switchDaily.isChecked = NotificationPrefs.dailyTips
        switchNewRecipes.isChecked = NotificationPrefs.newRecipes
        switchOffers.isChecked = NotificationPrefs.offers

        switchGeneral.setOnCheckedChangeListener { _, checked ->
            NotificationPrefs.isEnabled = checked
            if (!checked) {
                cancelAllNotifications()
                unsubscribeAllTopics()
            } else {
                if (NotificationPrefs.dailyTips) scheduleDailyTipsWork()
                if (NotificationPrefs.newRecipes) subscribeToTopic("new_recipes")
                if (NotificationPrefs.offers) subscribeToTopic("special_offers")
            }
            updateUIEnabledState(checked)
        }

        switchDaily.setOnCheckedChangeListener { _, checked ->
            NotificationPrefs.dailyTips = checked
            if (checked && NotificationPrefs.isEnabled) {
                scheduleDailyTipsWork()
            } else {
                cancelDailyTipsWork()
            }
        }

        switchNewRecipes.setOnCheckedChangeListener { _, checked ->
            NotificationPrefs.newRecipes = checked
            if (checked && NotificationPrefs.isEnabled) subscribeToTopic("new_recipes")
            else unsubscribeFromTopic("new_recipes")
        }

        switchOffers.setOnCheckedChangeListener { _, checked ->
            NotificationPrefs.offers = checked
            if (checked && NotificationPrefs.isEnabled) subscribeToTopic("special_offers")
            else unsubscribeFromTopic("special_offers")
        }

        requestNotificationPermissionIfNeeded()
        updateUIEnabledState(NotificationPrefs.isEnabled)
    }

    private fun updateUIEnabledState(enabled: Boolean) {
        val alpha = if (enabled) 1f else 0.5f
        switchDaily.alpha = alpha
        switchNewRecipes.alpha = alpha
        switchOffers.alpha = alpha

        switchDaily.isEnabled = enabled
        switchNewRecipes.isEnabled = enabled
        switchOffers.isEnabled = enabled
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }

    private fun subscribeToTopic(topic: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
    }

    private fun unsubscribeFromTopic(topic: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
    }

    private fun unsubscribeAllTopics() {
        unsubscribeFromTopic("new_recipes")
        unsubscribeFromTopic("special_offers")
    }

    private fun scheduleDailyTipsWork() {
        val work = PeriodicWorkRequestBuilder<DailyTipsWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "daily_tips_work", ExistingPeriodicWorkPolicy.REPLACE, work
        )
    }

    private fun cancelDailyTipsWork() {
        WorkManager.getInstance(requireContext()).cancelUniqueWork("daily_tips_work")
    }

    private fun cancelAllNotifications() {
        cancelDailyTipsWork()
        unsubscribeAllTopics()
    }

    private fun calculateInitialDelay(): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        if (now.after(target)) target.add(Calendar.DAY_OF_YEAR, 1)
        return target.timeInMillis - now.timeInMillis
    }
}
