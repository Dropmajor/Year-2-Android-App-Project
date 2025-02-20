package uk.ac.bournemouth.ap.battleshiplogic

import uk.ac.bournemouth.ap.battleshiplib.BattleshipOpponent
import uk.ac.bournemouth.ap.battleshiplib.Ship
import uk.ac.bournemouth.ap.battleshiplib.forEachIndex
import uk.ac.bournemouth.ap.lib.matrix.ext.Coordinate

class StudentOpponent (override val columns: Int, override val rows: Int, override var ships: List<Ship>,
) : BattleshipOpponent {

    constructor(columns: Int, rows: Int, shipSizes: IntArray, seed: kotlin.random.Random) :
            this (columns, rows, autoCreateShips(columns, rows, shipSizes, seed))

    init {
        for (ship in ships) {
            val shipList = ships.toMutableList()
            shipList.remove(ship)
            if(checkIntersect(ship.topLeft, ship.bottomRight, shipList) || !checkValid(ship, columns, rows)) {
                throw Exception("Ships were invalid")
            }
        }
    }

    override fun shipAt(column: Int, row: Int): BattleshipOpponent.ShipInfo<Ship>? {
        for ((shipIndex, ship) in ships.withIndex()) {
            if((column >= ship.left && column <= ship.right && row == ship.top) ||
                (row >= ship.top && row <= ship.bottom && column == ship.left)){
                return BattleshipOpponent.ShipInfo(shipIndex, ship)
            }
        }
        return null
    }

    companion object {
        private fun autoCreateShips(columns: Int, rows: Int, shipSizes: IntArray, seed: kotlin.random.Random): List<Ship> {
            var ships: MutableList<Ship> = mutableListOf()
            shipSizes.sort();
            shipSizes.reverse();
            var index = 0
            while (index < shipSizes.size) {
                val size = shipSizes[index]
                var x: Int
                var y: Int

                var vertical: Boolean = seed.nextBoolean()
                if ((vertical && size > rows) || !vertical && size > columns)
                    vertical = !vertical

                var attempts = 0
                do {
                    if (size <= rows && size <= columns)
                        vertical = seed.nextBoolean()
                    if(vertical) {
                        x = seed.nextInt(columns - 1)
                        y = if(size == rows) 0 else seed.nextInt(rows - size)
                    }
                    else {
                        x = if(size == columns) 0 else seed.nextInt(columns - size)
                        y = seed.nextInt(rows - 1)
                    }
                    attempts++
                } while (checkIntersect(Coordinate(x, y), Coordinate(
                            x + ((size - 1) * (if (!vertical) 1 else 0)),
                            y + ((size - 1) * (if (vertical) 1 else 0))), ships) && attempts < 25)

                if (attempts < 25) {
                    ships += StudentShip(y, x,
                        y + ((size - 1) * (if (vertical) 1 else 0)),
                        x + ((size - 1) * (if (!vertical) 1 else 0)))
                    index++
                } else {
                    //backtrack if too many attempts at placing a ship have been made as chances are after 25 tries a
                    //layout has been made where you cant fit anything
                    index -= 1
                    ships.removeAt(index)
                }
            }
            return ships
        }

        private fun checkIntersect(topLeft: Coordinate, bottomRight: Coordinate, existingShips: List<Ship>): Boolean {
            for(ship in existingShips){
                ship.forEachIndex { column, row ->
                    if((column >= topLeft.x && column <= bottomRight.x && row == topLeft.y) ||
                        (row >= topLeft.y && row <= bottomRight.y && column == topLeft.x)){
                        return true
                    }
                }
            }
            return false
        }

        private fun checkValid(ship: Ship, columns: Int, rows: Int) : Boolean {
            if(ship.left !in 0 until columns || ship.top !in 0 until rows ||
                ship.right !in 0 until columns || ship.bottom !in 0 until rows ||
                (ship.left != ship.right && ship.top != ship.bottom) ||
                (ship.left > ship.right || ship.top > ship.bottom)){
                return false
            }
            return true
        }
    }
}