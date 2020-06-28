package com.example.mysqlite.engine

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by Abu Muhsin on 15/02/2018.
 */
class DatabaseCreator : SQLiteOpenHelper {

    /**
     * To create database with a specific name or inside a specific  file name
     *
     * @param context   to use for locating paths to the the database
     * @param databaseName database name or file path́, an in memory database is created if null
     * @param version   number of the database (starting at 1); if the database is older,
     * [.onUpgrade] will be used to upgrade the database; if the database is
     * newer, [.onDowngrade] will be used to downgrade the database
     */
    constructor(context: Context?, databaseName: String? = null, version: Int = 0) : super(
        context,
        databaseName,
        null,
        version
    ) {

    }

    /**
     * @param context   to use for locating paths to the the database
     * @param databaseName of the database file_path, or null for an in-memory database
     * @param factory   to use for creating cursor objects, or null for the default
     * @param version   number of the database (starting at 1); if the database is older,
     * [.onUpgrade] will be used to upgrade the database; if the database is
     * newer, [.onDowngrade] will be used to downgrade the database
     */
    constructor(
        context: Context?,
        databaseName: String?,
        factory: CursorFactory?,
        version: Int
    ) : super(context, databaseName, factory, version) {
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    override fun onCreate(db: SQLiteDatabase) {
        //Todo by you
        dbListener?.onCreate(db)
    }

    /**
     * Called when the database needs to be upgraded. you should
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        dbListener?.onUpgrade(db, oldVersion, newVersion)
        //Todo by you
    }

    /**
     * Called when the database needs to be downgraded. This is strictly similar to
     * [.onUpgrade] method, but is called whenever current version is newer than requested one.
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    override fun onDowngrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        super.onDowngrade(db, oldVersion, newVersion)
        dbListener?.onDowngrade(db, oldVersion, newVersion)
        //Todo by you
    }

    companion object {
        const val TAG = "debug"
    }

    var dbListener: DatabaseListener? = null
        set(value) {
            field = value
        }

    fun setDatabaseListener(dbl: DatabaseListener) {
        dbListener = dbl
    }

    interface DatabaseListener {
        fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int)
        fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int)
        fun onCreate(db: SQLiteDatabase)
    }
}
    //To perform operation when upgraded,create and downgraded call the function
    // like This
//    myDb.dbListener=object:DatabaseCreator.DatabaseListener{
//        override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
//            TODO("Not yet implemented")
//        }
//
//        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
//            TODO("Not yet implemented")
//        }
//
//        override fun onCreate(db: SQLiteDatabase) {
//            TODO("Not yet implemented")
//        }
//
//    }
//}