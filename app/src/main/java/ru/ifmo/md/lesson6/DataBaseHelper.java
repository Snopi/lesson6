package ru.ifmo.md.lesson6;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by Snopi on 30.11.2014.
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "constants.db";
    private static final int SCHEMA = 11;
    public static final class COLUMNS {
        public static final String ID = "_id";
        public static final String CHANNEL_NAME = "channel_name";
        public static final String URL = "url";
        public static final String TITLE = "title";
        public static final String TABLE_NAME = "constants";
        public static final String DESCRIPTION = "description";
        public static final String TIME = "time";
        public static final String SPECIAL_FLAG = "special_flag";
        public static final int CHANNEL_FLAG = 0;
        public static final int FEED_FLAG = 1;
        public static final String CHANNEL_URL = "channel_url";
    }
    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Cursor c = db.rawQuery("SELECT name from sqlite_master where type = 'table'" +
                " AND name = 'constants'", null);
        try {
            if (c.getCount() == 0) {
                db.execSQL("CREATE TABLE constants " +
                        "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " url TEXT UNIQUE," +
                        " channel_name TEXT, " +
                        " title TEXT," +
                        " description TEXT," +
                        " time TEXT," +
                        " special_flag integer," +
                        " channel_url TEXT);");
              }
            ContentValues cv = new ContentValues();
            String bash = "http://bash.im/rss/";
            cv.put(COLUMNS.SPECIAL_FLAG, COLUMNS.CHANNEL_FLAG);
            cv.put(COLUMNS.CHANNEL_NAME, "Bash.im");
            cv.put(COLUMNS.CHANNEL_URL, bash);
            cv.put(COLUMNS.URL, bash);
            db.insert("constants", null, cv);
            bash = "http://www.vesti.ru/vesti.rss";
            cv.put(COLUMNS.SPECIAL_FLAG, COLUMNS.CHANNEL_FLAG);
            cv.put(COLUMNS.CHANNEL_NAME, "ВЕСТИ");
            cv.put(COLUMNS.CHANNEL_URL, bash);
            cv.put(COLUMNS.URL, bash);
            db.insert("constants", null, cv);
        } finally {
            c.close();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists constants");
        onCreate(db);
    }
}
