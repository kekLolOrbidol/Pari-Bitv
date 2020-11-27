package com.example.simplegame

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.GridView


class GameRankActivity : Activity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rank)
        var gridView = findViewById<GridView>(R.id.rankView)

        val results = ArrayList<String>()

        val sharedPref = this.getSharedPreferences("scores.xml",Context.MODE_PRIVATE)
        val resultMap = sharedPref.all
        var newMap  = HashMap<String,Int>()
        for ((key, value) in resultMap.entries) {
            newMap[key] = Integer.parseInt(value as String)
        }
        val sortedResult = newMap.toList().sortedBy { (_, value) -> value}.toMap()
        for(key in sortedResult.keys){
            results.add(key + "                            " + sortedResult[key] + " milliseconds")
        }

        val gridViewArrayAdapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, results)
        gridView.numColumns = 1
        gridView.adapter = gridViewArrayAdapter
    }
}