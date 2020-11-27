package com.example.game

import android.view.SurfaceHolder

interface GameControllerInterface : SurfaceHolder.Callback {
    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int,
                                height: Int) {
    }
    fun clickHere(x: Float, y: Float)
}