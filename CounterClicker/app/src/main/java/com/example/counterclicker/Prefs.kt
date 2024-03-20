package com.example.counterclicker

import android.content.Context

class Prefs(val context: Context) {
    val storage = context.getSharedPreferences("counterValue",0)

    fun saveCounterValue(currentValue:Int){
        storage.edit().putInt("countVal",currentValue).apply()
    }

    fun getCounterValue(): Int{
        return storage.getInt("countVal",0)
    }
}