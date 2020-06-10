package com.shid.clipboardmanagerkt.Utils

import android.graphics.Interpolator

class BounceInterpolator constructor(val mAmplitude: Double = 0.4, val mFrequency: Double = 22.0) :
    android.view.animation.Interpolator {
    private var result: Double? = null

    override fun getInterpolation(time: Float): Float {
        return  (-1 * Math.pow(Math.E, -time / mAmplitude) * Math.cos(mFrequency * time) + 1).toFloat()
    }
}