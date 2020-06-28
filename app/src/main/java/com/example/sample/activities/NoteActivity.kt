package com.example.sample.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mysqlite.engine.*
import com.example.mysqlite.utils.MyDatabaseUtils
import com.example.sample.R
import com.example.sample.activities.NewNoteActivity.Companion.SAVED
import com.example.sample.adapter.NoteAdapter
import com.example.sample.tables.Note
import com.example.sample.tables.NoteTable
import com.example.sample.tables.NoteTable.Companion.COLUMN_AUTHOR
import com.example.sample.tables.NoteTable.Companion.COLUMN_DETAILS
import com.example.sample.tables.NoteTable.Companion.ID
import com.example.sample.tables.NoteTable.LoadNoteListener
import kotlinx.android.synthetic.main.activity_note.*

class NoteActivity : AppCompatActivity() {
    private var noteAdapter: NoteAdapter? = null
    private var customNoteTable: NoteTable? = null
    var context: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        context = this;
//        initDatabase2()
//        initDatabase3()
        initDatabase4()
//        initDatabase()
        initAdapters()
        populateData()
    }

    private fun initAdapters() {
        noteAdapter = NoteAdapter()
        val lManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        // serving the layout manager  and adapter to our Recyclerview
        recyclerView?.apply {
            layoutManager = lManager
            adapter = noteAdapter
        }
    }

    private fun initDatabase4() {
        val myDb = createDatabase { name = "example.db"; version = 1 }
        noteTable =
            myDb createTable { "Note" autoPrimaryColumn ID stringColumn COLUMN_DETAILS stringColumn COLUMN_AUTHOR }
    }

    /**If you prefer a tree like structure
     *
     * */
    private fun initDatabase() {
        database {
            version = 1
            db_name = "example.db"
            table {
                noteTable = this
                tableName = "Note"
                columns {
                    autoPrimaryColumn<Int> { ID }
                    column<String> { COLUMN_DETAILS }
                    column<String> { COLUMN_AUTHOR }
//                    Columns.customColumn("age INTEGER PRIMARY KEY AUTOINCREMENT")
//                    customColumn { "age".integer().primaryKey().autoIncrement()}
//                    primaryColumn<Int> { ID }
                }
            }
//            table{...}
//            table{...}
        }
    }

    /**This is not bad either if you wish customise your database
     * operation function just create another class that extend
     * from the table class like i did
     * @see "{@link com.example.sample.tables.NoteTable}
     *
     * */
    private fun initDatabase2() {
        /**To create an in memory(temporary database) do something like this
        val databaseCreator = DatabaseCreator(this, null, 1)
         */
//        To create the database inside a sdCard do something like this
        val databaseCreator = DatabaseCreator(
            this
            , MyDatabaseUtils.getDatabasePath("Sample01app", "note_database.db")
            , null, 1
        )
        /**You can
         *
         */
        customNoteTable = NoteTable(databaseCreator)
    }

    /**This should get you started
     *
     * */
    private fun initDatabase3() {
        val myDb = createDatabase { name = "example.db"; version = 1 }
        noteTable = Table(
            myDb
            , "note"
            , Columns.primaryAutoIncrementColumn<Int>(ID)
            , Columns.column<String>(COLUMN_DETAILS)
            , Columns.column<String>(COLUMN_AUTHOR)
//            , Columns.customColumn("age INTEGER PRIMARY KEY AUTOINCREMENT")
//            , Columns.uniqueColumn<String>("author")
//            , Columns.customColumn(ID.integer().primaryKey().autoIncrement())
//            , Columns.customColumn(COLUMN_AUTHOR.varchar())
//            , Columns.customColumn(COLUMN_DETAILS.varchar())
        )

    }


    private fun populateData() {
        val notes = arrayListOf<Note>()
        noteTable!!.apply {
//            getRow(2) {
//            val note = Note(id=3, author="you", details= getValueOf("details"))
//                val details = getRow<String>(3 from "details")
//                val note = Note(id = 3, author = "you", details = details)
//            getSpecificRows(2, 3, 4,18,condition= "author" Is "me" orderByDESC "id") {
//            getRowsOfColumn("id","author",condition= "author" Is "shittu" orderByASC "id" limit 4){
//                val note = Note(id=getValueOf(it[0])
//                    , author=getValueOf(it[1])
//                    , details= "okay")
//            }
            getRows("id" exceeds 2 /*and "author" Is "shittu"*/ orderByDESC "id" limit 1000) {
//                        getRowsOfColumn(
//                            "id","author","details",
//                            condition="id" lessThan  13 or "id" Is 4 orderByDESC "id" limit 4
//                        ) { columns ->
//                            val note = Note(id=getValueOf(columns[0])
//                                , author=getValueOf(columns[1])
//                                , details= getValueOf(columns[2]))
//                        }
//            getRows {
                //Use the  extention function getValueOf(columnName)
                val id = getValueOf<Int>(ID)
                val details = getValueOf<String>(COLUMN_DETAILS)
                val author = getValueOf<String>(COLUMN_AUTHOR)
                val note = Note(id, details, author)
                notes.add(note)
            }
            noteAdapter!!.submitList(notes)
        }
    }

    private fun populateDataFromCustomTable() {
        customNoteTable!!.loadAllNotes(object : LoadNoteListener {
            override fun onNoteLoaded(notes: List<Note>) {
                noteAdapter!!.submitList(notes)
                Log.e(TAG, "notes submited")
            }

            override fun onNoteLoadProgress(note: Note, id: Int?) {
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_item, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add) {
            val intent = Intent(this, NewNoteActivity::class.java)
            startActivityForResult(intent, REQUESTCODE)
//            noteTable!!.saveNote(Note(details = "details", author = "me"))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUESTCODE)
            if (resultCode == SAVED)
                populateData()
            else Toast.makeText(this,"Oops! No note was save!!",LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "NoteActivity"
        private const val REQUESTCODE = 999
        var noteTable: Table? = null

    }
}