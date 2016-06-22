package ir.treeco.aftabe2.Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ir.treeco.aftabe2.Util.Logger;

import com.google.gson.Gson;

import java.util.ArrayList;

import ir.treeco.aftabe2.Object.Level;
import ir.treeco.aftabe2.Object.PackageObject;

public class DBAdapter {
    private static DBAdapter ourInstance;

    private static final String TAG = "DBAdapter";
    private static final String DATABASE_NAME = "aftabe.db";
    private static final int DATABASE_VERSION = 3;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String BLOB_TYPE = " BLOB";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String CREATE_TABLE = "CREATE TABLE ";
    private static final String AUTOINCREMENT = " AUTOINCREMENT";
    private static final String COMMA_SEP = ", ";
    private static final String BRACKET_OPEN_SEP = " (";
    private static final String BRACKET_CLOSE_SEP = ")";
    private static final String SEMICOLON = ";";
    private static final String NOT_NULL = " NOT NULL";
    private static final String UNIQUE = " UNIQUE";
    private static final String DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS ";

    private static final String PACKAGES = "PACKAGES";
    private static final String PACKAGE_SQL_ID = "PACKAGE_SQL_ID";
    private static final String PACKAGE_ID = "PACKAGE_ID";
    private static final String PACKAGE_GSON = "PACKAGE_GSON";

    private static final String SQL_CREATE_PACKAGES = CREATE_TABLE + PACKAGES + BRACKET_OPEN_SEP +
            PACKAGE_SQL_ID + INTEGER_TYPE + PRIMARY_KEY + AUTOINCREMENT + COMMA_SEP +
            PACKAGE_ID + INTEGER_TYPE + NOT_NULL + UNIQUE + COMMA_SEP +
            PACKAGE_GSON + TEXT_TYPE + BRACKET_CLOSE_SEP + SEMICOLON;

    private static final String LEVELS = "LEVELS";
    private static final String LEVEL_SQL_ID = "LEVEL_SQL_ID";
    private static final String LEVEL_ID = "LEVEL_ID";
    private static final String LEVEL_SOLUTION = "LEVEL_SOLUTION";
    private static final String LEVEL_RESOLVE = "LEVEL_RESOLVE";
    private static final String LEVEL_RESOURCES = "LEVEL_RESOURCES";
    private static final String LEVEL_THUMBNAIL = "LEVEL_THUMBNAIL";
    private static final String LEVEL_TYPE = "LEVEL_TYPE";
    private static final String LEVEL_PACKAGE_ID = "LEVEL_PACKAGE_ID";

    private static final String SQL_CREATE_LEVELS = CREATE_TABLE + LEVELS + BRACKET_OPEN_SEP +
            LEVEL_SQL_ID + INTEGER_TYPE + PRIMARY_KEY + AUTOINCREMENT + COMMA_SEP +
            LEVEL_ID + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
            LEVEL_SOLUTION + TEXT_TYPE + COMMA_SEP +
            LEVEL_RESOURCES + TEXT_TYPE + COMMA_SEP +
            LEVEL_THUMBNAIL + TEXT_TYPE + COMMA_SEP +
            LEVEL_TYPE + TEXT_TYPE + COMMA_SEP +
            LEVEL_RESOLVE + BLOB_TYPE + COMMA_SEP +
            LEVEL_PACKAGE_ID + INTEGER_TYPE + BRACKET_CLOSE_SEP + SEMICOLON;

    private static final String COINS = "COINS";
    private static final String COINS_SQL_ID = "COINS_SQL_ID";
    private static final String COINS_COUNT = "COINS_COUNT";
    private static final String COINS_REVIEWED = "COINS_REVIEWED";

    private static final String SQL_CREATE_COINS = CREATE_TABLE + COINS + BRACKET_OPEN_SEP +
            COINS_SQL_ID + INTEGER_TYPE + PRIMARY_KEY + AUTOINCREMENT + COMMA_SEP +
            COINS_COUNT + INTEGER_TYPE + COMMA_SEP +
            COINS_REVIEWED + BLOB_TYPE + BRACKET_CLOSE_SEP + SEMICOLON;

    private static final String FRIENDS = "FRIENDS";
    private static final String FRIEND_ID = "FRIEND_ID";
    private static final String FRIENDS_SQL_ID = "FRIEND_SQL_ID";
    private static final String FRIEND_USER_GSON = "FRIEND_USER_GSON";

