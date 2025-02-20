package uk.ac.bournemouth.ap.battleships

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView

class ConfigureGame : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configure_game)

        // Declaring and Initializing  edit text from the layout
        val columnInput = findViewById<EditText>(R.id.columnCount)
        val rowInput = findViewById<EditText>(R.id.rowCount)

        // Assigning filters
        columnInput.filters = arrayOf<InputFilter>(sizeFilter())
        rowInput.filters = arrayOf<InputFilter>(sizeFilter())
    }

    fun startGame(view: View) {
        val columns = findViewById<EditText>(R.id.columnCount).text.toString().toInt()
        val rows = findViewById<EditText>(R.id.rowCount).text.toString().toInt()
        //check if the total number of cells is sufficient to accomodate a game
        if(columns * rows >= 25){
            val bundle = Bundle()
            bundle.putInt("columns", columns)
            bundle.putInt("rows", rows)
            bundle.putBoolean("two players", findViewById<CheckBox>(R.id.twoplayers).isChecked)
            val intent = Intent(this, Game::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        else{
            findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
        }
    }

    inner class sizeFilter() : InputFilter {
        override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dStart: Int, dEnd: Int): CharSequence? {
            try {
                val input = Integer.parseInt(dest.toString() + source.toString())
                if (input in 1.. 15) {
                    return null
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
            return ""
        }
    }
}