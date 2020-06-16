package com.example.mysqlite.engine;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Abu Muhsin on 15/02/2018.
 */

public class DatabaseCreator extends SQLiteOpenHelper {
    public static final String TAG = "debug";
    SQLiteDatabase db = getWritableDatabase();

    /**
     * Use to create a temporary in-memory database
     * @param context to use for locating paths to the the database
     *                To create an in-memory database
     * @param version number of the database (starting at 1); if the database is older,
     *                {@link #onUpgrade} will be used to upgrade the database; if the database is
     *                newer, {@link #onDowngrade} will be used to downgrade the database
     */
    public DatabaseCreator(Context context, int version) {
        super(context, null, null, version);
    }

    /**
     * To create database with a specific name or inside a specific  file name
     *
     * @param context   to use for locating paths to the the database
     * @param file_path database name or file path́
     * @param version   number of the database (starting at 1); if the database is older,
     *                  {@link #onUpgrade} will be used to upgrade the database; if the database is
     *                  newer, {@link #onDowngrade} will be used to downgrade the database
     */
    public DatabaseCreator(Context context, String file_path, int version) {
        super(context, file_path, null, version);
    }

    /**
     * @param context   to use for locating paths to the the database
     * @param file_path of the database file_path, or null for an in-memory database
     * @param factory   to use for creating cursor objects, or null for the default
     * @param version   number of the database (starting at 1); if the database is older,
     *                  {@link #onUpgrade} will be used to upgrade the database; if the database is
     *                  newer, {@link #onDowngrade} will be used to downgrade the database
     */
    public DatabaseCreator(Context context, String file_path, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, file_path, factory, version);
    }


    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Todo by you
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
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Todo by you
    }

    /**
     * Called when the database needs to be downgraded. This is strictly similar to
     * {@link #onUpgrade} method, but is called whenever current version is newer than requested one.
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
        //Todo by you
    }

}
