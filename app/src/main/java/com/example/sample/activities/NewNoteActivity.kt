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
import com.example.sample.activities.NoteActivity.Companion.noteTable
import com.example.sample.tables.Note
import com.example.sample.tables.NoteTable
import kotlinx.android.synthetic.main.new_note_activity.*

class NewNoteActivity : AppCompatActivity() {
    var table: Table? = null
    var details: String? = null
    var author: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_note_activity)
        init()
    }

    private fun init() {
        table = noteTable
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
                Toast.makeText(this,"note details is empty please type something",LENGTH_SHORT).show()
                return  super.onOptionsItemSelected(item)
            }
            if (author!!.isBlank()) {
                Toast.makeText(this,"note author is empty please type something",LENGTH_SHORT).show()
                return  super.onOptionsItemSelected(item)
            }
            table!!.saveNote(Note(details = details!!, author = author!!))
            Toast.makeText(this,"note saved",LENGTH_SHORT).show()
            setResult(SAVED)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(FAILED)
        finish()
    }
    fun Table.saveNote(note: Note) {
        table!!.apply {
            insert(
                note.author to NoteTable.COLUMN_AUTHOR
                , note.details to NoteTable.COLUMN_DETAILS
            )
//            updateRow(1, COLUMN_AUTHOR with "men"
//                ,condition="author" Is "shittu")
//            update(COLUMN_AUTHOR with "bukola"
//                , COLUMN_DETAILS with "okay",condition= "id" Is 1 or "id" Is 6)
//            update(
//                COLUMN_AUTHOR with "yes"
//                , COLUMN_DETAILS with "no"
//                ,condition= ID exceeds 3 and COLUMN_DETAILS isEqual "okay"
//                )
//            deleteRow(5, condition = "details" Is "details")
//            deleteRows(1, 3, 4, condition= "details" Is "okay")
//            deleteWhen("id" isEqual 9 and "author" Is "shittu")
        }
    }


    companion object {
        const val SAVED = 111
        const val FAILED = 232

    }
}
