package com.example.mysqlite.engine

import android.content.Context
import android.util.Log
import com.example.mysqlite.utils.Constants.AND
import com.example.mysqlite.utils.Constants.ASC
import com.example.mysqlite.utils.Constants.DESC
import com.example.mysqlite.utils.Constants.GREATER
import com.example.mysqlite.utils.Constants.GREATER_EQUAL
import com.example.mysqlite.utils.Constants.LESSER
import com.example.mysqlite.utils.Constants.LESSER_EQUAL
import com.example.mysqlite.utils.Constants.OR
import com.example.mysqlite.engine.Table.Companion.TAG
import com.example.mysqlite.engine.Table.Companion.getAStatementWithOperator
import com.example.mysqlite.engine.Table.Companion.getEqualSignStatement
import com.example.mysqlite.engine.Table.Companion.getGroupByStatement
import com.example.mysqlite.engine.Table.Companion.getLikeStatement
import com.example.mysqlite.engine.Table.Companion.getOrderByStatement
import java.io.Serializable
import java.util.*

/**
 * Converts this pair into a list.
 */
fun <T> Pair<T, T>.toList(): List<T> = listOf(first, second)


/**
 *Infix functions
 *  */
infix fun <A, B> A.to(that: B): ValueColumnPair<A, B> = ValueColumnPair(this, that)
infix fun <A, B> A.from(that: B): ValueColumnPair<A, B> = ValueColumnPair(this, that)
infix fun <A, B> A.was(that: B): ColumnValuePair<A, B> = ColumnValuePair(this, that)
infix fun <A, B> A.row(that: B): ColumnValuePair<A, B> = ColumnValuePair(this, that)
infix fun <A, B> A.with(that: B): ColumnValuePair<A, B> = ColumnValuePair(this, that)

infix fun <B> B.stringColumn(that: String): Table {
    return TColumn<String, B>(that)
}

infix fun <B> B.intColumn(that: String): Table {
    return TColumn<Int, B>(that)
}

infix fun <B> B.doubleColumn(that: String): Table {
    return TColumn<Double, B>(that)
}

infix fun <B> B.floatColumn(that: String): Table {
    return TColumn<Float, B>(that)
}

infix fun <B> B.dateColumn(that: String): Table {
    return TColumn<Date, B>(that)
}

infix fun <B> B.primaryIntColumn(that: String): Table {
    return TPrimary<Int, B>(that)
}

infix fun <B> B.autoPrimaryColumn(that: String): Table {
    return TAutoPrimary<Int, B>(that)
}

infix fun <B> B.primaryStringColumn(that: String): Table {
    return TPrimary<String, B>(that)
}


private inline infix fun <reified A : Comparable<A>, B> B.TUniqueColumn(columnStatement: String): Table {
    return addBaseColumn(Columns.uniqueColumn<A>(columnStatement))
}

private inline infix fun <reified A : Comparable<A>, B> B.TAutoPrimary(that: String): Table {
    val columnStatement = Columns.primaryAutoIncrementColumn<A>(that)
    return addBaseColumn(columnStatement)
}

private inline infix fun <reified A : Comparable<A>, B> B.TPrimary(that: String): Table {
    return addBaseColumn(Columns.primaryColumn<A>(that))
}

private inline infix fun <reified A : Comparable<A>, B> B.TColumn(columnStatement: String): Table {
    return addBaseColumn(Columns.column<A>(columnStatement))
}

private infix fun <B> B.addBaseColumn(columnStatement: String): Table {
    if (this is String) {
        val table = Table()
        table.setName(this)
        table.rawColumn { columnStatement }
        return table
    } else if (this is Table) {
        this.rawColumn { columnStatement }
        return this
    }
    throw Exception(
        "Unsupported Type, the statement should start with either" +
                " a String or Table Class"
    )
}


/**
 *Infix functions to ease the generation of whereStatements
 *  */
infix fun <B> String.and(that: B): String {
    return "$this $AND $that "
}

infix fun <B> String.or(that: B): String {
    return "$this $OR $that "
}

infix fun <B> String.equal(that: B): String {
    return getEqualSignStatement(this, that)
}

infix fun <B> String.isEqual(that: B): String {
    return getEqualSignStatement(this, that)
}

infix fun <B> String.Is(that: B): String {
    return getEqualSignStatement(this, that)
}

infix fun <B> String.greaterOrEqual(that: B): String {
    return getAStatementWithOperator(this, GREATER_EQUAL, that)
}

