# MYSQLITE

MySqlite is a simple Android library that help you write a readable code for Sqlite CRUD Operations (Create,Read,update,delete).it's designed for education purpose, it will be useful for anybody who are looking for moltivation to learn Kotlin.

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
#### To populate your list from database

```Kotlin
getRows {
     val id = getValueOf<Int>(NoteTable.ID)
     val details = getValueOf<String>(COLUMN_DETAILS)
     val author = getValueOf<String>(COLUMN_AUTHOR)
     val note = Note(id, author, details)
     notes.add(note)
}
```
