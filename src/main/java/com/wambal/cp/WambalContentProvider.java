/*
 * Copyright 2009 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package com.wambal.cp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

public final class WambalContentProvider extends ContentProvider {

    public static final String DATABASE_NAME = "wambal.db";
    public static final int DATABASE_VERSION = 1;
    public static final String CATEGORY_TABLE_NAME = "CATEGORY";
    public static final int CATEGORY_URI_SINGLE = 1;
    public static final int CATEGORY_URI_MULTIPLE = 2;
    public static final String AUTHORITY = "com.wambal.cp.wambalcontentprovider";

    public static final String MIME_DIR_PREFIX =
    "vnd.android.cursor.dir";
    public static final String MIME_ITEM_PREFIX =
    "vnd.android.cursor.item";
    public static final Uri CONTENT_URI = Category.CONTENT_URI_MULTIPLE;

    private static Map<String,String> categoryProjectionMap;


    private static UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, Category.PATH_SINGLE, CATEGORY_URI_SINGLE);
        uriMatcher.addURI(AUTHORITY, Category.PATH_MULTIPLE, CATEGORY_URI_MULTIPLE);
        categoryProjectionMap = new HashMap<String, String>();
        categoryProjectionMap.put(Category._ID, Category._ID);
        categoryProjectionMap.put(Category.NAME, Category.NAME);
    }
    private DatabaseHelper databaseHelper;

    public static final class Category implements BaseColumns {
        public static final String NAME = "name";
        public static final String PATH_SINGLE = "categories/#";
        public static final String PATH_MULTIPLE = "categories";
        public static final Uri CONTENT_URI_SINGLE = Uri.parse("content://" + AUTHORITY + "/" + PATH_SINGLE);
        public static final Uri CONTENT_URI_MULTIPLE = Uri.parse("content://" + AUTHORITY + "/" + PATH_MULTIPLE);
        public static final String MIME_ITEM = "vnd.wambal.category";
        public static final String MIME_TYPE_SINGLE = MIME_ITEM_PREFIX + "/" + MIME_ITEM;
        public static final String MIME_TYPE_MULTIPLE = MIME_DIR_PREFIX + "/" + MIME_ITEM;
    }

    public static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override public void onCreate(final SQLiteDatabase db) {
            String tableCreate = "CREATE TABLE " + CATEGORY_TABLE_NAME + "("
                    + Category._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Category.NAME + " TEXT);";
            db.execSQL(tableCreate);
        }

        @Override public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE_NAME);
            onCreate(db);
        }
    }

    @Override public boolean onCreate() {
        databaseHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override public Cursor query(final Uri uri, final String[] projection, final String selection, final String[] selectionArgs,
                                  final String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(CATEGORY_TABLE_NAME);
        switch(uriMatcher.match(uri)) {
            case CATEGORY_URI_MULTIPLE:
                builder.setProjectionMap(categoryProjectionMap);
                break;
            case CATEGORY_URI_SINGLE:
                builder.setProjectionMap(categoryProjectionMap);
                builder.appendWhere(Category._ID + "=" + uri.getPathSegments().get(1));
                break;
            default: throw new IllegalStateException("Unknown URI: " + uri);
        }
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override public String getType(final Uri uri) {
        switch(uriMatcher.match(uri)) {
            case CATEGORY_URI_SINGLE: return Category.MIME_TYPE_SINGLE;
            case CATEGORY_URI_MULTIPLE: return Category.MIME_TYPE_MULTIPLE;
            default: throw new IllegalStateException("Unknow MIMETYPE for uri: " + uri);
        }
   }

    @Override public Uri insert(final Uri uri, final ContentValues contentValues) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        long rowId = db.insert(CATEGORY_TABLE_NAME, null, contentValues);
        if (rowId != -1) {
            Uri updatedUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(updatedUri, null);
            return updatedUri;
        } else {
            throw new SQLException("Failed to insert row into: " + uri);
        }
    }

    @Override public int delete(final Uri uri, final String whereClause, final String[] whereArgs) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int count;
        switch (uriMatcher.match(uri)) {
            case CATEGORY_URI_SINGLE:
                String id = uri.getPathSegments().get(1);
                count = db.delete(CATEGORY_TABLE_NAME, Category._ID + "=" + id +
                        (!TextUtils.isEmpty(whereClause) ? " AND (" + whereClause + ')' : ""), whereArgs);
                break;
            case CATEGORY_URI_MULTIPLE:
                count = db.delete(CATEGORY_TABLE_NAME, whereClause, whereArgs);
                break;
            default: throw new IllegalStateException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override public int update(final Uri uri, final ContentValues contentValues, final String whereClause, final String[] whereArgs) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int update;
        switch(uriMatcher.match(uri)) {
           case CATEGORY_URI_SINGLE:
               String id = uri.getPathSegments().get(1);
               update = db.update(CATEGORY_TABLE_NAME, contentValues, Category._ID + "=" + id +
                       (!TextUtils.isEmpty(whereClause) ? " AND (" + whereClause + ')' : ""), whereArgs);
               break;
           case CATEGORY_URI_MULTIPLE:
               update = db.update(CATEGORY_TABLE_NAME, contentValues, whereClause, whereArgs);
               break;
           default: throw new IllegalStateException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return update;
    }
}
