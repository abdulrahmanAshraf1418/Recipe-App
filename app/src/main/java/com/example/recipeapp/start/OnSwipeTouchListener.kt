package com.example.recipeapp.start

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

open class OnSwipeTouchListener(ctx: Context) : View.OnTouchListener {

    private val gestureDetector = GestureDetector(ctx, GestureListener())

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return true
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val diffX = e2.x - e1!!.x
            if (Math.abs(diffX) > Math.abs(e2.y - e1.y)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight()
                    } else {
                        onSwipeLeft()
                    }
                    return true
                }
            }
            return false
        }
    }

    open fun onSwipeRight() {}
    open fun onSwipeLeft() {}
}
