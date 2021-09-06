package com.example.minesweeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import kotlin.random.Random

class GameActivity : AppCompatActivity() {
    lateinit var innerLinearLayout : LinearLayout
    var arrMines :MutableList<MutableList<MineButton>> = mutableListOf() // 2D array
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        innerLinearLayout = findViewById(R.id.innerLinearLayout)
        val rows = intent.getIntExtra("rows",-1)
        val columns = intent.getIntExtra("columns",-1) // getting extra data from intents
        val mines = intent.getIntExtra("mines",-1) // getting extra data from intents
        Toast.makeText(this,"$rows X $columns",Toast.LENGTH_SHORT).show()
        initializeArrData(rows,columns)
        initializeMines(rows,columns,mines) // sets some random pixes as mines and increases adjacent pixes count by 1
        settingUpListeners(rows,columns) // sets up listeners for the buttons
    }
    private fun initializeArrData(rows:Int,columns:Int){
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
        Log.i("GameActivity",arrMines.toString())
    }
    private fun initializeMines(rows:Int,columns:Int,mines:Int){
        Log.i("GameActivity","Inside initializeMines")
        Log.i("GameActivity","$mines")
        for(l in 0 until mines){
            val i = Random.nextInt(0,rows) // from 0 to row-1 inclusive
            val j = Random.nextInt(0,columns)
            Log.i("GameActivity","${i},${j}")
                arrMines[i][j].isMine = true
                arrMines[i][j].btn.isEnabled = false
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
    private fun settingUpListeners(rows:Int,columns:Int){
       for(i in 0 until rows){
           for(j in 0 until columns){
               arrMines[i][j].btn.setOnClickListener {
                   whenBtnClicked(arrMines[i][j])
               }
           }
       }
    }
    private fun whenBtnClicked(obj:MineButton){
        // Implement the algorithm
        Toast.makeText(this,"${obj.i},${obj.j} , Mine : ${obj.isMine} , Count : ${obj.count}",Toast.LENGTH_SHORT).show()

    }
}