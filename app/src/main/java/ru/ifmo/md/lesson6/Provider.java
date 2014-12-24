package ru.ifmo.md.lesson6;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

/**
 * Created by Snopi on 01.12.2014.
 */
public class Provider extends ContentProvider {
    private DataBaseHelper db = null;


    private static final int CONSTANTS = 1;
    private static final int CONSTANT_ID = 2;
    private static final UriMatcher MATCHER;
    private static final String TABLE = "constants";

    public static final class Constants implements BaseColumns {
        public static final Uri CONTENT_URI =
                Uri.parse("content://ru.ifmo.md.lesson6.Provider/constants");
        public static final String DEFAULT_SORT_ORDER = "_id DESC";
        public static final String TITLE = "title";
        public static final String VALUE = "value";
    }
    static {
        MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        MATCHER.addURI("ru.ifmo.md.lesson6.Provider", "constants", CONSTANTS);
        MATCHER.addURI("ru.ifmo.md.lesson6..Provider", "constants/#", CONSTANT_ID);
    }
    @Override
    public boolean onCreate() {
        db = new DataBaseHelper(getContext());
        if (db == null) {
            return  false;
        }
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE);
        String orderBy = Constants.DEFAULT_SORT_ORDER;
        if (!TextUtils.isEmpty(sortOrder)) {
            orderBy = sortOrder;
        }
        Cursor c = qb.query(db.getReadableDatabase(), projection, selection, selectionArgs, null, null, orderBy);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = db.getWritableDatabase().insert(TABLE, Constants.TITLE, values);
        if (rowID > 0) {
            Uri ur = ContentUris.withAppendedId(Constants.CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(ur, null);
            return uri;
        }
        return uri;   // throw new SQLiteException("Failed to insert raw into: " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = db.getWritableDatabase().delete(TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = db.getWritableDatabase().update(TABLE, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
