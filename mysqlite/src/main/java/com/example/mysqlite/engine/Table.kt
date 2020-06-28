package com.example.mysqlite.engine

import android.content.ContentValues
import android.database.Cursor
import android.database.Cursor.*
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.mysqlite.utils.Constants
import com.example.mysqlite.Models.Where
import com.example.mysqlite.engine.Columns.primaryAutoIncrementColumn
import java.io.Serializable
import java.util.*

/**
 * Created by Abu Muhsin on 25/08/2018.
 */
open class Table : Serializable {
    var tableName: String? = null
    public var dbWrite: SQLiteDatabase? = null
    private var db: SQLiteDatabase? = null
    var columns: Array<String?>? = emptyArray()
    private var columnList: ArrayList<String> = ArrayList<String>()

    constructor() {
    }

    /**To use this method you must call  thesetName(String) addColums(vararg columns: String)
     * function to add columns
     * @param db_Creator an object of Database creator, a class that handle database
     * creation stuffs
     * @see  "{@link com.example.mysqlite.engine.DatabaseCreator.DatabaseCreator
     */
    constructor(db_Creator: DatabaseCreator) {
        dbWrite = db_Creator.writableDatabase
        db = db_Creator.readableDatabase
    }

    /**To use this method you must call the addColums(vararg columns: String)
     * function to add columns
     * @param db_Creator an object of Database creator, a class that handle database
     * creation stuffs
     * @see  "{@link com.example.mysqlite.engine.DatabaseCreator.DatabaseCreator
     * @param table_name your table name
     */
    constructor(db_Creator: DatabaseCreator, table_name: String?) {
        dbWrite = db_Creator.writableDatabase
        db = db_Creator.readableDatabase
        this.tableName = table_name
    }

    /**
     * @param db_Creator an object of Database creator, a class that handle database
     * creation stuffs
     * @see  "{@link com.example.mysqlite.engine.DatabaseCreator.DatabaseCreator
     * @param table_name your table name
     * @param columns used an array  to collect your column name
     * [ #getIntegerColumn_with_PrimaryKey_AutoIncrementStatement(String)][.getInteger_ColumnStatement]
     * and other method like that to insert your columns...
     */
    constructor(db_Creator: DatabaseCreator, table_name: String?, vararg columns: String?) {
        setDatabase(db_Creator)
        setName(table_name)
        createTableWithColumnlist(*columns)
        this.columns = arrayOf(*columns)
    }

    fun setDatabase(db_Creator: DatabaseCreator) {
        dbWrite = db_Creator.writableDatabase
        db = db_Creator.readableDatabase
    }

    fun setName(table_name: String?) {
        this.tableName = table_name
    }


    fun isEmptyColumn(): Boolean {
        return columnList.isEmpty()
    }

    fun loadColumns(): Array<String?>? {
        columns = arrayOfNulls(columnList.size)
        for ((i, value) in columnList.withIndex()) {
            columns?.set(i, value)
        }
        return columns
    }


    fun rawColumn(column: () -> String?) {
        column()?.let { columnList.add(it) }
    }

    inline fun <reified T : Comparable<T>> addNewColumn(rawColumnStatement: String) {
        val alterStatement = getAlterAddColumnStatement<T>(rawColumnStatement)
        rawColumn { rawColumnStatement }
        loadColumns()
        try {
            dbWrite!!.execSQL(alterStatement)
        } catch (e: SQLException) {
            e.printStackTrace()
//            Log.e(TAG,e.printStackTrace())
        }
    }

    inline fun <reified T : Comparable<T>> Table.getAlterAddColumnStatement(rawStatement: String): String {
        return "ALTER TABLE ${this.tableName} ADD $rawStatement"
    }

    /**
     * @param columns used an array to collect your column name
     * [ #getIntegerColumn_with_PrimaryKey_AutoIncrementStatement(String)][.getInteger_ColumnStatement]
     * and other method like that to insert your columns...
     */
    fun createTableWithColumnlist(vararg columns: String?) {
        val columnsStatements = StringBuilder()
        for (column in columns) {
            if (column == columns[columns.size - 1]) {
                columnsStatements.append(column)
            } else {
                columnsStatements.append(column).append(",")
            }
        }
        createWithColumnStrings(columnsStatements.toString())
    }

    /**
     * @param columnsStatements Accept column statement only to crreate the table
     */
    private fun createWithColumnStrings(columnsStatements: String) {
        createTable(
            "CREATE TABLE IF NOT EXISTS " +
                    "${tableName!!.toUpperCase(Locale.getDefault())}($columnsStatements)"
        )
    }

