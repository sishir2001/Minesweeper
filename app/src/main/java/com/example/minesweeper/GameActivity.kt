package com.example.minesweeper

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlin.random.Random
// TODO : Implement Time Tracker , Save the Time of the game and compare with best time in sharedPreferences
// TODO : Implement the algorithm, create a function to check for gameCompletion
// TODO : Implementing Flag Button , restricting flags to mine count - DONE
// TODO : Add listener to Restart Button -> making intent to the activity itself - DONE
const val GAME_TAG = "GameActivity"
class GameActivity : AppCompatActivity() {

    lateinit var innerLinearLayout : LinearLayout
    lateinit var btnRestart: Button
    lateinit var textViewMineCount : TextView
    var arrMines :MutableList<MutableList<MineButton>> = mutableListOf() // 2D array
    var actualMineCount : Int = 0
    var flagMineCount : Int = 0
    var nonMines : Int = -1
    private var rows = -1
    private var columns = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        innerLinearLayout = findViewById(R.id.innerLinearLayout)
        rows = intent.getIntExtra("rows",-1)
        columns = intent.getIntExtra("columns",-1) // getting extra data from intents
        val mines = intent.getIntExtra("mines",-1) // getting extra data from intents

        // initializing the global views
        btnRestart = findViewById(R.id.buttonRestart)
        btnRestart.setOnClickListener { whenClickRestart() }
        textViewMineCount = findViewById(R.id.MineCount)

//        Toast.makeText(this,"$rows X $columns",Toast.LENGTH_SHORT).show()

        // starting the game
        initializeArrData()
        initializeMines(mines) // sets some random pixes as mines and increases adjacent pixes count by 1
        // counting no of nonMines
        initializeNonMines()
        // initializing textView Mines to no of mines
        initializeTextViewMines()
        // setting up clicklisteners to every pixel
        settingUpListeners() // sets up listeners for the buttons

    }
    private fun whenClickRestart(){
        startActivity(intent)
        // or isEnabled = true for all the pixels and restart of actual initialization of @param actualMineCount and @param time
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
               btnMine.setBackgroundColor(R.color.purple_500)
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
        Log.i(GAME_TAG,"$mines")
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
                arrMines[i][j].btn.setBackgroundResource(R.drawable.mine_mine)
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
        if(!obj.isMine && !obj.isFlagged){
            obj.btn.text = obj.count.toString()
            // making the text white
            obj.btn.setTextColor(resources.getColor(R.color.white))
            obj.btn.isEnabled = false
            nonMines--
        }
        checkCompleteConventional()
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
            obj.btn.setBackgroundColor(R.color.purple_500)
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
        Log.i(GAME_TAG,"Non Mines count : $nonMines")
        if(nonMines == 0){
            Toast.makeText(this,"Congrats You won !",Toast.LENGTH_LONG).show()
            disableAll()
        }
    }
    private fun checkCompleteByFlagMark(){
        Log.i(GAME_TAG,"flagMineCount : $flagMineCount and actualMineCount : $actualMineCount")
        if(flagMineCount == 0 && actualMineCount == 0){
            Toast.makeText(this,"Congrats You won !",Toast.LENGTH_LONG).show()
            disableAll()
        }
    }
    private fun disableAll(){
       // disabling all buttons and setting mine_mine as background for mine buttons
        for(i in 0 until rows){
            for(j in 0 until columns){
                if(arrMines[i][j].isMine){
                   // set background as mine
                    arrMines[i][j].btn.setBackgroundResource(R.drawable.mine_mine)
                }
                else if(!arrMines[i][j].isFlagged && arrMines[i][j].btn.isEnabled){
                    arrMines[i][j].btn.text = arrMines[i][j].count.toString()
                    arrMines[i][j].btn.isEnabled = false
                }
            }
        }
    }
}