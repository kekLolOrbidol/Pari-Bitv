package com.example.game

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import com.example.simplegame.R

class SoundPlayer(
    context: Context
) {
    private var soundPool = SoundPool(2,AudioManager.STREAM_MUSIC,0)
    private var badDeath = soundPool.load(context,R.raw.baddeath,1)
    private var goodDeath = soundPool.load(context,R.raw.gooddeath,1)
    private var star = soundPool.load(context,R.raw.star,1)

    fun playBadDeath(){
        soundPool.play(badDeath,1.0f, 1.0f,1, 0, 1.0f)
    }

    fun playGoodDeath(){
        soundPool.play(goodDeath,0.25f, 0.25f,1, 0, 1.0f)
    }

    fun playStarThrow(){
        soundPool.play(star,1.0f, 1.0f,1, 0, 1.0f)
    }

}