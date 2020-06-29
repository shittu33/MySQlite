(https://jitpack.io/v/shittu33/MySQlite.svg)](https://jitpack.io/#shittu33/MySQlite)

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
#### To populate your list from the database, use the table object to call getRows Method

```Kotlin
noteTable.getRows {
     val id = getValueOf<Int>(ID)
     val details = getValueOf<String>(COLUMN_DETAILS)
     val author = getValueOf<String>(COLUMN_AUTHOR)
     val note = Note(id, author, details)
     notes.add(note)
}
```
And boom! you successfully create and read from your database.
