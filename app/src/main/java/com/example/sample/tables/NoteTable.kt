package com.example.sample.tables

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.AsyncTask
import android.util.Log
import com.example.mysqlite.engine.Columns
import com.example.mysqlite.engine.DatabaseCreator
import com.example.mysqlite.engine.Table
import com.example.mysqlite.engine.getValueOf

/**
 * A class that extends table class, you can also create any
 * of your table by just extending the Table Class and define
 * your own methods using the methods of the super class
 * @see * @see "{@link com.example.mysqlite.engine.Table}table class.
 */
class NoteTable(database: DatabaseCreator?) : Table(
    database!!,
    NOTE_TABLE,
    Columns.primaryAutoIncrementColumn<Int>(ID),
    Columns.column<String>(COLUMN_DETAILS),
    Columns.column<String>(COLUMN_AUTHOR)
) {
    /**let Loaf the data on background thread using AsyncTask*/
    @SuppressLint("StaticFieldLeak")
    fun loadAllNotes(noteListener: LoadNoteListener) {
        object : AsyncTask<Void?, Void?, List<Note>>() {
            protected override fun doInBackground(vararg params: Void?): List<Note> {
                val notes = arrayListOf<Note>()
                getRows {
                    val id = getValueOf<Int>(ID)
                    // you can also use extention function to get the note
                    // directly like [getNoteFromRowValues()]
                    val note = getNoteFromRowValues()
                    noteListener.onNoteLoadProgress(note, id)
                    notes.add(note)
                }
                return notes
            }
            override fun onPostExecute(notes: List<Note>) {
                super.onPostExecute(notes)
                noteListener.onNoteLoaded(notes)
                Log.e(TAG, "onPostExecute")
            }
        }.execute()
    }

    private fun Note.getRowValuesFromNote(): ContentValues {
        val row_values = ContentValues()
        row_values.put(COLUMN_DETAILS, details)
        row_values.put(COLUMN_AUTHOR, author)
        return row_values
    }

    fun insertNote(note: Note) {
        insert(note.getRowValuesFromNote())
    }

    interface LoadNoteListener {
        fun onNoteLoadProgress(note: Note, id: Int?)
        fun onNoteLoaded(notes: List<Note>)
    }

    companion object {
        const val NOTE_TABLE = "Note"
        const val ID = "id"
        const val COLUMN_DETAILS = "details"
        const val COLUMN_AUTHOR = "author"
        const val TAG = "NoteTable"
    }
}

/**
 * Instead of getting values one by one everytime to populate NoteTable
 * just create an extention function to do that for Example inside then
 * loopAllTableRows function just call the method like this
 * [val note:Note = getNoteFromRowValues()]
 * and you are done!
 *
 * */
fun ContentValues.getNoteFromRowValues(): Note {
    val id = getValueOf<Int>(NoteTable.ID)
    val details = getValueOf<String>(NoteTable.COLUMN_DETAILS)
    val author = getValueOf<String>(NoteTable.COLUMN_AUTHOR)
    Log.e(NoteTable.TAG, "getNoteFromRowValues: ID= $id, details= $details, author= $author")
    return Note(id, details, author)
}
