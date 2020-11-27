package com.example.simplegame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import android.view.SurfaceView
import com.example.game.GameControllerInterface


class GameView(context : Context) : SurfaceView(context) {
  private var lastClick = 0L
  private var gameControllerCallback: GameControllerInterface? = null

  fun addCallback(callback: GameControllerInterface?) {
    gameControllerCallback = callback
    holder.addCallback(callback)
  }
  fun drawInHolder(call: (Canvas) -> Unit) {
    var c: Canvas? = null
    try {
      c = holder.lockCanvas()
      if(c != null) call(c)
    } finally {
      c?.let { holder.unlockCanvasAndPost(it) }
    }
  }
  override fun onTouchEvent(event: MotionEvent?): Boolean {
    if (event == null) return super.onTouchEvent(event)
    if (System.currentTimeMillis() - lastClick > 500) {
      lastClick = System.currentTimeMillis()
      gameControllerCallback?.clickHere(event.x,event.y)
    }
    return super.onTouchEvent(event)
  }
  override fun onDraw(canvas: Canvas) { // A Simple Text Render to test the display
  }
}
