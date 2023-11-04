package com.pleavinseven.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast

object Utils {

    const val VIBE_EFFECT_DOUBLE_CLICK = 1500L
    const val VIBE_EFFECT_CLICK = 1000L

    fun showToastShort(context: Context, stringRes: Int) {
        Toast.makeText(
            context,
            stringRes,
            Toast.LENGTH_SHORT
        ).show()
    }

    fun vibrate(context: Context, effect: Long) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val vibrationEffect: VibrationEffect =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val vibeEffect = when (effect){
                    1000L -> VibrationEffect.EFFECT_CLICK
                    1500L -> VibrationEffect.EFFECT_DOUBLE_CLICK
                    else -> VibrationEffect.DEFAULT_AMPLITUDE
                }
                VibrationEffect.createPredefined(vibeEffect)
            } else {
                VibrationEffect.createOneShot(effect, VibrationEffect.DEFAULT_AMPLITUDE)
            }
        vibrator.cancel()
        vibrator.vibrate(vibrationEffect)
    }
}