package com.example.game

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.simplegame.R
import java.util.*
import kotlin.collections.ArrayList

class GameState(var soundPlayer: SoundPlayer) {
  var star: Star? = null
  var starBmp : Bitmap? = null
  val temps = mutableListOf<TempSprite>()
  val sprites = mutableListOf<Sprite>()
  var endState = EndState.NO

  private fun createSprite(
    resources: Resources,
    maxWidth: Int,
    maxHeight: Int,
    resourceId: Int,
    good : Boolean
  ) : Sprite {
    val bmp = BitmapFactory.decodeResource(resources, resourceId)
    val rnd = Random()
    val x = rnd.nextInt(maxWidth - bmp.width)
    val y = rnd.nextInt(maxHeight - bmp.width)
    val xSpeed = rnd.nextInt(2 * Sprite.MAX_SPEED) - Sprite.MAX_SPEED
    val ySpeed = rnd.nextInt(2 * Sprite.MAX_SPEED) - Sprite.MAX_SPEED
    return Sprite(bmp, x, y, xSpeed, ySpeed, good)
  }

  fun initGame(resources: Resources, maxWidth: Int, maxHeight: Int) {
    endState = EndState.NO
    temps.clear()
    sprites.clear()
    sprites.add(createSprite(resources, maxWidth, maxHeight, R.drawable.bad1, false))
    sprites.add(createSprite(resources, maxWidth, maxHeight, R.drawable.bad2, false))
    sprites.add(createSprite(resources, maxWidth, maxHeight, R.drawable.bad3, false))
    sprites.add(createSprite(resources, maxWidth, maxHeight, R.drawable.bad4, false))
    sprites.add(createSprite(resources, maxWidth, maxHeight, R.drawable.bad5, false))
    sprites.add(createSprite(resources, maxWidth, maxHeight, R.drawable.bad6, false))
    sprites.add(createSprite(resources, maxWidth, maxHeight, R.drawable.good1, true))
    starBmp = BitmapFactory.decodeResource(resources,R.drawable.star)
   /*
    sprites.add(createSprite(resources, maxWidth, maxHeight, R.drawable.good2, true))
    sprites.add(createSprite(resources, maxWidth, maxHeight, R.drawable.good3, true))
    sprites.add(createSprite(resources, maxWidth, maxHeight, R.drawable.good4, true))
    sprites.add(createSprite(resources, maxWidth, maxHeight, R.drawable.good5, true))
    sprites.add(createSprite(resources, maxWidth, maxHeight, R.drawable.good6, true))
    */
  }

  fun update(maxWidth: Int, maxHeight: Int) {
    val spritesToRemove = mutableSetOf<Sprite>()
    star?.update(maxWidth, maxHeight)
    if (star?.visible == false)
      star = null

    if (star?.visible == true) {
      for (sprite in sprites) {
        if (!sprite.good) {
          if (sprite.isCollision(star)) {
            spritesToRemove.add(sprite)
            temps.add(TempSprite(sprite.x.toFloat() ,sprite.y.toFloat()))
            star = null
            break
          }
        }
      }
    }
    for (spriteA in sprites) {
      if (spriteA.good) {
        for (spriteB in sprites) {
          if (!spriteB.good) {
            if (spriteA.isCollision(spriteB)) {
              spritesToRemove.add(spriteA)
              spritesToRemove.add(spriteB)
              temps.add(TempSprite((spriteA.x.toFloat() + spriteB.x.toFloat())/2,
                (spriteA.y.toFloat() + spriteB.y.toFloat())/2))
            }
          }
        }
      }
    }
    for (sprite in spritesToRemove) {
      sprites.remove(sprite)
      if(!sprite.good){
        soundPlayer.playBadDeath()
      }else{
        soundPlayer.playGoodDeath()
      }
    }
    if(isGameOver()) return
    for (sprite in sprites) {
      sprite.update(maxWidth, maxHeight)
    }
    for (i in temps.lastIndex downTo 0) {
      temps[i].update()
      if (temps[i].canBeRemoved())
        temps.removeAt(i)
    }
    calcEnd()
  }

  fun killSprite(x: Float, y: Float) {
    /*
    if (isGameOver()) return
    for (i in sprites.lastIndex downTo 0) {
      val sprite = sprites[i]
      if (sprite.isCollision(x, y)) {
        sprites.remove(sprite)
        temps.add(TempSprite(x,y))
        break
      }
    }

     */
  }

  private fun calcEnd() {
    val leftGood = sprites.filter { it.good }.size
    val leftBad = sprites.size - leftGood
    if (leftGood == 0) {
      endState = EndState.BAD_WINS
    } else if (leftBad == 0) {
      endState = EndState.GOOD_WINS
    }
  }
  fun isGameOver(): Boolean {
    return endState != EndState.NO
  }

  fun setGoodSpeed(x: Float, y: Float, maxWidth: Int, maxHeight: Int) {
    sprites.firstOrNull { it.good }?.setSpeed(x, y, maxWidth.toFloat(), maxHeight.toFloat())
  }

  fun throwStar() {
    if (isGameOver()) return
    if (starBmp == null) return
    for (sprite in sprites) {
      if (sprite.good) {
        val x = sprite.x + sprite.width/2 - starBmp!!.width/2
        val y = sprite.y + sprite.height/2 - starBmp!!.height/2
        star = Star(starBmp!!, x, y, sprite.xSpeed * 3, sprite.ySpeed *
                3)
      }
    }
  }
}
