package com.example.sample.tables

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.AsyncTask
import android.util.Log
import com.example.mysqlite.engine.DatabaseCreator
import com.example.mysqlite.engine.Table

/**
 * A class that extends table class, you can also create any
 * of your table by just extending the Table Class and define
 * your own methods using the methods of the super class
 * @see * @see "{@link com.example.mysqlite.engine.Table}table class.
 */
class NoteTable(database: DatabaseCreator?) : Table(
    database,
    NOTE_TABLE,
    getIntegerColumn_with_PrimaryKey_AutoIncrementStatement(ID),
    getString_ColumnStatement(COLUMN_DETAILS),
    getString_ColumnStatement(COLUMN_AUTHOR)
) {
    private val idIndex = 0
    private val detailsIndex = 1
    private val authorIndex = 2

    @SuppressLint("StaticFieldLeak")
    fun loadAllNotes(noteListener: LoadNoteListener) {
        object : AsyncTask<Void?, Void?, List<Note>>() {
            protected override fun doInBackground(vararg params: Void?): List<Note> {
                val notes = arrayListOf<Note>()
                loop_all_table_rows { row_values ->
                    if (row_values == null) {
                        Log.e(TAG,"onDataAddedProgress: " + "Why is row_values null?"
                        )
                    } else {
                        val id = row_values.getAsInteger(ID)
                        Log.e(TAG,"onDataAddedProgress: " + "isn't null"
                        )
                        val note =getNoteFromRowValues(row_values)
                        Log.e(TAG,"onNoteLoadProgress: $note"
                        )
                        //Log.e(TAG, "onDataAddedProgress: " + id);
                        noteListener.onNoteLoadProgress(note, id)
                        notes.add(note)
                    }
                }
                return notes
            }

            override fun onPostExecute(notes: List<Note>) {
                super.onPostExecute(notes)
                noteListener.onNoteLoaded(notes)
            }
        }.execute()
    }


    private fun getNoteFromRowValues(row_values: ContentValues): Note {
        val id = row_values.getAsInteger(ID)
        val details = row_values.getAsString(COLUMN_DETAILS)
        val author = row_values.getAsString(COLUMN_AUTHOR)
        Log.e(
            TAG, "getNoteFromRowValues: ID= " + id
                    + ", details= " + details
                    + ", author= " + author
        )
        return Note(id, details, author)
    }

    private fun getRowValuesFromNote(note: Note): ContentValues {
        val row_values = ContentValues()
        row_values.put(COLUMN_DETAILS, note.details)
        row_values.put(COLUMN_AUTHOR, note.author)
        return row_values
    }

    fun insertNote(note: Note) {
        insert_values_to_columns(getRowValuesFromNote(note))
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
        private const val TAG = "NoteTable"
    }
}