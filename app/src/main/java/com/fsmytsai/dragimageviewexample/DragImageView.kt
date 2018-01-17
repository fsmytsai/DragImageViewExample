package com.fsmytsai.dragimageviewexample

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageView

class DragImageView : ImageView {
    private val mTag = "DragImageView"
    private var mScreenWidth = 0
    private var mOriginX = 0f
    private var mDownX = 0f
    private var mDownY = 0f
    private var isAnimating = false
    private var mMyDragListener: MyDragListener? = null

    constructor(context: Context) : super(context) {
        setScreenWidth()
        adjustViewBounds = true
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setScreenWidth()
        adjustViewBounds = true
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        setScreenWidth()
        adjustViewBounds = true
    }

    private fun setScreenWidth() {
        if (context is Activity) {
            val dm = DisplayMetrics()
            val activity = context as Activity
            activity.windowManager.defaultDisplay.getMetrics(dm)
            mScreenWidth = dm.widthPixels
        } else
            Log.e(mTag, "context not an activity")
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mOriginX = x
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (mScreenWidth == 0 || isAnimating)
            return false

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mDownX = event!!.x
                mDownY = event!!.y
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event!!.x - mDownX
                val dy = event!!.y - mDownY
                if (Math.abs(dx) > Math.abs(dy)) {
                    val l = left + dx.toInt()
                    val r = right + dx.toInt()
                    layout(l, top, r, bottom)

                    mMyDragListener?.dragging()
                } else {
                    mDownX = event!!.x
                    mDownY = event!!.y
                }
            }
            MotionEvent.ACTION_UP -> {
                val centerX = x + width / 2
                isAnimating = true
                if (centerX < mScreenWidth / 3.3) {
                    //左滑
                    mMyDragListener?.finish(true)
                    animate().translationX(-width.toFloat())
                            .setDuration(300)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator?) {
                                    super.onAnimationEnd(animation)
                                    isAnimating = false
                                    mMyDragListener?.finished()
                                }
                            })

                } else if (centerX > mScreenWidth - mScreenWidth / 3.3) {
                    //右滑
                    mMyDragListener?.finish(false)
                    animate().translationX(width.toFloat())
                            .setDuration(300)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator?) {
                                    super.onAnimationEnd(animation)
                                    isAnimating = false
                                    mMyDragListener?.finished()
                                }
                            })
                } else {
                    //回滾
                    mMyDragListener?.rollBack()
                    animate().x(mOriginX)
                            .setDuration(300)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator?) {
                                    super.onAnimationEnd(animation)
                                    isAnimating = false
                                }
                            })
                }
            }
        }
        return true
    }

    fun setMyDragListener(myDragListener: MyDragListener) {
        mMyDragListener = myDragListener
    }

    interface MyDragListener {
        fun rollBack()
        fun finish(isAgree: Boolean)
        fun finished()
        fun dragging()
    }
}