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
    val bitmapList = ArrayList<Bitmap>()
    val dragImageViewList = ArrayList<DragImageView>()
    lateinit var rlMainContainer: RelativeLayout
    lateinit var ivMainAgree: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bitmapList.add(BitmapFactory.decodeResource(resources, R.drawable.i1))
        bitmapList.add(BitmapFactory.decodeResource(resources, R.drawable.i2))
        bitmapList.add(BitmapFactory.decodeResource(resources, R.drawable.i3))
        bitmapList.add(BitmapFactory.decodeResource(resources, R.drawable.i4))
        bitmapList.add(BitmapFactory.decodeResource(resources, R.drawable.i5))
        bitmapList.add(BitmapFactory.decodeResource(resources, R.drawable.i6))
        initViews()
    }

    private fun initViews() {
        ivMainAgree = findViewById(R.id.iv_main_agree)
        rlMainContainer = findViewById(R.id.rl_main_container)
        rlMainContainer.post {
            val firstDragImageView = DragImageView(this)
            dragImageViewList.add(firstDragImageView)
            val secondDragImageView = DragImageView(this)
            dragImageViewList.add(secondDragImageView)


            dragImageViewList[0].layoutParams = getFirstLayoutParams()
            dragImageViewList[1].layoutParams = getSecondLayoutParams()

            dragImageViewList[0].setImageBitmap(bitmapList[0])
            dragImageViewList[1].setImageBitmap(bitmapList[1])

            dragImageViewList[0].setMyDragListener(myDragListener)
            dragImageViewList[1].setMyDragListener(myDragListener)

            rlMainContainer.addView(dragImageViewList[1])
            rlMainContainer.addView(dragImageViewList[0])
        }
    }

    fun getFirstLayoutParams(): RelativeLayout.LayoutParams {
        val firstLayoutParams = RelativeLayout.LayoutParams((rlMainContainer.width * 0.8).toInt(), (rlMainContainer.height * 0.8).toInt())
        firstLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        return firstLayoutParams
    }

    fun getSecondLayoutParams(): RelativeLayout.LayoutParams {
        val secondLayoutParams = RelativeLayout.LayoutParams((rlMainContainer.width * 0.4).toInt(), (rlMainContainer.height * 0.4).toInt())
        secondLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        return secondLayoutParams
    }

    val myDragListener = object : DragImageView.MyDragListener {

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

            if (dragImageViewList.size <= 1)
                return

            val scaleAnimation = ScaleAnimation(1f, 2f, 1f, 2f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)

            scaleAnimation.duration = 300
            scaleAnimation.setAnimationListener(object : Animation.AnimationListener {

                override fun onAnimationRepeat(p0: Animation?) {

                }

                override fun onAnimationEnd(p0: Animation?) {
                    dragImageViewList[1].clearAnimation()
                    dragImageViewList[1].layoutParams = getFirstLayoutParams()
                }

                override fun onAnimationStart(p0: Animation?) {
                }

            })
            dragImageViewList[1].startAnimation(scaleAnimation)
        }

        private fun showAgree() {
            ivMainAgree.animate().alpha(1f)
                    .setDuration(300)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            ivMainAgree.animate().alpha(0f)
                                    .setDuration(300)
                        }

                    })
        }

        override fun finished() {
            bitmapList.removeAt(0)
            rlMainContainer.removeView(dragImageViewList[0])
            dragImageViewList.removeAt(0)

            if (bitmapList.size <= 1)
                return

            val dragImageView = DragImageView(this@MainActivity)
            dragImageViewList.add(dragImageView)

            dragImageViewList[1].layoutParams = getSecondLayoutParams()
            dragImageViewList[1].setImageBitmap(bitmapList[1])
            dragImageViewList[1].setMyDragListener(this)
            rlMainContainer.addView(dragImageViewList[1], 0)
        }

    }
}
