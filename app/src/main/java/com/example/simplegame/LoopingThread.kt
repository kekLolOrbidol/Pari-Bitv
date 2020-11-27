package com.example.simplegame

class LoopingThread(private val action: () -> Unit) : Thread() {
  private val fps = 10
  private val tick = 1000 / fps
  private var looping = false
  override fun run() {
    while (looping) {
      val startTime = System.currentTimeMillis()
      action.invoke()
      val sleepTime = tick - (System.currentTimeMillis() - startTime)
      try {
        if (sleepTime > 0) {
          sleep(sleepTime)
        } else {
          sleep(10)
        }
      } catch (ignore: Exception) {
      }
    }
  }
  fun startLoop() {
    if (looping) return
    looping = true
    start()
  }
  fun stopLoop() {
    if (!looping) return
    var retry = false
    looping = false
    while (retry) {
      try {
        join()
        retry = false
      } catch (ignore: InterruptedException) {
      }
    }
  }
}