    /**
     * @param table_Statement accept sql_table statement to create table
     */
    private fun createTable(table_Statement: String) {
        dbWrite!!.execSQL(table_Statement)
        Log.e(TAG, table_Statement)
    }

    /**
     * @param values contain columns as a key and column values as value
     */
    fun insert(values: ContentValues?) {
        dbWrite!!.insert(tableName, null, values)
//        dbWrite!!.close();
    }

    /**Convinient function to insert value to columns
     *  as in insert(5 to "id","dd" to "details","me" to "author" )
     * @param valueColumnPair is a pair of values to column of type [Pair(value,column)]
     * Note: this function does not allow saving 2 differnt value to the same column
     * [e.g 5 to "id",6 to "id"]
     * */

    fun insert(vararg valueColumnPair: ValueColumnPair<Any, String>) {
        val contentValues = ContentValues()
        for (pair in valueColumnPair) {
            contentValues.checkAndSaveValues(pair.value, pair.column)
            Log.e(TAG, pair.toString())
        }
        insert(contentValues)
    }

    /** Check whether the value is a supported Type*/
    fun ContentValues.checkAndSaveValues(value: Any, column: String) {
        when (value) {
            is String -> put(column, value)
            is Int -> put(column, value)
            is Double -> put(column, value)
            is Float -> put(column, value)
            else -> throw  Exception("Unhandled Data type")
        }
    }

    /**
     * @param contentValues a map from column names to new column values. null is a
     * valid value that will be translated to NULL.
     * @param condition  the optional WHERE clause to apply when updating.
     * will be replaced by the values from whereArgs. The values
     * will be bound as Strings.
     */
    private fun update(
        contentValues: ContentValues,
        condition: String? = null
    ) {
        db!!.update(tableName, contentValues, condition, null)
    }

    /**
     * Convinient function to insert value to columns
     *  as in insert(5 to "id","dd" to "details","me" to "author" )
     * @param condition the WHERE clause to apply when updating,
     *        take note that you have to explicitly specify the condition
     *        like this update(5 to "id",6 to "id", condition= "author_column" Is "john")
     * @param columnValuePair is a pair of values to column of type [Pair(value,column)]
     * Note: this function does not allow updating 2 differnt value to the same column
     * [e.g 5 to "id",6 to "id"]
     * >
     */
    fun update(vararg columnValuePair: ColumnValuePair<String, Any>, condition: String) {
        val contentValues = ContentValues()
        for ((i, pair) in columnValuePair.withIndex()) {
            contentValues.checkAndSaveValues(pair.value, pair.column)
        }
        update(contentValues, condition)
//        db!!.update(tableName, contentValues, whereStatement, null)
    }

    /**
     * Convenient function to insert value to columns
     *  as in insert(5 to "id","dd" to "details","me" to "author" )
     * @param columnValuePair is a pair of values to column of type [Pair(value,column)]
     * Note: this function does not allow updating 2 differnt value to the same column
     * [e.g 5 to "id",6 to "id"]
     * >
     */
    fun update(vararg columnValuePair: ColumnValuePair<String, Any>) {
        val contentValues = ContentValues()
        for ((i, pair) in columnValuePair.withIndex()) {
            contentValues.checkAndSaveValues(pair.value, pair.column)
        }
        db!!.update(tableName, contentValues, null, null)
    }

    /**
     * Convinient function to update value of columns
     *  as in update(5,"column_name" to  new_name ,"column_age" to 23... )
     *  @param keyValue the unique primary key that identify each row
     * @param ColumnValuePair is a pair of values to column of type [Pair(column,value)]
     * >
     */
    fun updateRow(keyValue: Any, vararg columnValuePairs: ColumnValuePair<String, Any>) {
        val whereStatement: String = getEqualSignStatement(primaryColumn, keyValue)
        update(*columnValuePairs, condition = whereStatement)
    }

    /**
     * Convinient function to update value of columns
     *  as in updateRow(5,"column_name" to  new_name ,"column_age" to 23... )
     *  @param keyValue the unique primary key that identify each row
     *  @param condition the condition to filter the sendResult
     *        take note that you have to explicitly specify the condition
     *        like this getSpecificRows(2, 3,4, condition= "author_column" Is "john")
     * @param ColumnValuePair is a pair of values to column of type [Pair(column,value)]
     * >
     */
    fun updateRow(
        keyValue: Any
        , vararg columnValuePairs: ColumnValuePair<String, Any>
        , condition: String? = null
    ) {
        val whereStatement: String = "(${getEqualSignStatement(primaryColumn, keyValue)})" +
                if (condition != null) " AND $condition" else ""

        update(*columnValuePairs, condition = whereStatement)
    }

