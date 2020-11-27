package com.example.game

import android.graphics.Bitmap
import kotlin.math.atan2
import kotlin.math.roundToInt

class Sprite(
    val bmp: Bitmap,
    var x: Int,
    var y: Int,
    var xSpeed: Int,
    var ySpeed: Int,
    val good: Boolean
) {
    companion object {
        const val BMP_ROWS = 4
        const val BMP_COLUMNS = 3
        val DIRECTION_TO_ANIMATION_MAP = intArrayOf(3, 1, 0, 2) /// up left down right
        const val MAX_SPEED = 10
    }

    val width = bmp.width / BMP_COLUMNS
    val height = bmp.height / BMP_ROWS
    var currentFrame = 0

    fun update(maxWidth: Int, maxHeight: Int) {
        if (x + xSpeed >= maxWidth - width) {
            x = maxWidth - width
            xSpeed = -xSpeed
        } else if (x + xSpeed <= 0) {
            x = 0
            xSpeed = -xSpeed
        }
        x += xSpeed

        if (y + ySpeed >= maxHeight - height) {
            y = maxHeight - height
            ySpeed = -ySpeed
        } else if (y + ySpeed <= 0) {
            y = 0
            ySpeed = -ySpeed
        }
        y += ySpeed
        currentFrame = ++currentFrame % BMP_COLUMNS
    }

    // direction = 0 up, 1 left, 2 down, 3 right
    fun getDirection(): Int {
        val dirDouble = atan2(xSpeed.toDouble(), ySpeed.toDouble()) / (Math.PI / 2) + 2
        return dirDouble.roundToInt() % BMP_ROWS
    }

    fun isCollision(x2: Float, y2: Float): Boolean {
        return x2 > x && x2 < x + width && y2 > y && y2 < y + height
    }

    fun isCollision(sprite: Sprite): Boolean {
        if (x + width < sprite.x ||
            sprite.x + sprite.width < x ||
            y + height < sprite.y ||
            sprite.y + sprite.height < y
        )
            return false
        return true
    }

    fun setSpeed(
        xDirection: Float, yDirection: Float, maxWidth: Float,
        maxHeight: Float
    ) {
        xSpeed = (((xDirection - x) / maxWidth) * MAX_SPEED * 3).roundToInt()
        ySpeed = (((yDirection - y) / maxHeight) * MAX_SPEED * 3).roundToInt()
    }

    fun isCollision(star: Star?): Boolean {
        if (star == null) return false
        if (x + width < star.x ||
            star.x + star.width < x ||
            y + height < star.y ||
            star.y + star.height < y
        )
          return false
        return true
    }


}
