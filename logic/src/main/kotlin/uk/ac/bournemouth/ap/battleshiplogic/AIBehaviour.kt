package uk.ac.bournemouth.ap.battleshiplogic

import uk.ac.bournemouth.ap.battleshiplib.GuessCell
import uk.ac.bournemouth.ap.battleshiplib.GuessResult
import uk.ac.bournemouth.ap.lib.matrix.ext.Coordinate
import kotlin.random.Random

class AIBehaviour(private val columns: Int, private val rows: Int, private val enemyGrid: Grid) {
    //store all hits the AI has landed that hasn't led to a sunk ship
    private var unresolvedHits : MutableList<MutableList<Coordinate>> = mutableListOf()

    fun takeTurn() : Coordinate {
        var x: Int
        var y: Int
        if(unresolvedHits.isNotEmpty()) {
            for(shipHit in unresolvedHits) {
                var direction = 0
                if(shipHit.size > 1){
                    //check for directional facing of the ship
                    direction = if(shipHit[0].x == shipHit[1].x) 1 else 2
                }

                val iRange = if(direction == 1) 0.. 0 else -1.. 1
                val jRange = if(direction == 2) 0.. 0 else -1.. 1

                for(hit in shipHit) {
                    for(i in iRange){
                        if(i == 0 || (hit.x + i < 0 || hit.x + i > columns - 1))
                            continue
                        else if(enemyGrid[hit.x + i, hit.y] == GuessCell.UNSET) {
                            x = hit.x + i
                            y = hit.y
                            return Coordinate(x, y)
                        }
                    }
                    for(j in jRange){
                        if(j == 0 || hit.y + j < 0 || hit.y + j > rows - 1)
                            continue
                        else if(enemyGrid[hit.x, hit.y + j] == GuessCell.UNSET) {
                            x = hit.x
                            y = hit.y + j
                            return Coordinate(x, y)
                        }
                    }
                }
            }
        }
        do {
            x = Random.nextInt(columns - 1)
            y = Random.nextInt(rows - 1)
        } while (enemyGrid[x, y] != GuessCell.UNSET)
        return Coordinate(x, y)
    }

    fun passResult(column: Int, row: Int, result: GuessResult)
    {
        if(result !is GuessResult.MISS)
        {
            var index = 0
            var found = false
            if(result is GuessResult.HIT) {
                for(shipHit in unresolvedHits) {
                    val hit = enemyGrid[shipHit[0].x, shipHit[0].y]
                    if(hit is GuessCell.HIT && result.shipIndex == hit.shipIndex) {
                        unresolvedHits[index] += Coordinate(column, row)
                        found = true
                        break
                    }
                    index++
                }
                if(!found)
                    unresolvedHits += mutableListOf(Coordinate(column, row))
            }
            else if(result is GuessResult.SUNK) {
                for(shipHit in unresolvedHits) {
                    val hit = enemyGrid[shipHit[0].x, shipHit[0].y]
                    if(hit is GuessCell.SUNK && result.shipIndex == hit.shipIndex) {
                        found = true
                        break
                    }
                    index++
                }
                if(found)
                    unresolvedHits.removeAt(index)
            }
        }
    }
}