
# MYSQLITE

MySqlite is a simple Android library that help you write a readable, yet type-safe code for Sqlite CRUD Operations (Create,Read,update,delete).

## Installation
[![](https://jitpack.io/v/shittu33/MySQlite.svg)](https://jitpack.io/#shittu33/MySQlite)

    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }

    dependencies {
            implementation 'com.github.shittu33:MySQlite:v1.0'
    }


## Usage

#### To create your database, simply write this one line of code

```kotlin

  val myDb = createDatabase { name = "example.db"; version = 1 }
```

#### To create a Table (named Note), write this simple and readable code
```Kotlin
    noteTable:Table =
     myDb createTable { "Note" autoPrimaryColumn ID stringColumn COLUMN_DETAILS stringColumn COLUMN_AUTHOR }
```

### To insert Data to database:

``` Kotlin
 noteTable!!.insert(
        author!! to COLUMN_AUTHOR
        ,details!! to COLUMN_DETAILS
 )
     
```

#### To populate your note list from the database, use the table object to call getRows function

```Kotlin
noteTable.getRows {
     val id = getValueOf<Int>(ID)
     val details = getValueOf<String>(COLUMN_DETAILS)
     val author = getValueOf<String>(COLUMN_AUTHOR)
     val note = Note(id, author, details)
     notes.add(note)
}
```

#### To update a row the noteTable try one of the amazing functions

```Kotlin
 noteTable!!.updateRow(
    note!!.id,
    COLUMN_DETAILS with details!!,
    COLUMN_AUTHOR with author!!
)

```
#### To delete a row with some condition from the database

```Kotlin
    noteTable!!.deleteRow(Note!!.id, condition = COLUMN_AUTHOR Is "john")
```

###
And boom! you successfully created, insert,read,update and delete from your database.There are still handful of other functions to perform this operations

#### Example of Other handy Functions

### Query functions
#### Get row 2 with no condition:
```Kotlin
getRow(2) {...}
```
#### Get row 3 from column details:
```Kotlin
val details = getRow<String>(3 from COLUMN_DETAILS)
``` 
#### Get a specific rows 2,3,4, and 18 with a condition:
```Kotlin
getSpecificRows(
  2, 3, 4, 18
  , condition = COLUMN_AUTHOR Is "me" orderByDESC "id"
  ) {...}
```

#### Get 4 rows from column ID, and author only, if id is less than 13 and author is john.
```Kotlin
 getRowsOfColumn(
       ID
       ,COLUMN_AUTHOR
        condition = ID lessThan 13 and COLUMN_AUTHOR Is "john" orderByDESC ID limit 4
 ) {...}
```
### Update Functions

```Kotlin
  update(
      COLUMN_AUTHOR with "bukola"
      , COLUMN_DETAILS with "okay"
      ,condition= ID Is 1 or ID Is 6 and COLUMN_AUTHOR Is "john"
  )
```
### Deletion functions

```Kotlin
deleteRows(1, 3, 4, condition= COLUMN_DETAILS Is "okay")
deleteWhen(ID isEqual 9 and COLUMN_AUTHOR Is "shittu")
```
## ADVANCE

#### To go more crazy i had to play around with Kotlin Higher-order functions and inifx to
create a database in a tree like structure as below

```Kotlin
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
//            Columns.customColumn("age INTEGER PRIMARY KEY AUTOINCREMENT")
//            customColumn { "age".integer().primaryKey().autoIncrement()}
//            primaryColumn<Int> { ID }
                }
            }
//            table{...}
//            table{...}
    }
```

#### To customize your own table you might have to extend from Table Class, and with extention
funtion, may be you don't!

```Kotlin
cass NoteTable(database: DatabaseCreator?) : Table(
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

```
##### From your activity/Reposity you can do something like this
```Kotlin
   val myDb = createDatabase { name = "example.db"; version = 1 }
   customNoteTable = NoteTable(databaseCreator)
   //Load your Notes
    customNoteTable!!.loadAllNotes(object : LoadNoteListener {
        override fun onNoteLoaded(notes: List<Note>) {
            noteAdapter!!.submitList(notes)
            Log.e(TAG, "notes submited")
        }
        override fun onNoteLoadProgress(note: Note, id: Int?) {
        }
    })
    //Insert Note
    customNoteTable!!.insert(note)
```
#### To handle the your database versions implement the following methods
```Kotlin
myDb.setDatabaseListener(object : DatabaseCreator.DatabaseListener {
            override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
                TODO("Not yet implemented")
            }

            override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
                TODO("Not yet implemented")
            }

            override fun onCreate(db: SQLiteDatabase) {
                TODO("Not yet implemented")
            }

        })
```

### Need more help???
    I have created a sample project, check it out it should get you started

## How it works?
The following Kotlin features add sugar to the tea, they are the magic wands
of this big show!!!

#### 1. Generic functions
#### 2. lamda function
#### 3. Type function
#### 4. Extension function
#### 5. Higher-Order Functions and Lambdas
#### 6. Scoping
#### 7. Infix notations

## Who can use this?

#### 1. Anybody who wish to create a quick and simple database without diving in to the details
#### 2. If you are unfamiliar with the usage of the above kotlin concepts
#### 3. If you lack the moltivation to learn Kotlin
#### 4. If you want to learn Kotlin advance features for Android 

## MySqlite Story

 5 year ago i created some helper methods in Java to ease my usage of sqlite database, i wrote so many line of boiler plate codes, now with the advent of Kotlin and it great features, i could easily create a flexible code and i think creating this will create a lot of moltivations for developers to learn about this cool features.

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

