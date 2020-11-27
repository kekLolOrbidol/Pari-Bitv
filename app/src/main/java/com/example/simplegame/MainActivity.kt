package com.example.simplegame

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.GestureDetector
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_VOLUME_DOWN
import android.view.SurfaceHolder
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.example.data.WebApi
import com.example.game.*
import com.google.gson.Gson
import java.util.*


class MainActivity : AppCompatActivity(), GameControllerInterface {
  private lateinit var gameView: GameView
  private var loopingThread: LoopingThread? = null

  private lateinit var picture: Bitmap
  private lateinit var gameState : GameState
  private lateinit var gameStateAdapter: GameStateAdapter

  private lateinit var mGestureDetector : GestureDetector
  private lateinit var soundPlayer: SoundPlayer
    private lateinit var dialogWin : AlertDialog
    private lateinit var dialogLose : AlertDialog
    private var count = 0
    private var gson : Gson = Gson()
    private var startTime : Long = 0
    private var resultTime : Long = 0

    var flag = false

    var flag2 = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    gameView = GameView(this)
    gameView.addCallback(this)

    soundPlayer = SoundPlayer(applicationContext)
    gameState = GameState(soundPlayer)

    setContentView(gameView)

      val t = Timer()
      t.scheduleAtFixedRate(object : TimerTask() {
          override fun run() {
              runOnUiThread {
                  count++
              }
          }
      }, 10, 1000)

      val builderWin = AlertDialog.Builder(this)
      val textWin = EditText(this);

      builderWin.setMessage("Give your name").setView(textWin)
      builderWin.setPositiveButton("Ok", DialogInterface.OnClickListener(){
              _, _ ->
          val sharedPref = this.getSharedPreferences("scores.xml",Context.MODE_PRIVATE)
          val textSet = sharedPref.getString(textWin.text.toString(),"0")
          if(!textSet.equals("0")){
              Toast.makeText(
                  this, "The given user is already in the ranking - enter a different name",
                  Toast.LENGTH_LONG
              ).show()
          }
          else {
              sharedPref.edit().putString(textWin.text.toString(), resultTime.toString()).apply()
              finish()
              startActivity(Intent(applicationContext, GameRankActivity::class.java))
          }
      })
      dialogWin = builderWin.create()


      val builderLose = AlertDialog.Builder(this)

      builderLose.setTitle("Unfortunately!")
          .setMessage("This time it failed")
      builderLose.setPositiveButton("Try again", DialogInterface.OnClickListener(){
              _, _ ->
          finish()
          startActivity(Intent(applicationContext,MainActivity::class.java))
      })
      builderLose.setNegativeButton("Return", DialogInterface.OnClickListener(){
              _, _ ->
          finish()
          startActivity(Intent(applicationContext,MenuActivity::class.java))
      })
      dialogLose = builderLose.create()

      picture = BitmapFactory.decodeResource(resources, R.drawable.good1)
    gameStateAdapter = GameStateAdapter(resources)
      startTime = System.currentTimeMillis()
  }


  @Synchronized
  override fun clickHere(x: Float, y: Float) {
    //gameState.killSprite(x,y)
      gameState.setGoodSpeed(x,y, gameView.width, gameView.height)
      gameState.throwStar()
      soundPlayer.playStarThrow()

  }


    override fun onKeyLongPress(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KEYCODE_VOLUME_DOWN) {
            println("Test Long press!")
            flag = false
            flag2 = true
            return true
        }
        return super.onKeyLongPress(keyCode, event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KEYCODE_VOLUME_DOWN) {
            event.startTracking()
            if (flag2) {
                flag = false
            } else {
                flag = true
                flag2 = false
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KEYCODE_VOLUME_DOWN) {
            event.startTracking()
            if (flag) {
                println("Test Short")
                gameState.throwStar()
                soundPlayer.playStarThrow()
            }
            flag = true
            flag2 = false
            return true
        }
        return super.onKeyUp(keyCode, event)
    }


  override fun surfaceDestroyed(holder: SurfaceHolder?) {
    loopingThread?.stopLoop()
}

  @Synchronized
  override fun surfaceCreated(holder: SurfaceHolder?) {
      gameState.initGame(resources,
      gameView.width, gameView.height)
    loopingThread = LoopingThread(this::loop)
    loopingThread?.startLoop()

  }

  @Synchronized
  private fun loop() {
    gameState.update(gameView.width, gameView.height)
    gameView.drawInHolder {
      it.drawColor(Color.BLACK)
      gameStateAdapter.draw(it, gameState)
    }
      if(gameState.isGameOver()){
          if(gameState.endState == EndState.GOOD_WINS) {
              if (!dialogWin.isShowing) this.runOnUiThread {
                  resultTime = System.currentTimeMillis() - startTime
                  dialogWin.setTitle("Congratulations! Your result: $resultTime millisecondsи ")
                  dialogWin.show()
              }
          }
          else if(gameState.endState == EndState.BAD_WINS) {
              if (!dialogLose.isShowing) this.runOnUiThread { dialogLose.show() }
          }
      }
  }



}
