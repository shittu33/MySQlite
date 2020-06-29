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

## How it works?
The following Kotlin features are used
### 1. Generic functions
### 2. lamda function
### 3. Type function
### 4. Extension function
### 5. Higher-Order Functions and Lambdas
### 6. Scoping
### 7. Infix notations

## Who can use this?

#### 1. Anybody who wish to create a quick and simple database without diving in to the details
#### 2. If you are unfamiliar with the usage of the above kotlin concepts
#### 3. If you lack the moltivation to learn Kotlin

## Story


#### 5 year ago i created some helper methods in Java to ease my usage of sqlite database, i wrote so many line of boiler plate codes, now with the advent of Kotlin and it great features, i could easily create a flexible code and i think creating this will create a lot of moltivations for developers to learn about this cool features.

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