infix fun <B> String.lesserOrEqual(that: B): String {
    return getAStatementWithOperator(this, LESSER_EQUAL, that)
}

infix fun <B> String.greatThan(that: B): String {
    return getAStatementWithOperator(this, GREATER, that)
}

infix fun <B> String.exceeds(that: B): String {
    return getAStatementWithOperator(this, GREATER, that)
}

infix fun <B> String.lessThan(that: B): String {
    return getAStatementWithOperator(this, LESSER, that)
}

infix fun <B> String.below(that: B): String {
    return getAStatementWithOperator(this, LESSER, that)
}

infix fun String.groupBy(that: String): String {
    return "$this ${getGroupByStatement(that)} "
}

infix fun String.limit(that: Int): String {
    return "$this Limit $that "
}

infix fun String.orderBy(that: String): String {
    return "$this ${getOrderByStatement(that)} "
}

infix fun String.orderByASC(that: String): String {
    return "$this ${getOrderByStatement(that)} $ASC"
}

infix fun String.orderByDESC(that: String): String {
    return "$this ${getOrderByStatement(that)} $DESC"
}

infix fun <B> String.like(that: B): String {
    return getLikeStatement(this, that)
}

//infix fun String.ASC(that: String): String {
//    return "$this ${Constants.ASC} "
//}
//
//infix fun String.DESC(that: String): String {
//    return "$this ${Constants.DESC} "
//}

/**
 *String extention functions to ease the generation of columnStatements
 *  */

fun String.notNull(): String {
    return " $this NOT NULL "
}

fun String.unique(): String {
    return " $this UNIQUE "
}

fun String.primaryKey(): String {
    return " $this PRIMARY KEY ";
}

fun String.integer(): String {
    return " $this INTEGER ";
}

fun String.double(): String {
    return " $this DOUBLE ";
}

fun String.varchar(): String {
    return " $this VARCHAR ";
}

fun String.text(): String {
    return " $this TEXT ";
}

fun String.autoIncrement(): String {
    return " $this AUTOINCREMENT "
}


/**
 *Infix functions to enable us to createTable & Database in a very readable information
 * e.g
 *  val myDb = createDatabase { name = "example.db"; version = 1 }
 *  myDb createTable { "Note" autoPrimaryColumn ID stringColumn COLUMN_DETAILS stringColumn COLUMN_AUTHOR }

 *  */

data class DatabaseInfo(var name: String? = null, var version: Int = 0)

infix fun Context.createDatabase(creator: DatabaseInfo.() -> Unit): DatabaseCreator {
    val context: Context = this
    val databaseInfo = DatabaseInfo()
    databaseInfo.creator()
    return DatabaseCreator(context, databaseInfo.name, databaseInfo.version)
}

infix fun DatabaseCreator.createTable(collectTableInfo: () -> Table): Table {
    val dbCreator: DatabaseCreator = this
    return collectTableInfo().apply {
        setDatabase(dbCreator)
        loadColumns()?.let { columns: Array<String?> ->
            Log.e(TAG, " the column size is ${columns.size.toString()}")
            createTableWithColumnlist(*columns)
        }
    }
}

/**
 * I had to create this to enable us to create our in a tree like
 * structure using the same Table class
 * */
infix fun Context.database(creator: Database.() -> Unit): DatabaseCreator {
    val databaseInfo = Database()
    databaseInfo.dbContext = this
    databaseInfo.creator()
    if (databaseInfo.dbCreator != null)
        return databaseInfo.dbCreator!!
    else
        throw Exception("You have to add a table to your database")
}

class Database() {
    var dbContext: Context? = null
    var db_name: String? = null
    var version: Int = 0
    var dbCreator: DatabaseCreator? = null
    infix fun table(collectTableInfo: Table.() -> Unit): Table {
        Table().apply {
            collectTableInfo()
            dbCreator = DatabaseCreator(dbContext, db_name, version)
            dbCreator?.let {
                setDatabase(it)
            }
            createTableWithColumnlist(*loadColumns()!!)
            return this
        }
    }
}


//Pair extention classes to map columns to values
class ColumnValuePair<out A, out B>(
    val column: A,
    val value: B
) {
    /**
     * Returns string representation of the [Pair] including its [column] and [value] values.
     */
    override fun toString(): String = "($column, $value)"
}

data class ValueColumnPair<out A, out B>(
    val value: A,
    val column: B
) : Serializable {

    /**
     * Returns string representation of the [Pair] including its [column] and [value] values.
     */
    override fun toString(): String = "($column, $value)"
}
