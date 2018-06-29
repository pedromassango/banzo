package com.pedromassango.banzo.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * Created by Pedro Massango on 6/26/18.
 */
class NoSwipeableViewPager(context: Context,
                           attributeSet: AttributeSet) : ViewPager(context, attributeSet){

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        //return super.onTouchEvent(ev)
        return false
    }



    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        //return super.onInterceptTouchEvent(ev)
        return false
    }
}