package com.example.recipeapp.ui

import android.app.Application
import com.example.recipeapp.notification.NotificationPrefs

class RecipeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationPrefs.init(this)
    }
}