    /**
     *  This is A higher-order function
     * Convinient method for getting values of a table row
     *
     * @param primaryKey      this represent the primaryKey to identify a specific row.
     * @param sendResult      is a function type that serve as listener for receiving the result
     */
    fun <T> getRow(
        primaryKey: T,
        sendResult: ContentValues.(columns: Array<String>) -> Unit = {}
    ) {
        val cursor = fetchAllWhere(getEqualSignStatement(primaryColumn, primaryKey))
        cursor.loadRows(sendResult, true)
    }

    /**Convenient method for getting value of a row of a table
     *filtered by the column e.g getRow<String>(3 from "id") {

     * @param ValueColumnPair  this represent the pair of the row_key to  column.
     * @return this method return a single value of type[T]
     */
    inline fun <reified T> getRow(
        rowPair: ValueColumnPair<Any, String>,
        noinline sendResult: ContentValues.(columns: Array<String>) -> Unit = {}
    ): T {
        val columnToSearch = rowPair.column
        val where = getEqualSignStatement(primaryColumn, rowPair.value)
        val cursor = fetchColumnWhere(columnToSearch, where)
        cursor?.loadRows(sendResult, true)
        return cursor!!.getCursorValue(columnToSearch)
    }

    /**
     *  This is A higher-order function
     * Loop through the whole table row by row
     * @param sendResult  is a function type that serve as listener for receiving
     * the result of each row of the table
     */
    fun getRows(
        sendResult: ContentValues.(columns: Array<String>) -> Unit
    ) {
        val cursor = fetchAll()
        cursor.loadRows(sendResult)
    }

    /**
     *  This is A higher-order function
     * Loop through the whole table row by row with a given conditions
     * @param condition The conditions to consider before loading database
     * use can use our infix to generate the sqlite statements
     * @see {@link com.example.mysqlite.engine.Notations}
     * {e.g "id" equal 3 and "author" greaterThan 2}
     * @param sendResult  is a function type that serve as listener for receiving
     * the result of each row of the table
     */
    fun getRows(
        condition: String,
        sendResult: ContentValues.(columns: Array<String>) -> Unit
    ) {
        val cursor = fetchAllWhere(condition)
        Log.e(TAG, condition)
        cursor.loadRows(sendResult)
    }

    /**Convenient method for getting values of specified rows of a table
     *
     * @param primaryKey      this represent the primaryKey value to identify a specific row.
     * @param condition an optional condition to be considered for loading rows
     *        take note that you have to explicitly specify the condition
     *        like this getSpecificRows(2, 3,4, condition= "author_column" Is "john")
     * @param sendResult      this serve as listener that update you with each row,
     * call the extension function of ContentValues{@link getValueOfColumn(")}
     * to get value of each column in a row.
     */
    fun <T : Comparable<T>> getSpecificRows(
        vararg primaryKeys: T,
        condition: String? = null,
        orderBy: String? = null,
        sendResult: ContentValues.(columns: Array<String>) -> Unit
    ) {
        val whereStatement =
            "(${getOREqualStatementsFromArgs(primaryColumn, *primaryKeys)})" +
                    "${if (condition != null) " AND $condition" else ""} "
        val cursor = fetchAllWhere(whereStatement)
        cursor.loadRows(sendResult)
    }

//    /**
//     *  This is A higher-order function
//     * This is a convenient method for looping through a column
//     *
//     * @param columns the columns to loop through...
//     * @param sendResult   is a function type that serve as listener for receiving the result
//     * @return ArrayList<T> an arryList of Generic type passed to the method
//     * which denote the type of data in the column
//     *
//     *     */
//    @Suppress("UNCHECKED_CAST")
//    fun getRowsOfColumn(
//        vararg columns: String,
//        sendResult: ContentValues.(columns: Array<String>) -> Unit = {}
//    ) {
//        val cursor =
//            fetchColumns(*columns)
//        cursor.loadRows(sendResult)
//        cursor.close()
//    }
//

    /**
     *  This is A higher-order function
     * This is a convenient method for looping through a column
     *
     * @param columns the columns to loop through...
     * @param condition condition to filter the result
     *        take note that you have to explicitly specify the condition
     *        like this getRowsOfColumn("column_id", condition= "author_column" Is "john")
     * @param sendResult   is a function type that serve as listener for receiving the result
     * @return ArrayList<T> an arryList of Generic type passed to the method
     * which denote the type of data in the column
     *
     *     */
    @Suppress("UNCHECKED_CAST")
    fun getRowsOfColumn(
        vararg columns: String,
        condition: String,
        sendResult: ContentValues.(columns: Array<String>) -> Unit
    ) {
        val cursor = fetchColumnsWhere(condition, *columns)
        cursor.loadRows(sendResult)
        cursor.close()
    }


