package com.triocodes.krishikkaran.Data;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

/**
 * Created by admin on 08-03-16.
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    protected static String DB_PATH;
    private static final String DB_NAME = "Krishikkarandb.sqlite";
    private static final int DATABASE_VERSION = 1;
    private static SQLiteDatabase db;
    private final Context myContext;

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.myContext = context;
        Resources resources = myContext.getResources();
        AssetManager assetManager = resources.getAssets();
       /* DB_PATH = Environment.getExternalStorageDirectory() + "/";*/
        DB_PATH = myContext.getExternalFilesDir(null) + "/";
    }

    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        if (dbExist) {

        } else {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);

        } catch (SQLiteException e) {

        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;

    }
    public void DeleteDatabase() {
        String myPath = DB_PATH + DB_NAME;
        SQLiteDatabase.deleteDatabase(new File(myPath));
    }

    private void copyDataBase()throws IOException{
        InputStream myInput=myContext.getAssets().open("Krishikkarandb.sqlite");
        String OutFileName=DB_PATH+DB_NAME;
        OutputStream myOutput=new FileOutputStream(OutFileName);
        byte[] buffer=new byte[1024];
        int length;
        while((length=myInput.read(buffer))>0){
            myOutput.write(buffer,0,length);

        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDatabase()throws android.database.SQLException{
        String myPath=DB_PATH+DB_NAME;
        db=SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.NO_LOCALIZED_COLLATORS);
    }

    @Override
    public synchronized void close() {
        if(db!=null){
            db.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void getQueryhelper(){
        new DataBaseQueryHelper(myContext,db);
    }
}
