package uk.ac.bournemouth.ap.battleships

import android.content.Context
import android.graphics.*
import android.graphics.drawable.VectorDrawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import uk.ac.bournemouth.ap.battleshiplib.BattleshipGrid
import uk.ac.bournemouth.ap.battleshiplogic.Grid


class ShipView : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var missPaint: Paint
    private var hitPaint: Paint

    private var gridSizeX: Int = 10
    private var gridSizeY: Int = 10
    private val bitmap = (ResourcesCompat.getDrawable(this.resources, R.drawable.miss_vector, null) as VectorDrawable).toBitmap()

    private var gridVals : List<ShotRef> = emptyList()
    private var displayShips : Boolean = false
    private var grid : Grid? = null

    //add shot grid
    val gridChangeListener: BattleshipGrid.BattleshipGridListener =
        BattleshipGrid.BattleshipGridListener { grid, column, row ->
            //add shot updating
            if(grid[column, row].toByte() == (-127).toByte())
                gridVals = gridVals + ShotRef(column, row, 0)
            else if(grid[column, row].toByte() >= (0).toByte())
                gridVals  = gridVals + ShotRef(column, row, 1)
            else
                gridVals  = gridVals + ShotRef(column, row, 2)
            invalidate()
        }

    init {
        hitPaint = Paint().apply {
            // Controls the size of the dot
            strokeWidth = 25f
            strokeCap = Paint.Cap.SQUARE

            //set the paint color
            color = Color.RED
        }
        hitPaint.colorFilter = PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)
        missPaint = Paint().apply {
            // Controls the size of the dot
            strokeWidth = 10f
            strokeCap = Paint.Cap.SQUARE

            //set the paint color
            color = Color.WHITE
        }
        missPaint.colorFilter = PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
    }

    fun setGridRef(gridRef : Grid) {
        grid = gridRef
    }

    fun setGridSize(columns: Int, rows: Int) {
        gridSizeX = columns
        gridSizeY = rows
    }

    override fun onDraw(canvas: Canvas) {
        if(grid != null) {
            val canvasWidth = width.toFloat()
            val canvasHeight = height.toFloat()

            val shotsSep = if(gridSizeX >= gridSizeY) canvasWidth / gridSizeX else canvasHeight / gridSizeY

            if (displayShips) {
                for (ship in grid!!.opponent.ships) {
                    if (ship.topLeft.x == ship.bottomRight.x) {
                        for (i in ship.topLeft.y..ship.bottomRight.y) {
                            canvas.drawPoint(
                                ship.topLeft.x.toFloat() * shotsSep + shotsSep / 2,
                                i * shotsSep + shotsSep / 2,
                                hitPaint
                            )
                        }
                    } else {
                        for (i in ship.topLeft.x..ship.bottomRight.x) {
                            canvas.drawPoint(
                                i * shotsSep + shotsSep / 2,
                                ship.topLeft.y * shotsSep + shotsSep / 2,
                                hitPaint
                            )
                        }
                    }
                }
            }

            for (shot in gridVals) {
                val centerX = shot.column * shotsSep + shotsSep / 2
                val centerY = shot.row * shotsSep + shotsSep / 2
                canvas.drawBitmap(
                    bitmap.copy(bitmap.config, true), null,
                    Rect(
                        (centerX).toInt() - 25, (centerY).toInt() - 25,
                        (centerX).toInt() + 25, (centerY).toInt() + 25
                    ), if (shot.status == 0) missPaint else hitPaint)
            }
        }
    }

    fun displayShips() {
        displayShips = true
        invalidate()
    }

    fun hideShips() {
        displayShips = false
        invalidate()
    }

    private data class ShotRef(val column: Int, val row: Int, var status: Int)
}