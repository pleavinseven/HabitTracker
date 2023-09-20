package com.pleavinseven.utils

import android.content.Context
import android.widget.Toast

object Utils {

    fun showToastShort(context: Context, stringRes: Int) {
        Toast.makeText(
            context,
            stringRes,
            Toast.LENGTH_SHORT
        ).show()
    }
}