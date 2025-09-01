package com.example.recipeapp.notification

import android.content.Context
import android.content.SharedPreferences

object NotificationPrefs {
    private const val PREFS_NAME = "notification_prefs"
    private const val KEY_ENABLED = "notifications_enabled"
    private const val KEY_DAILY = "notifications_daily"
    private const val KEY_NEW_RECIPES = "notifications_new_recipes"
    private const val KEY_OFFERS = "notifications_offers"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var isEnabled: Boolean
        get() = prefs.getBoolean(KEY_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_ENABLED, value).apply()

    var dailyTips: Boolean
        get() = prefs.getBoolean(KEY_DAILY, true)
        set(value) = prefs.edit().putBoolean(KEY_DAILY, value).apply()

    var newRecipes: Boolean
        get() = prefs.getBoolean(KEY_NEW_RECIPES, true)
        set(value) = prefs.edit().putBoolean(KEY_NEW_RECIPES, value).apply()

    var offers: Boolean
        get() = prefs.getBoolean(KEY_OFFERS, false)
        set(value) = prefs.edit().putBoolean(KEY_OFFERS, value).apply()
}
