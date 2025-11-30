package com.example.mobiledevlopmentproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import java.util.ArrayList;

/**
 * DBLeaderboard
 * A separate database handler specifically for the Leaderboard functionality.
 * This ensures no conflicts with the main DBHandler used by teammates.
 */
public class DBLeaderboard extends SQLiteOpenHelper {

    // 1. Database Configuration
    // Using a distinct file name to isolate data
    private static final String DB_NAME = "Leaderboard.db";
    private static final int DB_VERSION = 1;

    // 2. Table and Column Names
    private static final String TABLE_NAME = "scores";
    private static final String COL_ID = "id";
    private static final String COL_SETNAME = "setname"; // Which flashcard set was played
    private static final String COL_SCORE = "score";     // e.g., "10/10"
    private static final String COL_TIME = "time";       // e.g., "00:45"
    private static final String COL_DATE = "date";       // e.g., "2025-11-30 14:00"

    public DBLeaderboard(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the scores table
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_SETNAME + " TEXT,"
                + COL_SCORE + " TEXT,"
                + COL_TIME + " TEXT,"
                + COL_DATE + " TEXT)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if exists and recreate
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Insert a new game score into the database.
     */
    public void addScore(String setName, String score, String time, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_SETNAME, setName);
        values.put(COL_SCORE, score);
        values.put(COL_TIME, time);
        values.put(COL_DATE, date);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    /**
     * Retrieve all scores, sorted by newest first.
     * @return List of formatted strings for display.
     */
    public ArrayList<String> getLeaderboardData() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query all rows, ordering by ID descending (newest games at the top)
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_ID + " DESC", null);

        if (cursor.moveToFirst()) {
            do {
                // Extract data from cursor
                String setName = cursor.getString(1);
                String score = cursor.getString(2);
                String time = cursor.getString(3);
                String date = cursor.getString(4);

                // Format the string for the ListView
                // Format: "Math Set | Score: 5/5 | Time: 00:30 | Date..."
                String displayString = "Set: " + setName + "\n" +
                        "Score: " + score + " | Time: " + time + "\n" +
                        "Date: " + date;

                list.add(displayString);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}