    fun Cursor.loadRows(
        sendResult: ContentValues.(columns: Array<String>) -> Unit,
        isSingle: Boolean = false
    ) {
        val load = {
            val columns = columnNames
            val values = ContentValues()
            for (currentIndex in columns.indices) {
                val column = columns[currentIndex]
                Log.e(TAG, "getAllTableRows: $column")
//                type = getColumnTypeByPosition(currentIndex)
//                Log.e(TAG, "loopRows: type is $type")
                when (getType(currentIndex)) {
                    FIELD_TYPE_STRING -> {
                        Log.e(TAG, "loopRows " + getString(currentIndex))
                        values.mapColumnWithValue<String>(column, this)
                    }
                    FIELD_TYPE_INTEGER -> {
                        Log.e(TAG, "loopRows " + getInt(currentIndex))
                        values.mapColumnWithValue<Int>(column, this)
                    }
                    FIELD_TYPE_FLOAT ->
                        values.mapColumnWithValue<Float>(column, this)
                }
            }
            values.sendResult(columnNames)
        }
        if (isSingle) {
            if (moveToFirst()) {
                load()
            }
        } else
            while (moveToNext()) {
                load()
            }
    }

    /** An extention function for mapping cursor value to a column
     * for an easy access
     * @param column the column to get value from
     * @param cursor the cursor we use to load values
     * */
    private inline fun <reified T> ContentValues.mapColumnWithValue(
        column: String,
        cursor: Cursor
    ) {
        when (T::class) {
            String::class ->
                put(column, cursor.getCursorValue<String>(column))
            Int::class ->
                put(column, cursor.getCursorValue<Int>(column))
            Float::class ->
                put(column, cursor.getCursorValue<Float>(column))
            Double::class ->
                put(column, cursor.getCursorValue<Double>(column))
            Date::class ->
                put(column, cursor.getCursorValue<Long>(column))
            else -> throw Exception("Unhandled return type")
        }
    }

    /** An extention function of cursor for getting value of a column
     * for an easy access
     * @param column the column to get value from
     * */
    inline fun <reified T> Cursor.getCursorValue(column: String): T {
        val columnIndex = getColumnIndex(column)
        return when (T::class) {
            String::class ->
                getString(columnIndex) as T
            Int::class ->
                getInt(columnIndex) as T
            Float::class ->
                getFloat(columnIndex) as T
            Double::class ->
                getDouble(columnIndex) as T
            Date::class ->
                getLong(columnIndex) as T
            else -> throw Exception("Unhandled return type")
        }
    }

    /** i am using Generic extension function to test for supported Types*/
    fun <T> T.isSupported(): Boolean {
        return when (this) {
            String::class
                , Int::class
                , Double::class
                , Float::class
                , Date::class -> true
            else -> false
        }
    }

    /**
     *
     * @param condition The conditions to consider before deleting any record
     * use can use our infix to generate the sqlite statements
     * @see {@link com.example.mysqlite.engine.Notations}
     * {e.g "id" equal 3 and "author" greaterThan 2}
     * */
    fun deleteWhen(condition: String?): Int {
        return dbWrite!!.delete(tableName, condition, null)
    }

    /**
     * Convenient method to Delete a row.
     *
     * @param keyValue the unique value(Object) that can identify each row, usually a primary key.
     * @param condition the condition for deleting the row
     * type [T] is the type of your primaryKey
     * @return will return the 1 row affected.
     */
    fun <T> deleteRow(keyValue: T, condition: String? = null): Int {
        return deleteWhen(
//            getEqualSignStatement(primaryColumn, keyValue)
            "(${getEqualSignStatement(primaryColumn, keyValue)})" +
                    if (condition != null) " AND $condition" else ""

        )
    }

    /**
     * Convenient method to Delete many rows.
     *
     * @param keyValues the unique value you use as primary key that can identify
     * each row.
     *@param condition  the condition for deleting the rows
     *        take note that you have to explicitly specify the condition
     *        like this deleteRows(2,3,4,5 condition= "author_column" Is "john")
     * type [T] is the type of your primaryKey
     * @return will return the n number of  affected rows
     */
    fun <T : Comparable<T>> deleteRows(vararg keyValues: T, condition: String? = null): Int {
        return deleteWhen(
            "(${getOREqualStatementsFromArgs(primaryColumn, *keyValues)})" +
                    "${if (condition != null) " AND $condition" else ""} "

        )
    }

