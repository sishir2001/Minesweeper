package com.example.minesweeper

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
const val BEST_SCORE = "BEST_SCORE"
const val PREV_SCORE = "PREV_SCORE"
class MainActivity : AppCompatActivity() {
    private lateinit var diffLevel : RadioGroup
    private lateinit var btnStart : Button
    private lateinit var textViewBestScore : TextView
    private lateinit var textViewPrevScore : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // loading data from sharedPreferences
        loadData()
        btnStart = findViewById(R.id.buttonStart)
        diffLevel = findViewById(R.id.radioGroup1)
        btnStart.setOnClickListener {
            val checkedId = diffLevel.checkedRadioButtonId
            if(checkedId != -1){
                val checkedRadioButton = findViewById<RadioButton>(checkedId)
                when(checkedRadioButton.text){
                    getString(R.string.easy) ->{
                        Toast.makeText(this,"easy",Toast.LENGTH_SHORT).show()
                    }
                    getString(R.string.medium) ->{
                        Toast.makeText(this,"medium",Toast.LENGTH_SHORT).show()
                    }
                    getString(R.string.hard) ->{
                        Toast.makeText(this,"hard",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else{
                Toast.makeText(this,"None clicked",Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun loadData(){
        textViewPrevScore = findViewById(R.id.textViewPrevTime)
        textViewBestScore = findViewById(R.id.textViewBestTime)
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val bestScore:Int = sharedPref.getInt(BEST_SCORE,-1)
        val prevScore:Int = sharedPref.getInt(PREV_SCORE,-1)
        if(bestScore == -1 && prevScore == -1){
            textViewBestScore.text = getString(R.string.no_best_time)
            textViewPrevScore.text = getString(R.string.no_previous_time)
        }
        else{
            textViewBestScore.text = getString(R.string.best_time,bestScore)
            textViewPrevScore.text = getString(R.string.previous_time,prevScore)
        }
    }
}