    private static final String SQL_CREATE_FRIENDS = CREATE_TABLE + FRIENDS + BRACKET_OPEN_SEP +
//            FRIENDS_SQL_ID + INTEGER_TYPE +  + AUTOINCREMENT + COMMA_SEP +
            FRIEND_ID + TEXT_TYPE + PRIMARY_KEY + COMMA_SEP +
            FRIEND_USER_GSON + TEXT_TYPE + BRACKET_CLOSE_SEP + SEMICOLON;

    private Integer coin = null;
    private Object coinLock = new Object();

    private static Object lock = new Object();

    public static DBAdapter getInstance(Context context) {
        synchronized (lock) {
            if (ourInstance == null) {
                ourInstance = new DBAdapter(context);
            }
        }

        return ourInstance;
    }

    private DBAdapter(Context context) {
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(SQL_CREATE_PACKAGES);
                db.execSQL(SQL_CREATE_LEVELS);
                db.execSQL(SQL_CREATE_COINS);
//                db.execSQL(SQL_CREATE_FRIENDS);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Logger.d(TAG, "Upgrading database from version" + oldVersion + "to" + newVersion + ", which will destroy all old data");
            db.execSQL(DROP_TABLE_IF_EXISTS + PACKAGES);
            db.execSQL(DROP_TABLE_IF_EXISTS + LEVELS);
            db.execSQL(DROP_TABLE_IF_EXISTS + COINS);
//            db.execSQL(DROP_TABLE_IF_EXISTS + FRIENDS);

            onCreate(db);
        }
    }

    public DBAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        DBHelper.close();
    }


    public void deletePackageOnly(int id){
        open();

        db.delete(PACKAGES, PACKAGE_ID + " = " + id, null);

        close();
    }

    public void deletePackage(int id) {

        open();

        db.delete(PACKAGES, PACKAGE_ID + " = " + id, null);

        close();

        if (getLevels(id) != null) {

            deletePackageLevels(id);
        }

    }

    private void deletePackageLevels(int id) {

        open();

        db.delete(LEVELS, LEVEL_PACKAGE_ID + " = " + id, null);

        close();
    }

    public void insertPackage(PackageObject packageObject) {

        open();
        ContentValues values = new ContentValues();
        values.put(PACKAGE_ID, packageObject.getId());
        values.put(PACKAGE_GSON, new Gson().toJson(packageObject));

        db.insert(PACKAGES, null, values);


        close();

        if (packageObject.getLevels() != null)
            insertLevels(packageObject.getLevels(), packageObject.getId());
    }

    public void updatePackage(PackageObject object) {

        open();
        ContentValues values = new ContentValues();
        values.put(PACKAGE_GSON, new Gson().toJson(object));
        db.update(PACKAGES, values, PACKAGE_ID + " = " + object.getId(), null);
        close();
    }

    public void insertLevels(ArrayList<Level> levels, int packageID) {
        open();

        for (int i = 0; i < levels.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(LEVEL_ID, levels.get(i).getId());
            values.put(LEVEL_SOLUTION, levels.get(i).getJavab());
            values.put(LEVEL_RESOLVE, false);
            values.put(LEVEL_RESOURCES, levels.get(i).getResources());
            values.put(LEVEL_THUMBNAIL, levels.get(i).getThumbnail());
            values.put(LEVEL_TYPE, levels.get(i).getType());
            values.put(LEVEL_PACKAGE_ID, packageID);
            db.insert(LEVELS, null, values);
        }
        close();
    }

    public Level getLevel(int packageID, int levelID) {
        open();
        Cursor cursor = db.query(LEVELS,
                new String[]{LEVEL_ID, LEVEL_SOLUTION, LEVEL_RESOLVE,
                        LEVEL_RESOURCES, LEVEL_THUMBNAIL, LEVEL_TYPE},
                LEVEL_PACKAGE_ID + " = " + packageID + " AND " + LEVEL_ID + " = " + levelID,
                null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Level level = new Level();
            level.setId(cursor.getInt(cursor.getColumnIndex(LEVEL_ID)));
            level.setJavab(cursor.getString(cursor.getColumnIndex(LEVEL_SOLUTION)));
            level.setResolved(cursor.getInt(cursor.getColumnIndex(LEVEL_RESOLVE)) > 0);
            level.setResources(cursor.getString(cursor.getColumnIndex(LEVEL_RESOURCES)));
            level.setThumbnail(cursor.getString(cursor.getColumnIndex(LEVEL_THUMBNAIL)));
            level.setType(cursor.getString(cursor.getColumnIndex(LEVEL_TYPE)));
            close();
            return level;
        }
        close();
        return null;
    }

    public Level[] getLevels(int packageID) {
        open();
        Cursor cursor = db.query(LEVELS,
                new String[]{LEVEL_ID, LEVEL_SOLUTION, LEVEL_RESOLVE,
                        LEVEL_RESOURCES, LEVEL_THUMBNAIL, LEVEL_TYPE},
                LEVEL_PACKAGE_ID + " = " + packageID,
                null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Level[] levels = new Level[cursor.getCount()];
            for (int i = 0; i < cursor.getCount(); i++, cursor.moveToNext()) {
                Level level = new Level();
                level.setId(cursor.getInt(cursor.getColumnIndex(LEVEL_ID)));
                level.setJavab(cursor.getString(cursor.getColumnIndex(LEVEL_SOLUTION)));
                level.setResolved(cursor.getInt(cursor.getColumnIndex(LEVEL_RESOLVE)) > 0);
                level.setResources(cursor.getString(cursor.getColumnIndex(LEVEL_RESOURCES)));
                level.setThumbnail(cursor.getString(cursor.getColumnIndex(LEVEL_THUMBNAIL)));
                level.setType(cursor.getString(cursor.getColumnIndex(LEVEL_TYPE)));
                levels[i] = level;
            }
            close();
            return levels;
        }
        close();
        return null;
    }

    public PackageObject[] getPackages() {
        open();
        Cursor cursor = db.query(PACKAGES,
                new String[]{PACKAGE_ID, PACKAGE_GSON},
                null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            PackageObject[] packages = new PackageObject[cursor.getCount()];
            for (int i = 0; i < cursor.getCount(); i++, cursor.moveToNext()) {

                String packageGson = cursor.getString(cursor.getColumnIndex(PACKAGE_GSON));

                PackageObject packageObject = new Gson().fromJson(packageGson, PackageObject.class);
                packages[i] = packageObject;
            }
            close();
            return packages;
        }
        close();
        return null;
    }

    public boolean containsCoin() {

        open();
        Cursor cursor = db.query(COINS,
                new String[]{COINS_COUNT},
                COINS_SQL_ID + " = 1",
                null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int count = cursor.getInt(cursor.getColumnIndex(COINS_COUNT));
            close();
            return true;
        }
        return false;

    }

    public int getCoins() {

        Logger.d(TAG, "coin is " + ((coin == null) ? "null" : coin));
        synchronized (coinLock) {

            if (coin != null)
                return coin;

            open();
            Cursor cursor = db.query(COINS,
                    new String[]{COINS_COUNT},
                    COINS_SQL_ID + " = 1",
                    null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getInt(cursor.getColumnIndex(COINS_COUNT));
                close();
                coin = count;
                return count;
            }
            close();

            return 0;
        }
    }

    public boolean getCoinsReviewed() {
        open();
        Cursor cursor = db.query(COINS,
                new String[]{COINS_REVIEWED},
                COINS_SQL_ID + " = 1",
                null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            boolean reviewed = cursor.getInt(cursor.getColumnIndex(COINS_REVIEWED)) > 0;
            close();
            return reviewed;
        }
        close();
        return false;
    }

    public void insertCoins(int count) {

        if (containsCoin())
            return;

        synchronized (coinLock) {

            coin = count;

            open();
            ContentValues values = new ContentValues();
            values.put(COINS_COUNT, count);
            values.put(COINS_REVIEWED, false);
            db.insert(COINS, null, values);
            close();
        }
    }

    public void updateCoins(int count) {
        synchronized (coinLock) {

            coin = count;

            open();
            ContentValues values = new ContentValues();
            values.put(COINS_COUNT, count);
            db.update(COINS, values, COINS_SQL_ID + " = 1", null);
            close();
        }
    }

    public void updateReviewed(boolean reviewed) {
        open();
        ContentValues values = new ContentValues();
        values.put(COINS_REVIEWED, reviewed);
        db.update(COINS, values, COINS_SQL_ID + " = 1", null);
        close();
    }

    public void resolveLevel(int packageID, int levelID) {
        open();

        ContentValues values = new ContentValues();
        values.put(LEVEL_RESOLVE, true);
        db.update(LEVELS, values,
                LEVEL_PACKAGE_ID + " = " + packageID + " AND " + LEVEL_ID + " = " + levelID, null);

        close();
    }
}
