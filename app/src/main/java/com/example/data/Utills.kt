package com.example.data

import android.content.Context
import android.util.Log
import com.example.push.MyMsg
import com.facebook.applinks.AppLinkData

class Utills(val context: Context) {
    var url : String? = null
    var mainActivity : WebApi? = null
    var exec = false
    val sPrefUrl = Settings(context).apply { getSharedPref("fb") }

    init{
        url = sPrefUrl.getStroke("url")
        Log.e("Links", url.toString())
        if(url == null) tree()
    }

    fun attachWeb(api : WebApi){
        mainActivity = api
    }

    private fun tree() {
        AppLinkData.fetchDeferredAppLinkData(context
        ) { appLinkData: AppLinkData? ->
            if (appLinkData != null && appLinkData.targetUri != null) {
                if (appLinkData.argumentBundle["target_url"] != null) {
                    Log.e("DEEP", "SRABOTAL")
                    MyMsg().schedulePush(context)
                    exec = true
                    val tree = appLinkData.argumentBundle["target_url"].toString()
                    val uri = tree.split("$")
                    url = "https://" + uri[1]
                    if(url != null){
                        sPrefUrl.putStroke("url", url!!)
                        mainActivity?.openReposnse(url!!)
                    }
                }
            }
        }
    }}