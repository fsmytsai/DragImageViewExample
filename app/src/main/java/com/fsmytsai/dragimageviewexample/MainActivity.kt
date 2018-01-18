package com.fsmytsai.dragimageviewexample

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.RelativeLayout

class MainActivity : AppCompatActivity() {
    private val mBitmapList = ArrayList<Bitmap>()
    private val mDragImageViewList = ArrayList<DragImageView>()
    private lateinit var rlMainContainer: RelativeLayout
    private lateinit var ivMainAgree: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBitmapList.add(BitmapFactory.decodeResource(resources, R.drawable.i1))
        mBitmapList.add(BitmapFactory.decodeResource(resources, R.drawable.i2))
        mBitmapList.add(BitmapFactory.decodeResource(resources, R.drawable.i3))
        mBitmapList.add(BitmapFactory.decodeResource(resources, R.drawable.i4))
        mBitmapList.add(BitmapFactory.decodeResource(resources, R.drawable.i5))
        mBitmapList.add(BitmapFactory.decodeResource(resources, R.drawable.i6))
        initViews()
    }

    private fun initViews() {
        ivMainAgree = findViewById(R.id.iv_main_agree)
        rlMainContainer = findViewById(R.id.rl_main_container)
        rlMainContainer.post {
            val firstDragImageView = DragImageView(this)
            mDragImageViewList.add(firstDragImageView)
            val secondDragImageView = DragImageView(this)
            mDragImageViewList.add(secondDragImageView)

            mDragImageViewList[0].layoutParams = getFirstLayoutParams()
            mDragImageViewList[1].layoutParams = getSecondLayoutParams()

            mDragImageViewList[0].setImageBitmap(mBitmapList[0])
            mDragImageViewList[1].setImageBitmap(mBitmapList[1])

            mDragImageViewList[0].setMyDragListener(mMyDragListener)
            mDragImageViewList[1].setMyDragListener(mMyDragListener)

            rlMainContainer.addView(mDragImageViewList[1])
            rlMainContainer.addView(mDragImageViewList[0])
        }
    }

    private fun getFirstLayoutParams(): RelativeLayout.LayoutParams {
        val firstLayoutParams = RelativeLayout.LayoutParams((rlMainContainer.width * 0.8).toInt(), (rlMainContainer.height * 0.8).toInt())
        firstLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        return firstLayoutParams
    }

    private fun getSecondLayoutParams(): RelativeLayout.LayoutParams {
        val secondLayoutParams = RelativeLayout.LayoutParams((rlMainContainer.width * 0.4).toInt(), (rlMainContainer.height * 0.4).toInt())
        secondLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        return secondLayoutParams
    }

    private val mMyDragListener = object : DragImageView.MyDragListener {

        override fun rollBack() {
        }

        override fun dragging() {
        }

        override fun finish(isAgree: Boolean) {
            if (isAgree)
                ivMainAgree.setImageResource(R.drawable.yes)
            else
                ivMainAgree.setImageResource(R.drawable.no)

            showAgree()

            if (mDragImageViewList.size <= 1)
                return

            val scaleAnimation = ScaleAnimation(1f, 2f, 1f, 2f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)

            scaleAnimation.duration = 300
            scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(p0: Animation?) {
                }

                override fun onAnimationEnd(p0: Animation?) {
                    mDragImageViewList[1].clearAnimation()
                    mDragImageViewList[1].layoutParams = getFirstLayoutParams()
                    mDragImageViewList[1].isAnimating = false
                }

                override fun onAnimationStart(p0: Animation?) {
                }
            })
            mDragImageViewList[1].startAnimation(scaleAnimation)
            mDragImageViewList[1].isAnimating = true
        }

        override fun finished() {
            mBitmapList.removeAt(0)
            rlMainContainer.removeView(mDragImageViewList[0])
            mDragImageViewList.removeAt(0)

            if (mBitmapList.size <= 1)
                return

            val dragImageView = DragImageView(this@MainActivity)
            mDragImageViewList.add(dragImageView)

            mDragImageViewList[1].layoutParams = getSecondLayoutParams()
            mDragImageViewList[1].setImageBitmap(mBitmapList[1])
            mDragImageViewList[1].setMyDragListener(this)
            rlMainContainer.addView(mDragImageViewList[1], 0)
        }
    }

    private fun showAgree() {
        ivMainAgree.animate().alpha(1f)
                .setDuration(300)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        ivMainAgree.animate().alpha(0f).duration = 300
                    }
                })
    }
}
