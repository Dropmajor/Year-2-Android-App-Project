package uk.ac.bournemouth.ap.battleships

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import uk.ac.bournemouth.ap.battleshiplib.*
import uk.ac.bournemouth.ap.battleshiplogic.Grid
import kotlin.random.Random

class Game : AppCompatActivity() {

    //store the coordinates of the users most recent input
    private var touchCoords : FloatArray = FloatArray(2)
    private var gameLogicInstance : GameManager? = null
    private var gridSize : Array<Int> = arrayOf(10, 10)
    private var twoPlayer = false
    private var player1Turn = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        val msg = intent.extras
        if (msg is Bundle) {
            gridSize[0] = msg.getInt("columns")
            gridSize[1] = msg.getInt("rows")
            twoPlayer = msg.getBoolean("two players")
        }
        val player1Grid = findViewById<GridView>(R.id.player1Grid)
        val player2Grid = findViewById<GridView>(R.id.player2Grid)

        val location = IntArray(2)

        player1Grid.setSize(gridSize[0], gridSize[1])
        player2Grid.setSize(gridSize[0], gridSize[1])

        val touchListener : View.OnTouchListener = View.OnTouchListener { v, e ->
            if (e != null) {
                touchCoords[0] = e.x
                touchCoords[1] = e.y
            }
            false
        }

        //
        val clickListener: View.OnClickListener = View.OnClickListener { view ->
            if(gameLogicInstance != null) {

                if(twoPlayer && view == player1Grid){
                    player1Grid.getLocationOnScreen(location)
                    val gridSquares = player1Grid.getGridSquare(touchCoords[0], touchCoords[1])
                    takeShot(gridSquares[0], gridSquares[1], false)
                } else if(view == player2Grid){
                    player2Grid.getLocationOnScreen(location)
                    val gridSquares = player2Grid.getGridSquare(touchCoords[0], touchCoords[1])
                    takeShot(gridSquares[0], gridSquares[1])
                }
            }
        }
        player1Grid.setOnTouchListener(touchListener)
        player1Grid.setOnClickListener(clickListener)

        player2Grid.setOnTouchListener(touchListener)
        player2Grid.setOnClickListener(clickListener)
    }

     fun startGame(view: View) {
         gameLogicInstance = GameManager(gridSize[0], gridSize[1], twoPlayer, this)
         val opponents: List<Grid> = gameLogicInstance!!.getPlayerGrids()
         val player1ShipView = findViewById<ShipView>(R.id.player1Ships)
         val player2ShipView = findViewById<ShipView>(R.id.player2Ships)
         findViewById<Button>(R.id.RandomPlace).visibility = View.GONE

         player1ShipView.setGridRef(opponents[0])
         opponents[0].addOnGridChangeListener(player1ShipView.gridChangeListener)
         player1ShipView.displayShips()
         player1ShipView.setGridSize(gridSize[0], gridSize[1])

         player2ShipView.setGridRef(opponents[1])
         opponents[1].addOnGridChangeListener(player2ShipView.gridChangeListener)
         player2ShipView.hideShips()
         player2ShipView.setGridSize(gridSize[0], gridSize[1])
     }

    fun endGame() {
        val turnIndicator = findViewById<TextView>(R.id.TurnIndicator)
        turnIndicator.text = "Game over"

        val player1ShipView = findViewById<ShipView>(R.id.player1Ships)
        val player2ShipView = findViewById<ShipView>(R.id.player2Ships)

        val opponents: List<Grid> = gameLogicInstance!!.getPlayerGrids()
        opponents[0].removeOnGridChangeListener(player1ShipView.gridChangeListener)
        opponents[1].removeOnGridChangeListener(player2ShipView.gridChangeListener)

        findViewById<ShipView>(R.id.player1Ships).displayShips()
        findViewById<ShipView>(R.id.player2Ships).displayShips()
    }

    fun switchTurn(view: View){
        val player1ShipView = findViewById<ShipView>(R.id.player1Ships)
        val player2ShipView = findViewById<ShipView>(R.id.player2Ships)
        if(player1Turn){
            player1ShipView.displayShips()
            player2ShipView.hideShips()
        }
        else{
            player2ShipView.displayShips()
            player1ShipView.hideShips()
        }
        findViewById<Button>(R.id.startTurn).visibility = View.GONE
    }

    private fun takeShot(column : Int, row : Int, player1: Boolean = true) {
        val startTurnButton = findViewById<Button>(R.id.startTurn)
        if(startTurnButton.visibility != View.VISIBLE){
            val result = gameLogicInstance?.takeShot(column, row, player1)
            if(result != null) {
                player1Turn = !player1Turn
                if(twoPlayer){
                    findViewById<ShipView>(R.id.player1Ships).hideShips()
                    findViewById<ShipView>(R.id.player2Ships).hideShips()
                    startTurnButton.visibility = View.VISIBLE
                }
            }
        }
    }

    fun displayResultMSG(column : Int, row : Int, result: GuessResult, USN: Boolean) {
        val shotText : TextView = if(USN) findViewById(R.id.player1GridResult)
                                    else findViewById(R.id.player2GridResult)
        if(result is GuessResult.MISS)
            shotText.text = "Shot fired at (" + column + "," + row + ")"
        else if(result is GuessResult.HIT)
            shotText.text = "Hit landed at (" + column + "," + row + ")"
    }

    fun displayResultMSG(sunkShip: Ship, USN: Boolean) {
        val shotText : TextView = if(USN) findViewById(R.id.player1GridResult)
                                    else findViewById(R.id.player2GridResult)
        shotText.text = getName(USN, sunkShip.size) + getTerm()
        //display ship icon
    }

    companion object {
        @JvmStatic
        val SINKING_TERMS = arrayOf(" has been stricken",
        " has capsized", "'s ammunition has detonated",
        " has been torpedoed", " has sunk")

        @JvmStatic
        val USN_SHIP_NAMES = arrayOf(
            arrayOf("Cuttlefish", "Gato", "Growler"), //submarines
            arrayOf("Atlanta", "Monaghan", "Phelps"), //destroyers/light cruisers
            arrayOf("New Orleans", "Northampton", "Portland"), //heavy cruiser names for the BB as there were no USN battleships present at midway
            arrayOf("Yorktown", "Enteprise", "Yorktown") //Aircraft carriers
        )

        private val IJN_SHIP_NAMES = arrayOf(
            arrayOf("I-156", "I-121", "I-162"), //submarines
            arrayOf("Fubuki", "Shirayuki", "Hatsuyuki"), //destroyers
            arrayOf("Fuso", "Haruna", "Nagato"), //Battleships
            arrayOf("Akagi", "Hiryu", "Hosho"), //aircraft carriers
        )

        fun getTerm(): String {
            return SINKING_TERMS[Random.nextInt(SINKING_TERMS.size - 1)]
        }

        fun getName(USN: Boolean, size: Int) : String {
            var name = ""
            name = if(USN)
                "USS " + USN_SHIP_NAMES[size - 2][Random.nextInt(USN_SHIP_NAMES[size-2].size - 1)]
            else
                "IJN " + IJN_SHIP_NAMES[size - 2][Random.nextInt(IJN_SHIP_NAMES[size-2].size - 1)]
            return name
        }
    }
}