    val primaryColumn: String
        get() {
            var primary_column = ""
            for (column_clause in columns!!) {
                if (get_a_word(column_clause, " ", 2) == "PRIMARY" && get_a_word(
                        column_clause,
                        " ",
                        3
                    ) == "KEY"
                ) {
                    primary_column = get_a_word(column_clause, " ", 0)
                    break
                }
            }
            return primary_column
        }

    fun getColumnNameByPosition(columnPosition: Int): String {
        var columnName = ""
        if (columns != null) {
            val content = columns!![columnPosition]
            columnName = get_a_word(content, " ", 0)
        }
        return columnName
    }

    fun getColumnTypeByPosition(column_position: Int): String {
        var column_Type = ""
        if (columns != null) {
            val content = columns!![column_position]
            val sql_type = get_a_word(content, " ", 1)
            when (sql_type) {
                "DECIMAL", "FLOAT" -> column_Type = Constants.FLOAT
                "INTEGER" -> column_Type = Constants.INTEGER
                "DOUBLE" -> column_Type = Constants.DOUBLE
                "DATETIME" -> column_Type = Constants.DATE
                "TEXT", "VARCHAR" -> column_Type = Constants.STRING
            }
        }
        return column_Type
    }

    fun getColumnTypeByName(ColumnName: String): String {
        var column_Type = ""
        for (i in columns!!.indices) {
            if (ColumnName == getColumnNameByPosition(i)) {
                column_Type = getColumnTypeByPosition(i)
                break
            }
        }
        return column_Type
    }

    private fun get_a_word(text: String?, regExp: String, pos: Int): String {
        val array_of_text = text!!.split(regExp).toTypedArray()
        return array_of_text[pos]
    }


//    private fun fetchDataWithStatment(fSatatement: String): Cursor {
//        return db!!.rawQuery(fSatatement, null)
//    }

    private fun fetchAll(): Cursor {
        return rawFetch()
//        val statement = "SELECT * FROM $tableName"
//        return fetchDataWithStatment(statement)
    }

    private fun fetchAllWhere(whereStatement: String): Cursor {
        return rawFetch(where = whereStatement)
//        return fetchDataWithStatment("SELECT * FROM $tableName WHERE $where")
    }

    fun fetchAllWhereOrderedby(whereStatement: String, orderByStatement: String)
            : Cursor {
        return rawFetch(where = whereStatement, orderByString = orderByStatement)
    }


    fun fetchColumn(column: String): Cursor {
        return rawFetch(columnArgs = *arrayOf(column))
//        return fetchDataWithStatment("SELECT $column From $tableName")
    }

    fun fetchColumnWhere(column: String, whereStatement: String): Cursor? {
        return rawFetch(columnArgs = *arrayOf(column), where = whereStatement)
//        return try {
//            fetchDataWithStatment("SELECT $column From $tableName WHERE $whereStatement")
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
    }

    private fun fetchColumns(vararg columns: String?): Cursor {
        return rawFetch(columnArgs = *columns)
    }

    private fun fetchColumnsWhere(whereStatement: String, vararg columns: String?): Cursor {
        return rawFetch(columnArgs = *columns, where = whereStatement)
    }

    private fun rawFetch(
        distinct: Boolean = false,
        vararg columnArgs: String?,
        where: String? = null,
        groupByString: String? = null,
        havingString: String? = null,
        orderByString: String? = null,
        limit: String? = null
    ): Cursor {
        return db!!.query(
            distinct,
            tableName,
            columnArgs,
            where,
            null,
            groupByString,
            havingString,
            orderByString,
            limit
        )
    }


    //...This are higher order functions to make creating Table more readable

    fun columns(predicate: Table.() -> Unit) {
        this.predicate()
    }

    inline fun <reified T : Comparable<T>> column(noinline column: () -> String?) {
        rawColumn { Columns.column<T>(column()!!) }
    }

    fun customColumn(column: () -> String?) {
        rawColumn { Columns.customColumn(column()!!) }
    }

    inline fun <reified T : Comparable<T>> uniqueColumn(noinline column: () -> String?) {
        rawColumn { Columns.uniqueColumn<T>(column()!!) }
    }

    inline fun <reified T : Comparable<T>> primaryColumn(noinline column: () -> String?) {
        rawColumn { Columns.primaryColumn<T>(column()!!) }
    }

    inline fun <reified T : Comparable<T>> autoPrimaryColumn(noinline column: () -> String?) {
        val columnStatement = primaryAutoIncrementColumn<T>(column()!!)
        rawColumn { columnStatement }
        Log.e(TAG, columnStatement)
    }

//    inline fun <reified T> getTUnitColumn(noinline column: () -> Unit): String {
//    }


//    inline fun <reified T : Comparable<T>> Tcolumn(noinline column: Column.() -> Unit): Column {
//        Column().apply {
//            column()
//            return this
//        }
//    }

//...............................Statemennts........................


