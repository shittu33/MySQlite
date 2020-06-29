package com.example.sample.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
//import com.example.mysqlite.engine.Table
import com.example.mysqlite.engine.*
import com.example.sample.R
import com.example.sample.activities.NoteActivity.Companion.COLUMN_DETAILS
import com.example.sample.activities.NoteActivity.Companion.COLUMN_AUTHOR
import com.example.sample.activities.NoteActivity.Companion.ID
import com.example.sample.activities.NoteActivity.Companion.noteTable
import com.example.sample.tables.Note
import kotlinx.android.synthetic.main.new_note_activity.*

class NewNoteActivity : AppCompatActivity() {
    var table: Table? = null
    var details: String? = null
    var author: String? = null
    var isForUpdate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_note_activity)
        init()
    }

    var note: Note? = null

    private fun init() {
        table = noteTable
        val extra = intent.getSerializableExtra("note")
        if (extra != null) {
            isForUpdate = true
            note = extra as Note
            edit_body.setText(note?.details)
            edit_author.setText(note?.author)
        } else
            isForUpdate = false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_note_save, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.save) {
            details = edit_body.text.toString()
            author = edit_author.text.toString()
            if (details!!.isBlank()) {
                Toast.makeText(this, "note details is empty please type something", LENGTH_SHORT)
                    .show()
                return super.onOptionsItemSelected(item)
            }
            if (author!!.isBlank()) {
                Toast.makeText(this, "note author is empty please type something", LENGTH_SHORT)
                    .show()
                return super.onOptionsItemSelected(item)
            }
            if (isForUpdate) {
                table!!.updateRow(
                    note!!.id,
                    COLUMN_DETAILS with details!!,
                    COLUMN_AUTHOR with author!!
                )
                setResult(UPDATE)
                finish()
            } else {
                table!!.insert(
                    author!! to COLUMN_AUTHOR
                    , details!! to COLUMN_DETAILS
                )
                setResult(SAVED)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(FAILED)
        finish()
    }

    fun Table.otherAlternatives(note: Note) {
            update(COLUMN_AUTHOR with "bukola"
                , COLUMN_DETAILS with "okay",condition= ID Is 1 or ID Is 6 and COLUMN_AUTHOR Is "john")
            update(
                COLUMN_AUTHOR with "yes"
                , COLUMN_DETAILS with "no"
                ,condition= ID exceeds 3 and COLUMN_DETAILS isEqual "okay"
                )
            deleteRow(5, condition = COLUMN_DETAILS Is "details")
            deleteRows(1, 3, 4, condition= COLUMN_DETAILS Is "okay")
            deleteWhen(ID isEqual 9 and COLUMN_AUTHOR Is "shittu")
    }


    companion object {
        const val SAVED = 111
        const val UPDATE = 211
        const val FAILED = 232
    }
}
