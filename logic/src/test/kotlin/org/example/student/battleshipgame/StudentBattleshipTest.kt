package org.example.student.battleshipgame

import uk.ac.bournemouth.ap.battleshiplib.*
import uk.ac.bournemouth.ap.battleshiplib.test.BattleshipTest
import uk.ac.bournemouth.ap.battleshiplogic.Grid
import uk.ac.bournemouth.ap.battleshiplogic.StudentOpponent
import uk.ac.bournemouth.ap.battleshiplogic.StudentShip
import uk.ac.bournemouth.ap.lib.matrix.boolean.BooleanMatrix
import kotlin.random.Random

class StudentBattleshipTest : BattleshipTest<StudentShip>() {
    override fun createOpponent(
        columns: Int,
        rows: Int,
        ships: List<StudentShip>
    ): StudentOpponent {
        return StudentOpponent(columns, rows, ships)
    }

    override fun transformShip(sourceShip: Ship): StudentShip {
        return StudentShip(sourceShip.topLeft.y, sourceShip.topLeft.x, sourceShip.bottomRight.y, sourceShip.bottomRight.x)
    }

    override fun createOpponent(
        columns: Int,
        rows: Int,
        shipSizes: IntArray,
        random: Random
    ): StudentOpponent {
        // Note that the passing of random allows for repeatable testing
        return StudentOpponent(columns, rows, shipSizes, random)
    }

    override fun createGrid(
        grid: BooleanMatrix, opponent: BattleshipOpponent): Grid {
        // If the opponent is not a StudentBattleshipOpponent, create it based upon the passed in data
        val studentOpponent =
            opponent as? StudentOpponent
                ?: createOpponent(opponent.columns, opponent.rows, opponent.ships.map { it as? StudentShip ?: transformShip(it) })
        return Grid(grid.width, grid.height, studentOpponent)
    }
}