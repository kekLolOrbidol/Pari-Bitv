package com.example.game

import android.graphics.Bitmap

class Star(
    val bmp: Bitmap,
    var x: Int,
    var y: Int,
    private val xSpeed: Int,
    private val ySpeed: Int
) {
    val width = bmp.width
    val height = bmp.height
    var visible = true
    fun update(maxWidth: Int, maxHeight: Int) {
        if (x + xSpeed >= maxWidth - width || x + xSpeed <= 0) {
            visible = false
        }
        x += xSpeed
        if (y + ySpeed >= maxHeight - height || y + ySpeed <= 0) {
            visible = false
        }
        y += ySpeed
    }
}
