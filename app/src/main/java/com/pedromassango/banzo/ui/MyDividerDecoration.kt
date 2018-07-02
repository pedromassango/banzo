package com.pedromassango.banzo.ui

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import com.pedromassango.banzo.R

/**
 * Created by Pedro Massango on 7/2/18.
 * A class to set as divider item decoration for some RecyclerViews.
 */
class MyDividerDecoration(context: Context): DividerItemDecoration(context, DividerItemDecoration.VERTICAL){
    init {
        setDrawable(
                ResourcesCompat.getDrawable(context.resources, R.drawable.divider, null)!!
        )
    }
}