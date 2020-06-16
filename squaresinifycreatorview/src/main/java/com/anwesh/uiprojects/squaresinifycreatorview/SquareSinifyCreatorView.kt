package com.anwesh.uiprojects.squaresinifycreatorview

/**
 * Created by anweshmishra on 16/06/20.
 */

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Color
import android.graphics.Canvas

val colors : Array<String> = arrayOf("#3F51B5", "#F44336", "#2196F3", "#009688", "#4CAF50")
val parts : Int = 2
val divs : Int = 4
val scGap : Float = 0.02f / (divs)
val sizeFactor : Float = 3.8f
val strokeFactor : Float = 90f
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 20
