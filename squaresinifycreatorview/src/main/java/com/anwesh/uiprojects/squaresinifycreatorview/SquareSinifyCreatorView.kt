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
    translate(w / 2, (h / 2) * sc1)
    save()
    translate(0f, -sj * size * 0.5f * sc3)
    for (j in 0..(parts - 1)) {
        save()
        rotate(180f * j * sc2)
        drawLine(0f, 0f, size * 0.5f, 0f, paint)
        restore()
    }
    restore()
    drawLine(size * 0.5f * sj, -size * 0.5f * sc3, size * 0.5f * sj, size * 0.5f * sc3, paint)
    restore()
}

fun Canvas.drawSSCNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = Color.parseColor(colors[i])
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    save()
    for (j in 0..(parts - 1)) {
        drawSquareSinify(j, scale, w, h, paint)
    }
    restore()
}

class SquareSinifyCreatorView(ctx : Context) : View(ctx) {

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
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

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class SSCNode(var i : Int, val state : State = State()) {

        private var prev : SSCNode? = null
        private var next : SSCNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = SSCNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawSSCNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : SSCNode {
            var curr : SSCNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class SquareSinifyCreator(var i : Int) {

        private var curr : SSCNode = SSCNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : SquareSinifyCreatorView) {

        private val animator : Animator = Animator(view)
        private val ssc : SquareSinifyCreator = SquareSinifyCreator(0)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            ssc.draw(canvas, paint)
            animator.animate {
                ssc.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            ssc.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : SquareSinifyCreatorView {
            val view : SquareSinifyCreatorView = SquareSinifyCreatorView(activity)
            activity.setContentView(view)
            return view
        }
    }
}