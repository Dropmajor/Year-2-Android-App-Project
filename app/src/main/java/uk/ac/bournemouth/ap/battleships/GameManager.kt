package uk.ac.bournemouth.ap.battleships

import uk.ac.bournemouth.ap.battleshiplib.GuessResult
import uk.ac.bournemouth.ap.battleshiplogic.AIBehaviour
import uk.ac.bournemouth.ap.battleshiplogic.Grid
import uk.ac.bournemouth.ap.battleshiplogic.StudentOpponent
import uk.ac.bournemouth.ap.lib.matrix.ext.Coordinate
import kotlin.random.Random

//add the option to import preset ships
class GameManager(columns: Int, rows: Int, twoPlayers: Boolean, activity: Game) {
    private val player1Grid: Grid
    private val player2Grid: Grid
    private var turnCounter: Int = 0
    private val AIInstance : AIBehaviour?
    private val gameActivity : Game

    init{
        gameActivity = activity
        val ships = IntArray(5)
        ships[0] = 2
        ships[1] = 3
        ships[2] = 3
        ships[3] = 4
        ships[4] = 5
        val player1opponent = StudentOpponent(columns, rows, ships, Random);
        val player2opponent = StudentOpponent(columns, rows, ships, Random);
        player1Grid = Grid(columns, rows, player1opponent)
        player2Grid = Grid(columns, rows, player2opponent)
        if(!twoPlayers){
            AIInstance = AIBehaviour(columns, rows, player1Grid)
        }else
            AIInstance = null
    }

    //get the grid instances in the game
    fun getPlayerGrids(): List<Grid> {
        return listOf(player1Grid, player2Grid)
    }

    fun takeShot(column: Int, row: Int, player1: Boolean) : GuessResult? {
        var result : GuessResult? = null
        if(turnCounter % 2 == 0 && player1 && !player2Grid.checkCellSet(column, row)) {
            result = player2Grid.shootAt(column, row)
            if(result is GuessResult.SUNK) {
                if(player2Grid.isFinished) {
                    gameActivity.endGame()
                }
                else{
                    gameActivity.displayResultMSG(player2Grid.opponent.ships[result.shipIndex], false)
                }
            }
            else{
                gameActivity.displayResultMSG(column, row, result, false)
            }
            incrementTurn()

        }
        else if(turnCounter % 2 == 1 && !player1 && !player1Grid.checkCellSet(column, row)) {
            result = player1Grid.shootAt(column, row)
            if(result is GuessResult.SUNK) {
                if(player1Grid.isFinished) {
                    gameActivity.endGame()
                }
                else{
                    gameActivity.displayResultMSG(player1Grid.opponent.ships[result.shipIndex], true)
                }
            }
            else{
                gameActivity.displayResultMSG(column, row, result, true)
            }
            incrementTurn()
        }
        return result
    }


    private fun incrementTurn() {
        turnCounter++;
        if(turnCounter % 2 == 1 && AIInstance != null) {
            //have the AI take its turn
            val shotPoint = AIInstance.takeTurn()
            takeShot(shotPoint.x, shotPoint.y, false)?.let {
                AIInstance.passResult(shotPoint.x, shotPoint.y,
                    it
                )
            }
        }
    }
}