    fun <T> getAStatementWithOperator(
        column: String,
        operator: String,
        value: T
    ): String {
        return if (value is String) "$column $operator '$value'" else "$column $operator $value"
    }

    fun <T> getGreaterStatement(column: String, value: T): String {
        return getAStatementWithOperator(
            column,
            Constants.GREATER,
            value
        )
    }

    fun <T> getLessStatement(column: String, value: T): String {
        return getAStatementWithOperator(
            column,
            Constants.LESSER,
            value
        )
    }

    fun <T> getGreaterEqualStatement(column: String, value: T): String {
        return getAStatementWithOperator(
            column,
            Constants.GREATER_EQUAL,
            value
        )
    }

    fun <T> getLessEqualStatement(column: String, value: T): String {
        return getAStatementWithOperator(
            column,
            Constants.LESSER_EQUAL,
            value
        )
    }

    private fun getEqualStatements(
        operator: String,
        vararg columnValuePair: ColumnValuePair<String, Any>
    ): String {
        val lastPosition = columnValuePair.size - 1
        val whereStatement = StringBuilder()
        for ((i, pair) in columnValuePair.withIndex()) {
            Log.e(TAG, pair.toString())
            val column = pair.column;
            val value = pair.value
            whereStatement.append(
                getEqualSignStatement(
                    column,
                    value
                )
            )
            if (i != lastPosition) {
                whereStatement.append(operator)
            }
        }
        return whereStatement.toString()
    }

    private fun getEqualStatementsVColumn(
        operator: String,
        vararg valueColumnPair: ValueColumnPair<Any, String>
    ): String {
        val lastPosition = valueColumnPair.size - 1
        val whereStatement = StringBuilder()
        for ((i, pair) in valueColumnPair.withIndex()) {
            Log.e(TAG, pair.toString())
            val column = pair.column;
            val value = pair.value
            whereStatement.append(
                getEqualSignStatement(
                    column,
                    value
                )
            )
            if (i != lastPosition) {
                whereStatement.append(operator)
            }
        }
        return whereStatement.toString()
    }

    fun getAndEqualstatements(whereValues: ContentValues): String {
        val where = StringBuilder()
        val list =
            ArrayList(
                whereValues.valueSet()
            )
        for (entry in list) {
            val Where_column = entry.key
            val where_value = entry.value
            val last_position = list.size - 1
            where.append(
                getEqualSignStatement(
                    Where_column,
                    where_value
                )
            )
            if (entry != list[last_position]) {
                where.append(Constants.AND)
            }
        }
        return where.toString()
    }

    private fun getAndEqualstatementsVColumn(
        vararg columnValuePair: ValueColumnPair<Any, String>
    ): String {
        return getEqualStatementsVColumn(Constants.AND, *columnValuePair)
    }

    fun getAndEqualstatements(
        vararg columnValuePair: ColumnValuePair<String, Any>
    ): String {
        return getEqualStatements(Constants.AND, *columnValuePair)
    }

    private fun getOREqualstatementsVColumn(
        vararg ValueColumnPairs: ValueColumnPair<Any, String>
    ): String {
        return getEqualStatementsVColumn(Constants.OR, *ValueColumnPairs)
    }

    fun getOREqualstatements(
        vararg columnValuePair: ColumnValuePair<String, Any>
    ): String {
        return getEqualStatements(Constants.OR, *columnValuePair)
    }

    fun <T> getOREqualStatementsFromArgs(column: String, vararg values: T): String {
        return getEqualStatementFromArgs<T>(column, Constants.OR, *values)
    }

    fun <T> getANDEqualStatementsFromArgs(column: String, vararg values: T): String {
        return getEqualStatementFromArgs<T>(column, Constants.AND, *values)
    }

    private fun <T> getEqualStatementFromArgs(
        column: String,
        operator: String,
        vararg values: T
    ): String {
        val where = StringBuilder()
        for ((index, value) in values.withIndex()) {
            where.append(
                getEqualSignStatement(
                    column,
                    value
                )
            )
            if (index != values.size - 1) {
                where.append(operator)
            }
        }
        return where.toString()
    }

//    private fun getStatementFromArgs(
//        operator: String,
//        vararg conditions: TablePair<String, Any>
//    ): String {
//        val statement = StringBuilder()
//        for ((index, pair) in conditions.withIndex()) {
//            when(pair){
//                is GreaterPair<String,Any>->{
//                    statement.append(
//                        getGreaterStatement(
//                            pair.column,
//                            pair.value
//                        )
//                    )
//                }
//                is EqualPair<String,Any>->{
//                    statement.append(
//                        getEqualSignStatement(
//                            pair.column,
//                            pair.value
//                        )
//                    )
//                }
//                is LessPair<String,Any>->{
//                    statement.append(
//                        getLessStatement(
//                            pair.column,
//                            pair.value
//                        )
//                    )
//                }
//            }
//            if (index != conditions.size - 1) {
//                statement.append(operator)
//            }
//        }
//        return statement.toString()
//    }

