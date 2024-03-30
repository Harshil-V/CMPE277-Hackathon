package com.example.cmpe277_hackathon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "annotations.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "annotation_records";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_GRAPH_NAME = "graph_name";
    private static final String COLUMN_ANNOTATION_TEXT = "annotation_text";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_GRAPH_NAME + " TEXT," +
                    COLUMN_ANNOTATION_TEXT + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertRecord(String graphName, String annotationText) {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(COLUMN_GRAPH_NAME, graphName);
        values.put(COLUMN_ANNOTATION_TEXT, annotationText);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TABLE_NAME, null, values);
    }

    public List<String> getAnnotationsByGraphName(String graphName) {
        List<String> annotations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_ANNOTATION_TEXT // Assuming you only need the text
        };

        String selection = COLUMN_GRAPH_NAME + " = ?";
        String[] selectionArgs = { graphName };

        Cursor cursor = db.query(
                TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            String annotationText = cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_ANNOTATION_TEXT));
            annotations.add(annotationText);
        }
        cursor.close();

        return annotations;
    }

    public int getAnnotationsCount(String graphName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_GRAPH_NAME + " = ?";
        String[] selectionArgs = { graphName };

        Cursor cursor = db.query(
                TABLE_NAME,
                new String[] { "COUNT(*) as count" },
                selection,
                selectionArgs,
                null,
                null,
                null          
        );

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

}
