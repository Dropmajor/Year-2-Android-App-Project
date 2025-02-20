package uk.ac.bournemouth.ap.battleshiplogic

import uk.ac.bournemouth.ap.battleshiplib.Ship
import uk.ac.bournemouth.ap.lib.matrix.ext.Coordinate

data class StudentShip (override val top: Int, override val left: Int, override val bottom: Int, override val right: Int) :
    Ship {
    override val topLeft: Coordinate get() = Coordinate(x = left, y = top)
    override val bottomRight: Coordinate get() = Coordinate(x = right, y = bottom)
}