package com.example.counterclicker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    companion object{
        lateinit var prefs: Prefs
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startService(Intent(baseContext, ClearService::class.java))

        setContentView(R.layout.activity_main)
        prefs = Prefs(applicationContext)
        initUI()

    }

    fun initUI(){
        val counterLabel = findViewById<TextView>(R.id.counter)
        val btnIncreaseCounter = findViewById<Button>(R.id.btnIncrease)
        var counterValue = prefs.getCounterValue()

        counterLabel.text = counterValue.toString()

        btnIncreaseCounter.setOnClickListener{
            counterValue += 1
            prefs.saveCounterValue(counterValue)
            counterLabel.text = (prefs.getCounterValue()).toString()
        }
    }
}