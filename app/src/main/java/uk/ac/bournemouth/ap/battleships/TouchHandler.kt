package uk.ac.bournemouth.ap.battleships

import android.view.View
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.core.view.GestureDetectorCompat

class TouchHandler : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val myGestureDetector = GestureDetectorCompat(context, MyGestureListener())

    init {

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return myGestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    private inner class MyGestureListener: GestureDetector.SimpleOnGestureListener() {

        override fun onDown(ev: MotionEvent): Boolean {
            /*
             * You should always include onDown().
             *
             * Normally onDown should return true, unless you want to ignore all touch gestures
             * starting in a particular region of your view (you will not see events from outside
             * the view anyway).
             */
            return true
        }

        override fun onSingleTapUp(ev: MotionEvent): Boolean {
            // You can access the properties of the event
            val xCoord = ev.x
            val yCoord = ev.y

            val canvasWidth = width.toFloat()
            val canvasHeight = height.toFloat()

            return true
            //Note the use of the dollar symbol to insert the variable values into the string.
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            val xCoord = e.x
            val yCoord = e.y

            Log.d(LOGTAG, "DoubleTap x= $xCoord y= $yCoord")
            return true
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val xCoord = e1.x
            val yCoord = e2.y

            Log.d(LOGTAG, "Fling x= $xCoord y= $yCoord velocityX=$velocityX velocityY=$velocityY")
            return true
        }
    }      // End of myGestureListener class


    companion object {         // declare a constant (must be in the companion of the outer class)
        const val LOGTAG = "MyTask"
    }
}