    //.....................Like Statements..................................

    private fun getLikeWhereStatementWithOperator(
        operator: String?,
        vararg where_array: Where
    ): String {
        val where = StringBuilder()
        for (whereColumnValue in where_array) {
            val Where_column = whereColumnValue.column
            val where_value = whereColumnValue.value
            val pos = where_array.size - 1
//            switch (operator) {
//                case AND:
            if (whereColumnValue == where_array[pos]) {
                where.append(getLikeStatement(Where_column, where_value))
            } else {
                where.append(getLikeStatement(Where_column, where_value))
                where.append(operator)
            }
        }
        return where.toString()
    }

    fun getLikeWhereStatementWithAnd(vararg where_values: Where): String {
        return getLikeWhereStatementWithOperator(
            Constants.AND,
            *where_values
        )
    }

    fun getLikeWhereStatementWithOR(vararg where_values: Where): String {
        return getLikeWhereStatementWithOperator(
            Constants.OR,
            *where_values
        )
    }

    private fun getOrderByStatement(
        Column: String,
        OrderType: String
    ): String {
        return getOrderByStatement(Column) + " " + OrderType
    }

    fun getAscendingOrderByStatement(Column: String): String {
        return getOrderByStatement(Column) + " " + Constants.ASC
    }

    fun getDecendingOrderByStatement(Column: String): String {
        return getOrderByStatement(Column) + " " + Constants.DESC
    }

    fun getAscendingOrderByStatementforcolumns(vararg Columns: String): String {
        return getOrderByColumnsStatement(Constants.ASC, *Columns)
    }

    fun getDecendingOrderByStatementforcolumns(vararg columns: String): String {
        return getOrderByColumnsStatement(Constants.DESC, *columns)
    }

    private fun getOrderByColumnsStatement(
        Order_type: String,
        vararg Columns: String
    ): String {
        val orderBuilder = StringBuilder()
        orderBuilder.append(" ORDER BY ")
        for (element in Columns) {
            orderBuilder.append(element)
            if (element != Columns[Columns.size - 1]) {
                orderBuilder.append(", ")
            }
        }
        orderBuilder.append(" ")
        orderBuilder.append(Order_type)
        return orderBuilder.toString()
    }

    companion object {
        const val TAG = "debug"

        fun getOrderByStatement(Column: String): String {
            return " ORDER BY $Column "
        }

        fun getGroupByStatement(Column: String): String {
            return " GROUP BY $Column "
        }

        fun <T> getLikeStatement(Column: String, like_value: T): String {
            return " $Column LIKE '%$like_value%' "
        }

        fun <T> getAStatementWithOperator(
            column: String,
            operator: String,
            value: T
        ): String {
            return if (value is String) " $column $operator '$value'" else "$column $operator $value "
        }

        fun <T> getEqualSignStatement(Column: String, value: T): String {
            return if (value is String) " $Column IS '$value'" else "$Column=$value "
        }
    }

    private fun getIfExistStringRecordInColumnCursor(
        column: String, search_item: String, where: String
    ): Cursor {
        return db!!.rawQuery(
            "SELECT EXISTS (SELECT * FROM $tableName WHERE $where AND $column ='$search_item' LIMIT 1)",
            null
        )
    }

    private fun getIsIntegerInColumnCursor(
        column: String, search_item: Int, where: String
    ): Cursor {
//        return db.rawQuery("SELECT * FROM " + table_name + " WHERE    yourKey=? AND yourKey1=?", new String[]{keyValue,keyvalue1});
        return db!!.rawQuery(
            "SELECT EXISTS (SELECT * FROM $tableName WHERE $where AND $column =$search_item LIMIT 1)",
            null
        )
    }

    private fun getIfExistStringRecordInColumnCursor(
        column: String,
        searchItem: String
    ): Cursor {
        return db!!.rawQuery(
            "SELECT EXISTS (SELECT * FROM $tableName WHERE $column ='$searchItem' LIMIT 1)",
            null
        )
    }

