package uk.ac.bournemouth.ap.battleshiplogic

import uk.ac.bournemouth.ap.battleshiplib.*

class Grid(override val columns: Int,
           override val rows: Int,
           override val opponent: BattleshipOpponent,
           override val shipsSunk: BooleanArray): BattleshipGrid {


    //track if a shot has been firced into this grid
    private var cells : Array<Array<GuessCell>> = Array(columns) { Array(rows) { GuessCell.UNSET }}
    private val gridChangeListeners = mutableListOf<BattleshipGrid.BattleshipGridListener>()

    constructor(columns: Int, rows: Int, opponent: BattleshipOpponent) :
            this(columns, rows, opponent, BooleanArray(opponent.ships.size))

    override fun get(column: Int, row: Int): GuessCell {
        return cells[column][row]
    }

    override fun shootAt(column: Int, row: Int): GuessResult {
        if(checkCellSet(column, row)) {
            throw java.lang.Exception("Duplicate moves aren't allowed")
        }
        val foundShip = opponent.shipAt(column, row)
        cells[column][row] = if(foundShip != null) GuessCell.HIT(foundShip.index) else GuessCell.MISS

        if(foundShip != null) {
            var sunk = true
            foundShip.ship.forEachIndex { x, y ->
                if(this[x, y] !is GuessCell.HIT)
                    sunk = false
            }

            if(sunk) {
                shipsSunk[foundShip.index] = true
                for(indice in foundShip.ship.columnIndices ) {
                    cells[indice][foundShip.ship.top] = GuessCell.SUNK(foundShip.index)
                }
                for(row in foundShip.ship.rowIndices ) {
                    cells[foundShip.ship.left][row] = GuessCell.SUNK(foundShip.index)
                }
            }
        }
        fireGridChange(column, row)
        return if(foundShip != null) {
            when (cells[column][row]) {
                is GuessCell.HIT -> GuessResult.HIT(foundShip.index)
                else -> GuessResult.SUNK(foundShip.index)
            }
        } else
            GuessResult.MISS
    }

    fun checkCellSet (column: Int, row: Int) : Boolean{
        return cells[column][row] != GuessCell.UNSET
    }

    override fun addOnGridChangeListener(listener: BattleshipGrid.BattleshipGridListener) {
        if (listener !in gridChangeListeners) {
            gridChangeListeners.add(listener)
        }
    }

    private fun fireGridChange(column: Int, row: Int) {
        for(listener in gridChangeListeners) {
            listener.onGridChanged(this, column, row)
        }
    }

    override fun removeOnGridChangeListener(listener: BattleshipGrid.BattleshipGridListener) {
        gridChangeListeners.remove(listener)
    }
}