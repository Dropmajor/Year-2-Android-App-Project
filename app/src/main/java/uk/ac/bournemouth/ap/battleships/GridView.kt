package uk.ac.bournemouth.ap.battleships

import android.content.Context
import android.graphics.*
import android.graphics.Paint.Style
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.roundToInt


public class GridView : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val circleCol: Int = Color.RED
    private val labelCol: Int = Color.YELLOW
    private val backCol: Int = Color.rgb(250, 250, 200)

    //label text
    private val wordsText: String = "Hello World"

    //paint variables
    private var backPaint: Paint = Paint().apply {
        // Set up the paint style
        setStyle(Style.FILL)
        setColor(backCol)
    }
    var fillPaint = Paint()
    var strokePaint = Paint()

    //seperation
    private var xSep: Float = 75f;
    private var ySep: Float = 75f;

    private var gridSizeX : Int = 5;
    private var gridSizeY : Int = 5;
    var point : Int = -1

    init {
        fillPaint.style = Paint.Style.FILL;
        fillPaint.color = Color.GRAY;

        // stroke
        strokePaint.style = Paint.Style.STROKE;
        strokePaint.color = Color.BLACK;
        strokePaint.strokeWidth = 5f;
    }

    fun setSize(columns: Int, rows: Int) {
        gridSizeX = columns;
        gridSizeY = rows;
    }

    override fun onDraw(canvas: Canvas) {
        val canvasWidth = width.toFloat()
        val canvasHeight = height.toFloat()

        val cellSize = if(gridSizeX >= gridSizeY) canvasWidth / gridSizeX else canvasHeight / gridSizeY

        xSep = canvasWidth / gridSizeX
        ySep = canvasHeight / gridSizeY

        for (x in 1..gridSizeX) {
            for (y in 1..gridSizeY) {
                val centerX = x*cellSize - cellSize/2
                val centerY = y*cellSize- cellSize/2

                val r = RectF(centerX + cellSize/2, centerY + cellSize/2, centerX - cellSize/2, centerY - cellSize/2)
                canvas.drawRect(r, fillPaint)    // fill
                canvas.drawRect(r, strokePaint)
            }
        }
    }

    fun getGridSquare(x: Float, y: Float): IntArray {
        val cellSize = if(gridSizeX >= gridSizeY) width.toFloat() / gridSizeX else height.toFloat() / gridSizeY
        val gridX = floor(x / cellSize).toInt()
        val gridY = floor(y / cellSize).toInt()
        return intArrayOf(min(gridX, gridSizeX - 1), min(gridY, gridSizeY - 1));
    }
}