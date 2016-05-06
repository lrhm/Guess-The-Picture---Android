package ir.treeco.aftabe.Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import ir.treeco.aftabe.API.AftabeAPIAdapter;
import ir.treeco.aftabe.API.BatchUserFoundListener;
import ir.treeco.aftabe.API.UserFoundListener;
import ir.treeco.aftabe.Object.Level;
import ir.treeco.aftabe.Object.PackageObject;
import ir.treeco.aftabe.Object.User;

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
    private static final String PACKAGE_NAME = "PACKAGE_NAME";
    private static final String PACKAGE_URL = "PACKAGE_URL";
    private static final String PACKAGE_DOWNLOADED = "PACKAGE_DOWNLOADED";

    private static final String SQL_CREATE_PACKAGES = CREATE_TABLE + PACKAGES + BRACKET_OPEN_SEP +
            PACKAGE_SQL_ID + INTEGER_TYPE + PRIMARY_KEY + AUTOINCREMENT + COMMA_SEP +
            PACKAGE_ID + INTEGER_TYPE + NOT_NULL + UNIQUE + COMMA_SEP +
            PACKAGE_NAME + TEXT_TYPE + COMMA_SEP +
            PACKAGE_URL + TEXT_TYPE + COMMA_SEP +
            PACKAGE_DOWNLOADED + BLOB_TYPE + BRACKET_CLOSE_SEP + SEMICOLON;

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


    private ArrayList<User> mCachedFriends = null;

    private Object friendsLock = new Object();

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
                db.execSQL(SQL_CREATE_FRIENDS);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version" + oldVersion + "to" + newVersion + ", which will destroy all old data");
            db.execSQL(DROP_TABLE_IF_EXISTS + PACKAGES);
            db.execSQL(DROP_TABLE_IF_EXISTS + LEVELS);
            db.execSQL(DROP_TABLE_IF_EXISTS + COINS);
            db.execSQL(DROP_TABLE_IF_EXISTS + FRIENDS);

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

    public void addFriendToDB(User otherUser) {

        if (otherUser.isMe())
            return;

        if (isFriendInDB(otherUser))
            return;

        Gson gson = new Gson();
        String friendGsonString = gson.toJson(otherUser);

        synchronized (friendsLock) {

            open();
            ContentValues values = new ContentValues();
            values.put(FRIEND_ID, otherUser.getId());
            values.put(FRIEND_USER_GSON, "'" + friendGsonString + "'");
            db.insert(FRIENDS, null, values);
            close();

            if (mCachedFriends != null)
                mCachedFriends.add(otherUser);
        }
    }

    public ArrayList<User> getMyCachedFriends() {

        synchronized (friendsLock) {

            if (mCachedFriends != null)
                return mCachedFriends;
        }

        ArrayList<User> list = new ArrayList<>();

        synchronized (friendsLock) {
            open();

            Cursor cursor = db.query(FRIENDS,
                    new String[]{FRIEND_USER_GSON},
                    null,
                    null, null, null, null);

            if (cursor != null) {
                Gson gson = new Gson();
                while (cursor.moveToNext()) {
//                Log.d(TAG, cursor.getString(cursor.getColumnIndex(FRIEND_USER_GSON)));
                    list.add(gson.fromJson(cursor.getString(cursor.getColumnIndex(FRIEND_USER_GSON)).replace("'", ""), User.class));
                }
            }
            close();
        }
        return list;
    }

    public void updateFriendInDB(User friendUser) {

        open();
        ContentValues values = new ContentValues();
        Gson gson = new Gson();
        values.put(FRIEND_USER_GSON, "'" + gson.toJson(friendUser) + "'");
//        Log.d(TAG, gson.toJson(friendUser));
        db.update(FRIENDS, values, FRIEND_ID + " = '" + friendUser.getId() + "'", null);
        close();

    }

    public void deleteFriendInDB(User friendUser) {

        open();
        db.delete(FRIENDS, FRIEND_ID + " = '" + friendUser.getId() + "'", null);
        close();

    }


    public void updateFriendsFromAPI(User[] newList) {

        ArrayList<User> myFriends = getMyCachedFriends();
        HashMap<String, Boolean> found = new HashMap<>();

        for (User updatedFriend : newList) {
            for (User myFriend : myFriends) {
                if (myFriend.getId().equals(updatedFriend.getId())) {
                    Gson gson = new Gson();
                    found.put(myFriend.getId(), true);
                    if (!gson.toJson(myFriend).equals(gson.toJson(updatedFriend)))
                        updateFriendInDB(updatedFriend);
                    break;

                }
            } //Didnt find this user in friend list , add

            addFriendToDB(updatedFriend);


        }

        for (User user : myFriends) {
            Boolean isFound = found.get(user.getId());
            if (isFound == null) {
                deleteFriendInDB(user);
            }
        }
        // remove those wich are not friends anymore


    }

    public boolean isFriendInDB(User otherUser) {
        open();

        try {
            Cursor cursor = db.query(FRIENDS,
                    new String[]{FRIEND_USER_GSON, FRIEND_ID},
                    FRIEND_ID + " =  \"" + otherUser.getId() + "\"",
                    null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                close();
                return true;
            }
            close();
            return false;
        } catch (Exception e) {
            close();
            return false;
        }
    }


    public void insertPackage(PackageObject packageObject) {
        open();
        ContentValues values = new ContentValues();
        values.put(PACKAGE_ID, packageObject.getId());
        values.put(PACKAGE_NAME, packageObject.getName());
        values.put(PACKAGE_URL, packageObject.getUrl());
        values.put(PACKAGE_DOWNLOADED, true);
        db.insert(PACKAGES, null, values);
        close();

        insertLevels(packageObject.getLevels(), packageObject.getId());
    }

    private void insertLevels(ArrayList<Level> levels, int packageID) {
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
                new String[]{PACKAGE_ID, PACKAGE_NAME, PACKAGE_URL, PACKAGE_DOWNLOADED},
                null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            PackageObject[] packages = new PackageObject[cursor.getCount()];
            for (int i = 0; i < cursor.getCount(); i++, cursor.moveToNext()) {
                PackageObject packageObject = new PackageObject();
                packageObject.setId(cursor.getInt(cursor.getColumnIndex(PACKAGE_ID)));
                packageObject.setName(cursor.getString(cursor.getColumnIndex(PACKAGE_NAME)));
                packageObject.setUrl(cursor.getString(cursor.getColumnIndex(PACKAGE_URL)));
                packages[i] = packageObject;
            }
            close();
            return packages;
        }
        close();
        return null;
    }

    public int getCoins() {
        open();
        Cursor cursor = db.query(COINS,
                new String[]{COINS_COUNT},
                COINS_SQL_ID + " = 1",
                null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int count = cursor.getInt(cursor.getColumnIndex(COINS_COUNT));
            close();
            return count;
        }
        close();
        return 0;
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
        open();
        ContentValues values = new ContentValues();
        values.put(COINS_COUNT, count);
        values.put(COINS_REVIEWED, false);
        db.insert(COINS, null, values);
        close();
    }

    public void updateCoins(int count) {
        open();
        ContentValues values = new ContentValues();
        values.put(COINS_COUNT, count);
        db.update(COINS, values, COINS_SQL_ID + " = 1", null);
        close();
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