    private fun getIsIntegerInColumnCursor(column: String, search_item: Int): Cursor {
//        return db.rawQuery("SELECT * FROM " + table_name + " WHERE    yourKey=? AND yourKey1=?", new String[]{keyValue,keyvalue1});
        return db!!.rawQuery(
            "SELECT EXISTS (SELECT * FROM $tableName WHERE $column=$search_item LIMIT 1)",
            null
        )
    }

    /**
     * Covinient method to check if a column contain a value
     *
     * @param column                 The column to search through
     * @param value                  the search item either String or integer
     * @param whereStatement other where statement to filter the result
     * @return return true if the item was found, false otherwise.
     */
    fun <T> isValueExist(column: String, value: T, whereStatement: String): Boolean {
        if (value is String) {
            val cursor = getIfExistStringRecordInColumnCursor(
                column,
                value,
                whereStatement
            )
            cursor.moveToFirst()
            return cursor.getInt(0) == 1
        } else if (value is Int) {
            val cursor =
                getIsIntegerInColumnCursor(column, value, whereStatement)
            cursor.moveToFirst()
            return cursor.getInt(0) == 1
        }
        return false
    }

    /**
     * Covinient method to check if a column contain a value
     *
     * @param column The column to search through
     * @param value  the search item either String or integer
     * @return return true if the item was found, false otherwise.
     */
    fun <T> isValueExist(column: String, value: T): Boolean {
        if (value is String) {
            val cursor =
                getIfExistStringRecordInColumnCursor(column, value)
            cursor.moveToFirst()
            return cursor.getInt(0) == 1
        } else if (value is Int) {
            val cursor =
                getIsIntegerInColumnCursor(column, value)
            cursor.moveToFirst()
            return cursor.getInt(0) == 1
        }
        return false
    }

    /**
     * A convinient method to update each row in a column by adding some value
     *
     * @param Column         column to update
     * @param incrementValue a number to add
     */
    fun increaseColumnValues(Column: String, incrementValue: Int) {
        db!!.execSQL("UPDATE $tableName SET $Column=$Column + $incrementValue")
    }

    /**
     * A convinient method to update each row in a column by adding some value
     * with WHERE statement
     *
     * @param Column         column to update
     * @param incrementValue a number to add
     * @param where          a where statement used as filter of records excluding WHERE keyword
     */
    fun increaseColumnValues(Column: String, incrementValue: Int, where: String) {
        db!!.execSQL("UPDATE $tableName SET $Column=$Column + $incrementValue WHERE $where")
    }

}


/** An extention function for ContentValues to get value of type [T] from  a column
 * for an easy access
 * @param column the column to get value from
 * */
inline fun <reified T : Comparable<T>> ContentValues.getValueOf(column: String? = null): T {
    var actualColumn = column
    if (column == null /*&& size()<2*/) {
        val keySet = keySet()
        for (lColumn in keySet) {
            actualColumn = lColumn;
        }
    }
    try {
        return when (T::class) {
            String::class ->
                getAsString(actualColumn) as T
            Int::class ->
                getAsInteger(actualColumn) as T
            Float::class ->
                getAsFloat(actualColumn) as T
            Double::class ->
                getAsDouble(actualColumn) as T
            Date::class ->
                getAsLong(actualColumn) as T
            else -> throw Exception("Unhandled return type")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        throw Exception("the column you query is probably null!!")
    }
}

object Columns {

    inline fun <reified T : Comparable<T>> primaryAutoIncrementColumn(column: String): String {
        return primaryColumn<T>(column) + " AUTOINCREMENT"
    }

    inline fun <reified T : Comparable<T>> primaryColumn(column: String): String {
        return column<T>(column) + " PRIMARY KEY"
    }

    inline fun <reified T : Comparable<T>> uniqueColumn(column: String): String {
        return column<T>(column) + " NOT NULL UNIQUE"
    }

    fun customColumn(columnStatement: String): String {
        return columnStatement;
    }

    inline fun <reified T : Comparable<T>> column(column: String): String {
        return when (T::class) {
            String::class ->
                "$column VARCHAR"
            Int::class ->
                "$column INTEGER"
            Double::class ->
                "$column DOUBLE "
            Float::class ->
                "$column FLOAT "
            Date::class ->
                "$column DATETIME "
            else -> throw  Exception("Unsupported column type")
        }
    }

    fun getForeignKeyStatement(
        Foreign_column: String,
        Reference_table: String,
        Reference_column: String
    ): String {
        return " FOREIGN KEY ($Foreign_column) REFERENCES $Reference_table($Reference_column));"
    }

    fun getDateColumnWithDefaultDate(column: String): String {
        return "$column DATETIME DEFAULT CURRENT_TIMESTAMP "
    }

}