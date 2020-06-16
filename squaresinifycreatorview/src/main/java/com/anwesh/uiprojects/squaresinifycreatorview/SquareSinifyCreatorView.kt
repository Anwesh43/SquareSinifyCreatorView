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

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawSquareSinify(i : Int, scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val sf : Float = scale.sinify()
    val sc1 : Float = sf.divideScale(0, divs)
    val sc2 : Float = sf.divideScale(1, divs)
    val sc3 : Float = sf.divideScale(2, divs)
    val sj : Float = 1f - 2 * i
    save()
    translate(w / 2, (h / 2) * sc1 - sj * size * 0.5f * sc3)
    for (j in 0..1) {
        save()
        rotate(180f * j * sc2)
        drawLine(0f, 0f, size * 0.5f, 0f, paint)
        restore()
    }
    restore()
}

fun Canvas.drawSSCNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = Color.parseColor(colors[i])
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    save()
    for (j in 0..1) {
        drawSquareSinify(j, scale, w, h, paint)
    }
    restore()
}

class SquareSinifyCreatorView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var prevScale : Float = 0f, var dir : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += dir * scGap
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }
}