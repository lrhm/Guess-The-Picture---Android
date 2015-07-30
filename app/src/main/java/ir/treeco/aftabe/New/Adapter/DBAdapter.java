package ir.treeco.aftabe.New.Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import ir.treeco.aftabe.New.Object.Level;
import ir.treeco.aftabe.New.Object.PackageObject;

/**
 * Created by behdad on 7/30/15.
 */
public class DBAdapter {
    private static DBAdapter ourInstance;

    private static final String TAG = "DBAdapter";
    private static final String DATABASE_NAME = "da";
    private static final int DATABASE_VERSION = 1;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String BLOB = " BLOB";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String CREATE_TABLE = "CREATE TABLE ";
    private static final String AUTOINCREMENT = " AUTOINCREMENT";
    private static final String COMMA_SEP = ", ";
    private static final String BRACKET_OPEN_SEP = " (";
    private static final String BRACKET_CLOSE_SEP = ")";
    private static final String SEMICOLON = ";";
    private static final String NOT_NULL = " NOT NULL";
    private static final String UNIQUE = " UNIQUE";
    private static final String DROP_TABLE_IF_EXISTS  = "DROP TABLE IF EXISTS ";

    private static final String PACKAGES = "PACKAGES";
    private static final String PACKAGESQLID = "PACKAGESQLID";
    private static final String PACKAGEID = "PACKAGEID";
    private static final String PACKAGENAME = "PACKAGENAME";
    private static final String PACKAGEURL = "PACKAGEURL";
    private static final String PACKAGEDOWNLOADED = "PACKAGEDOWNLOADED";

    private static final String SQL_CREATE_PACKAGES = CREATE_TABLE + PACKAGES + BRACKET_OPEN_SEP +
            PACKAGESQLID + INTEGER_TYPE + PRIMARY_KEY + AUTOINCREMENT + COMMA_SEP +
            PACKAGEID + INTEGER_TYPE + NOT_NULL + UNIQUE +
            PACKAGENAME + TEXT_TYPE + COMMA_SEP +
            PACKAGEURL + TEXT_TYPE + COMMA_SEP +
            PACKAGEDOWNLOADED + BLOB + BRACKET_CLOSE_SEP + SEMICOLON;

    private static final String LEVELS = "LEVELS";
    private static final String LEVELSQLID = "LEVELSQLID";
    private static final String LEVELID = "LEVELID";
    private static final String LEVELSOLUTION = "LEVELSOLUTION";
    private static final String LEVELRESOLVES = "LEVELRESOLVES";
    private static final String LEVELRESOURCES = "LEVELRESOURCES";
    private static final String LEVELTHUMBNAIL = "LEVELTHUMBNAIL";
    private static final String LEVELTYPE = "LEVELTYPE";
    private static final String LEVELPACKAGEID = "LEVELPACKAGEID";

    private static final String SQL_CREATE_LEVELS = CREATE_TABLE + PACKAGES + BRACKET_OPEN_SEP +
            LEVELSQLID + INTEGER_TYPE + PRIMARY_KEY + AUTOINCREMENT + COMMA_SEP +
            LEVELID + INTEGER_TYPE + NOT_NULL +
            LEVELSOLUTION + TEXT_TYPE + COMMA_SEP +
            LEVELRESOURCES + TEXT_TYPE + COMMA_SEP +
            LEVELTHUMBNAIL + TEXT_TYPE + COMMA_SEP +
            LEVELTYPE + TEXT_TYPE + COMMA_SEP +
            LEVELRESOLVES + BLOB +
            LEVELPACKAGEID + INTEGER_TYPE +BRACKET_CLOSE_SEP + SEMICOLON;


    public static DBAdapter getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new DBAdapter(context);
        }

        return ourInstance;
    }

    private DBAdapter(Context context) {
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            try {
                db.execSQL(SQL_CREATE_PACKAGES);
                db.execSQL(SQL_CREATE_LEVELS);
            } catch ( SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            Log.w(TAG, "Upgrading database from version" + oldVersion + "to" + newVersion + ", which will destroy all old data");
            db.execSQL(DROP_TABLE_IF_EXISTS + PACKAGES);
            db.execSQL(DROP_TABLE_IF_EXISTS + LEVELS);
            onCreate(db);
        }
    }

    public DBAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        DBHelper.close();
    }

    public void insertPackage(PackageObject packageObject) {
        open();
        ContentValues values = new ContentValues();
        values.put(PACKAGEID, packageObject.getId());
        values.put(PACKAGENAME, packageObject.getName());
        values.put(PACKAGEURL, packageObject.getUrl());
        values.put(PACKAGEDOWNLOADED, true);
        close();

        insertLevels(packageObject.getLevels(), packageObject.getId());
    }

    public void insertLevels(ArrayList<Level> levels, int packageID) {
        open();

        for (int i = 0; i < levels.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(LEVELID, levels.get(i).getId());
            values.put(LEVELSOLUTION, levels.get(i).getJavab());
            values.put(LEVELRESOLVES, false);
            values.put(LEVELRESOURCES, levels.get(i).getResources());
            values.put(LEVELTHUMBNAIL, levels.get(i).getThumbnail());
            values.put(LEVELTYPE, levels.get(i).getType());
            values.put(LEVELPACKAGEID, packageID);
        }
        close();
    }
}
