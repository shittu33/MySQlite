package com.example.mysqlite.utils;

import android.os.Environment;

import java.io.File;

public class MyDatabaseUtils {
    /**
     * @param app_dir your app filename, if you have none just put any name you like
     * @param database_name the name of the database
     * */
    public static String getDatabasePath(String app_dir,String database_name) {
        String database_path = Environment.getExternalStorageDirectory()
                + File.separator + app_dir + File.separator
                + "Databases";
        File database_dir = new File(database_path);
        if (!database_dir.exists()) //noinspection ResultOfMethodCallIgnored
            database_dir.mkdirs();
        return database_dir.getAbsolutePath() + File.separator + database_name;
    }
}
