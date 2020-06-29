package com.example.sample.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.mysqlite.engine.*
import com.example.mysqlite.utils.MyDatabaseUtils
import com.example.sample.R
import com.example.sample.activities.NewNoteActivity.Companion.SAVED
import com.example.sample.activities.NewNoteActivity.Companion.UPDATE
import com.example.sample.adapter.NoteAdapter
import com.example.sample.tables.Note
import com.example.sample.tables.NoteTable
import com.example.sample.tables.NoteTable.LoadNoteListener
import kotlinx.android.synthetic.main.activity_note.*

class NoteActivity : AppCompatActivity(), NoteAdapter.ItemListener {
    private var noteAdapter: NoteAdapter? = null
    private var customNoteTable: NoteTable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        initDatabase()
        initAdapters()
        populateData()
    }

    private fun initDatabase() {
        val myDb = createDatabase { name = "example.db"; version = 1 }
        noteTable =
            myDb createTable { "Note" autoPrimaryColumn NoteTable.ID stringColumn COLUMN_DETAILS stringColumn COLUMN_AUTHOR }
    }

    private fun populateData() {
        val notes = arrayListOf<Note>()
        noteTable!!.apply {
//            getRows( "id" exceeds 2 /*and "author" Is "shittu"*/ orderByDESC "id" limit 1000) {
            getRows {
                //Use the  extension function getValueOf(columnName)
                val id = getValueOf<Int>(NoteTable.ID)
                val details = getValueOf<String>(COLUMN_DETAILS)
                val author = getValueOf<String>(COLUMN_AUTHOR)
                val note = Note(id, author, details)
                notes.add(note)
            }
            noteAdapter!!.submitList(notes)
        }
    }

    private fun initAdapters() {
        noteAdapter = NoteAdapter()
        noteAdapter!!.setItemListener(this)
        val lManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        // serving the layout manager  and adapter to our Recyclerview
        recyclerView?.apply {
            layoutManager = lManager
            adapter = noteAdapter
        }
        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: ViewHolder,
                target: ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                val note = noteAdapter!!.getNote(viewHolder.adapterPosition)
                noteTable!!.deleteRow(note!!.id)
            }
        }).attachToRecyclerView(recyclerView)

    }


    override fun onClick(v: View?, Note: Note?, pos: Int) {
        val intent = Intent(this, NewNoteActivity::class.java)
        intent.putExtra("note", Note)
        startActivityForResult(intent, REQUESTCODE_UPDATE)
    }

    override fun onLongClick(v: View?, Note: Note?, pos: Int) {
        val popupMenu = PopupMenu(v?.context, v)
        popupMenu.inflate(R.menu.activity_menu_option)
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener {
            if (it.itemId == R.id.delete) {
                noteTable!!.deleteRow(Note!!.id)
            } else if (it.itemId == R.id.update) {
                val intent = Intent(this, NewNoteActivity::class.java)
                intent.putExtra("note", Note)
                startActivityForResult(intent, REQUESTCODE_UPDATE)
            }
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_item, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add) {
            val intent = Intent(this, NewNoteActivity::class.java)
            startActivityForResult(intent, REQUESTCODE_SAVE)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUESTCODE_SAVE) {
            if (resultCode == SAVED) {
                populateData()
                Toast.makeText(this, "note saved", LENGTH_SHORT).show()
            }
        } else if (requestCode == REQUESTCODE_UPDATE) {
            if (resultCode == UPDATE) {
                populateData()
                Toast.makeText(this, "note updated", LENGTH_SHORT).show()
            }
        } else Toast.makeText(this, "Oops! No note was save!!", LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "NoteActivity"
        const val ID = "id"
        const val COLUMN_AUTHOR = "author"
        const val COLUMN_DETAILS = "details"
        const val REQUESTCODE_SAVE = 999
        const val REQUESTCODE_UPDATE = 888
        var noteTable: Table? = null

    }


    /**create database and table directly from the DatabaseCreator
     * and table class respectively
     * */
    private fun initDatabaseNormal() {
        val myDb = DatabaseCreator(this, "example.db", 0)
        noteTable = Table(
            myDb
            , "note"
            , Columns.primaryAutoIncrementColumn<Int>(NoteTable.ID)
            , Columns.column<String>(COLUMN_DETAILS)
            , Columns.column<String>(COLUMN_AUTHOR)
//            , Columns.customColumn("age INTEGER PRIMARY KEY AUTOINCREMENT")
//            , Columns.uniqueColumn<String>("author")
//            , Columns.customColumn(ID.integer().primaryKey().autoIncrement())
//            , Columns.customColumn(COLUMN_AUTHOR.varchar())
//            , Columns.customColumn(COLUMN_DETAILS.varchar())
        )

    }

    /**If you prefer a tree like structure
     *
     * */
    private fun initDatabaseInTreeLikeStructure() {
        database {
            version = 1
            db_name = "example.db"
            noteTable =
                table {
                    tableName = "Note"
                    columns {
                        autoPrimaryColumn<Int> { NoteTable.ID }
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
    private fun initDatabaseByExtendingTableClass() {
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

    private fun Table.OtherAlternativesToPopulateData() {
        //This is useful if all you need is just a row
        getRow(2) {
            val note = Note(
                //here we use name of each column as a key to retreive
                // their values for each row.
                id = getValueOf(ID),
                author = getValueOf(COLUMN_AUTHOR),
                details = getValueOf(COLUMN_DETAILS)
            )
        }
        //This is useful if all you need is just a row from a specific column
//        val details = getRow<String>(ValueColumnPair(3,COLUMN_DETAILS))
        val details = getRow<String>(3 from COLUMN_DETAILS)
        val note = Note(id = 3, author = "you", details = details)

        //This is useful if all you need is just some few rows
        getSpecificRows(
            2, 3, 4, 18
            , condition = COLUMN_AUTHOR Is "me" orderByDESC "id"
        ) { it ->
            //The "it" receiver passed the name of column loaded as an arrayOfNull
            //it[0] is "id", it[1] is "author", it[2] is "details"
            val note = Note(
                id = getValueOf(it[0])
                , author = getValueOf(it[1])
                , details = getValueOf(it[2])
            )
        }
        //This is useful if all you need is get data from some specified column
        getRowsOfColumn(
            ID,
            COLUMN_AUTHOR,
            condition = "author" Is "shittu" orderByASC "id" limit 4
        ) { it ->
            //The "it" receiver passed the name of column loaded as an arrayOfNull
            //it[0] is "id", it[1] is "author"
            val note = Note(
                id = getValueOf(it[0])
                , author = getValueOf(it[1])
                , details = "not from database"
            )
        }
        getRowsOfColumn(
            ID,
            COLUMN_AUTHOR,
            COLUMN_DETAILS,
            condition = "id" lessThan 13 or "id" Is 4 orderByDESC "id" limit 4
        ) { it ->
            //The "it" receiver passed the name of column loaded as an arrayOfNull
            //it[0] is "id", it[1] is "author", it[2] is "details"
            val note = Note(
                id = getValueOf(it[0])
                , author = getValueOf(it[1])
                , details = getValueOf(it[2])
            )
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


}