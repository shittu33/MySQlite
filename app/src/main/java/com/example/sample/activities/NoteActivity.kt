package com.example.sample.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mysqlite.engine.DatabaseCreator
import com.example.mysqlite.engine.Table
import com.example.mysqlite.utils.DatabaseUtils
import com.example.sample.R
import com.example.sample.adapter.NoteAdapter
import com.example.sample.tables.Note
import com.example.sample.tables.NoteTable
import com.example.sample.tables.NoteTable.LoadNoteListener
import kotlinx.android.synthetic.main.activity_note.*

class NoteActivity : AppCompatActivity() {
    private var noteAdapter: NoteAdapter? = null
    private val recyclerview: RecyclerView? = null
    private var noteTable: NoteTable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        init()
    }

    private fun init() {
        initMySqlite()
        initAdapters()
        populate_data()
    }


    private fun initAdapters() {
        noteAdapter = NoteAdapter()
        val l_manager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView!!.layoutManager = l_manager
        recyclerview!!.adapter = noteAdapter
    }

    private fun initMySqlite() {
//         To create an in memory(temporary database) do something like this
//        val databaseCreator = DatabaseCreator(this, null, 1)
        //         To create the database inside a file do something like this
//        val databaseCreator1 = DatabaseCreator(this
//            ,DatabaseUtils.getDatabasePath("Sample01app","note_database.db")
//            , null, 1)
        //         To create a normal database with a name
        val databaseCreator = DatabaseCreator(this, "example.db", 1)

        /**You can create a table by directly creating the object of table class
         * like i did for the commented code below
         */
//        var noteTable = Table(databaseCreator
//            ,"note"
//            ,Table.getIntegerColumn_with_PrimaryKey_AutoIncrementStatement(NoteTable.ID)
//            ,Table.getString_ColumnStatement(NoteTable.COLUMN_DETAILS),
//            Table.getString_ColumnStatement(NoteTable.COLUMN_AUTHOR))
        /**You can create a table by extending the table class like i did
         * @see "{@link com.example.sample.tables.NoteTable}
         *
         */
        noteTable = NoteTable(databaseCreator)
    }

    private fun populate_data() {
        noteTable!!.loadAllNotes(object : LoadNoteListener {

            override fun onNoteLoaded(notes: List<Note>) {
                noteAdapter!!.submitList(notes)
            }

            override fun onNoteLoadProgress(note: Note, id: Int?) {
                //if you wish to do something with each note loaded
                //you can also update a counter here, probably with
                // the id.
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_item, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add) {
            //Add a new note
            startActivity(Intent(this,NoteActivity::class.java))
            noteTable!!.insertNote(Note(3, "me", "this is just a test"))
            populate_data()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG = "NoteActivity"
    }
}