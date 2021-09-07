package com.example.minesweeper

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import kotlin.random.Random
// TODO : Implement Time Tracker , Save the Time of the game and compare with best time in sharedPreferences
// TODO : Implement RESTART at last -> initialize all the respective variables to their original value and Time = 0
const val GAME_TAG = "GameActivity"
class GameActivity : AppCompatActivity() {

    lateinit var innerLinearLayout : LinearLayout
    lateinit var btnRestart: Button
    lateinit var textViewMineCount : TextView
    lateinit var textViewTimer : TextView
    lateinit var timer:CountDownTimer
    private var arrMines :MutableList<MutableList<MineButton>> = mutableListOf() // 2D array
    private var actualMineCount : Int = 0 // restart
    private var flagMineCount : Int = -1// restart
    private var nonMines : Int = -1
    private var rows = -1
    private var columns = -1
    private var mines = -1
    private var clickCount = 0 // imp for checking completion of game // restart
    private var counter = 0// for timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        innerLinearLayout = findViewById(R.id.innerLinearLayout)
        rows = intent.getIntExtra("rows",-1)
        columns = intent.getIntExtra("columns",-1) // getting extra data from intents
        mines = intent.getIntExtra("mines",-1) // getting extra data from intents

        // initializing the global views
        btnRestart = findViewById(R.id.buttonRestart)
        textViewMineCount = findViewById(R.id.MineCount)
        textViewTimer = findViewById(R.id.Time)
        btnRestart.setOnClickListener { whenClickRestart() }

//        Toast.makeText(this,"$rows X $columns",Toast.LENGTH_SHORT).show()

        // starting the game
        initializeArrData()
        initializeMines(mines) // sets some random pixes as mines and increases adjacent pixes count by 1
        // counting no of nonMines
        initializeNonMines()
        // initializing textView Mines to no of mines
        initializeTextViewMines() // initialization of actual mine counts
        // setting up clicklisteners to every pixel
        settingUpListeners() // sets up listeners for the buttons

