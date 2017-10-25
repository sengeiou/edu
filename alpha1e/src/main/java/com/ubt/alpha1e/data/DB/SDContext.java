package com.ubt.alpha1e.data.DB;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.ubt.alpha1e.data.FileTools;

import java.io.File;

public class SDContext extends ContextWrapper {

    private String mDbPath;

    public SDContext(Context base, String dbPath) {
        super(base);
        mDbPath = dbPath;
    }

    @Override
    public File getDatabasePath(String db_name) {
        File db_file = new File(mDbPath, db_name);
        if (db_file.exists())
            return db_file;

        File path = new File(mDbPath);
        if (!path.exists())
            path.mkdirs();

        boolean isFileCreateSuccess = false;

        isFileCreateSuccess = FileTools.writeAssetsToSd("DB/" + db_name, this, mDbPath + "/" + db_name);


        if (isFileCreateSuccess)
            return db_file;

        return null;
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String db_name, int mode,
                                               CursorFactory factory) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(
                getDatabasePath(db_name), null);
        return result;
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String db_name, int mode,
                                               CursorFactory factory, DatabaseErrorHandler errorHandler) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(
                getDatabasePath(db_name), null);
        return result;
    }

}