        timer = startTimer()
        timer.start() // starting the timer


    }
    private fun startTimer():CountDownTimer{
        val timer = object : CountDownTimer(1200000,1000){
            override fun onTick(p0: Long) {
                textViewTimer.text = counter.toString()
                counter++
            }

            override fun onFinish() {
                textViewTimer.text = counter.toString()
            }
        }
        return timer
    }
    private fun whenClickRestart(){
        // or isEnabled = true for all the pixels and restart of actual initialization of @param actualMineCount and @param time

        Log.i(GAME_TAG,"Restarting the game !!")
        enableAll()
        // clearing the list
//        arrMines.clear()
        clickCount = 0
        actualMineCount = 0
        counter = 0
//        initializeArrData()
        initializeMines(mines) // sets some random pixes as mines and increases adjacent pixes count by 1
        // counting no of nonMines
        initializeNonMines()
        // initializing textView Mines to no of mines
        initializeTextViewMines() // initialization of actual mine counts
        // setting up clicklisteners to every pixel
//        settingUpListeners() // sets up listeners for the buttons
        timer = startTimer()
        timer.start() // starting the timer
    }
    private fun initializeNonMines(){
       nonMines = rows*columns - actualMineCount
        Log.i(GAME_TAG,"nonMines : $nonMines")
    }
    private fun initializeTextViewMines(){
        textViewMineCount.text = actualMineCount.toString()
        flagMineCount = actualMineCount // this variable for checking flagged mines correctly
    }
    private fun initializeArrData(){
        // params for linear layout
        val params1 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0
        ).apply {
            weight = 1.0F
        }
        val params2 = LinearLayout.LayoutParams(
            0,
            150
        ).apply {
            weight = 1.0F
        }
       for(i in 0 until rows){
           val horLinearLayout = LinearLayout(this)
           horLinearLayout.layoutParams = params1
           val arr = mutableListOf<MineButton>()
           for(j in 0 until columns){
               val btnMine = Button(this)
               btnMine.setBackgroundColor(R.color.purple_700)
               btnMine.id = (i.toString()+j.toString()).toInt()
               btnMine.layoutParams = params2
               val btnObj = MineButton(btnMine,i,j)
               arr.add(btnObj)
               horLinearLayout.addView(btnMine)
           }
           innerLinearLayout.addView(horLinearLayout)
           arrMines.add(arr)
       }
        Log.i(GAME_TAG,arrMines.toString())
    }
    private fun initializeMines(mines:Int){
        Log.i(GAME_TAG,"Inside initializeMines")
//        Log.i(GAME_TAG,"$mines")
        for(l in 0 until mines){
            val i = Random.nextInt(0,rows) // from 0 to row-1 inclusive
            val j = Random.nextInt(0,columns)
            Log.i(GAME_TAG,"${i},${j}")
            if(arrMines[i][j].isMine == false){
                // increasing the actualMinecount
                    actualMineCount++

                arrMines[i][j].isMine = true
//                arrMines[i][j].btn.isEnabled = false
//                arrMines[i][j].btn.text = "M"
                // TODO : Remove final app
//                arrMines[i][j].btn.setBackgroundResource(R.drawable.mine_mine)

                // adding count to all the adjacent elements
                if(i-1 >= 0 && j-1>=0){
                    arrMines[i-1][j-1].count++
                }
                if((i >= 0 || i < rows) && j-1 >= 0)
                    arrMines[i][j-1].count++
                if(i-1 >= 0 && (j >= 0 || j < columns))
                    arrMines[i-1][j].count++
                if(i-1 >= 0 && j+1<columns){
                    arrMines[i-1][j+1].count++
                }
                if((i >= 0 || i < rows) && j+1 < columns)
                    arrMines[i][j+1].count++
                if(i+1 < rows && j - 1 >= 0){
                    arrMines[i+1][j-1].count++
                }
                if(i+1<rows && (j >= 0 || j < columns))
                    arrMines[i+1][j].count++
                if(i+1<rows && j+1<columns)
                    arrMines[i+1][j+1].count++
            }
        }
    }
    private fun settingUpListeners(){
        // setting click listeners and Long click listeners
       for(i in 0 until rows){
           for(j in 0 until columns){
               arrMines[i][j].btn.setOnClickListener {
                   whenBtnClicked(arrMines[i][j])
               }
               arrMines[i][j].btn.setOnLongClickListener {
                   updateMineCountAndFlag(arrMines[i][j])
                   true
               }
           }
       }
    }
    private fun whenBtnClicked(obj:MineButton){
        // Implement the algorithm
//        Toast.makeText(this,"${obj.i},${obj.j} , Mine : ${obj.isMine} , Count : ${obj.count}",Toast.LENGTH_SHORT).show()
//        if(obj.isMine){
//           updateMineCount()
//        }
//        if(!obj.isMine && !obj.isFlagged){
//            obj.btn.text = obj.count.toString()
//            // making the text white
//            obj.btn.setTextColor(resources.getColor(R.color.white))
//            obj.btn.isEnabled = false
//            nonMines--
//        }
        rGameAlgo(obj.i,obj.j)
        checkCompleteConventional()
    }
    private fun rGameAlgo(i:Int,j:Int){
        if((i < 0 || i >= rows || j < 0 || j >= columns) || (arrMines[i][j].isFlagged)||(!arrMines[i][j].btn.isEnabled))
            return
        if(arrMines[i][j].isMine){
           Toast.makeText(this,"Oops You Stepped on a Mine , YOU LOST !!",Toast.LENGTH_LONG).show()
            disableAll(false)
        }
        else if(arrMines[i][j].count > 0){
            clickCount++
            arrMines[i][j].btn.text = arrMines[i][j].count.toString()
            arrMines[i][j].btn.setTextColor(resources.getColor(R.color.white))
            arrMines[i][j].btn.isEnabled = false
        }
        else if(arrMines[i][j].count == 0){
            clickCount ++
            arrMines[i][j].btn.text = getString(R.string.empty_string) // setting an empty string
            arrMines[i][j].btn.isEnabled = false
            rGameAlgo(i-1,j)
            rGameAlgo(i,j+1)
            rGameAlgo(i+1,j)
            rGameAlgo(i,j-1)
        }
    }
    private fun updateMineCountAndFlag(obj:MineButton){
        if(!obj.isFlagged && actualMineCount > 0){
            actualMineCount--
            if(obj.isMine){
                flagMineCount --
            }
            obj.isFlagged = true
            obj.btn.setBackgroundResource(R.drawable.mine_flag)
            Log.i(GAME_TAG,"Set Flag ${obj.i},${obj.j}")
        }
        else if(obj.isFlagged){
            obj.btn.setBackgroundColor(R.color.purple_700)
            obj.isFlagged = false
            if(obj.isMine){
                flagMineCount++
            }
            actualMineCount ++
            Log.i(GAME_TAG,"Invert Flag ${obj.i},${obj.j}")
        }
        textViewMineCount.text = actualMineCount.toString()
        checkCompleteByFlagMark()
    }
    private fun checkCompleteConventional(){
        Log.i(GAME_TAG,"Non Mines count : $nonMines , clickCounts : $clickCount")
        if(nonMines == clickCount){
            Toast.makeText(this,"Congrats You won !",Toast.LENGTH_LONG).show()
            disableAll(true)
        }
    }
    private fun checkCompleteByFlagMark(){
        Log.i(GAME_TAG,"flagMineCount : $flagMineCount and actualMineCount : $actualMineCount")
        if(flagMineCount == 0 && actualMineCount == 0){
            Toast.makeText(this,"Congrats You won !",Toast.LENGTH_LONG).show()
            disableAll(true)
        }
    }
    private fun disableAll(gameWon:Boolean){
       // disabling all buttons and setting mine_mine as background for mine buttons
        for(i in 0 until rows){
            for(j in 0 until columns){
                if(arrMines[i][j].isMine){
                   // set background as mine
                    arrMines[i][j].btn.setBackgroundResource(R.drawable.mine_mine)
                }
                else if(!arrMines[i][j].isFlagged && arrMines[i][j].btn.isEnabled){
                    if(gameWon){
                        if(arrMines[i][j].count > 0)
                            arrMines[i][j].btn.text = arrMines[i][j].count.toString()
                    }
                    arrMines[i][j].btn.isEnabled = false
                }
            }
        }
        timer.cancel()
        if(gameWon){
            Log.i(GAME_TAG,"Saving the prev score : ${counter-1}")
            // storing the the time in prevTime
            val sharedPref = getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE)
            with(sharedPref.edit()){
                putInt(PREV_SCORE,counter-1)
                apply()
            }
            // making an intent to MainActivity
            val mainIntent = Intent(this,MainActivity::class.java)
            startActivity(mainIntent)
        }
    }
    private fun enableAll(){
        for(i in 0 until rows){
            for(j in 0 until columns){
//                if(arrMines[i][j].isMine){
//                    // set background as mine
//                    arrMines[i][j].btn.setBackgroundResource(R.drawable.mine_mine)
//                }
//                else if(!arrMines[i][j].isFlagged && arrMines[i][j].btn.isEnabled){
//                            arrMines[i][j].btn.text = arrMines[i][j].count.toString()
//                    }
                arrMines[i][j].btn.isEnabled = true
                arrMines[i][j].isFlagged = false
                arrMines[i][j].isMine = false
                arrMines[i][j].count = 0
                arrMines[i][j].btn.text = resources.getString(R.string.empty_string)
                arrMines[i][j].btn.setBackgroundColor(R.color.purple_700)
            }
            }
        }
    }
