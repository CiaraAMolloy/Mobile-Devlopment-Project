package com.example.mobiledevlopmentproject;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper{

    private static final String DB_NAME = "FlashCarddb";
    // This may be used for migration in the future.
    private static final int DB_VERSION = 1;

    //one table
    private static final String TABLE_NAME = "flashcards";
    private static final String ID_COL = "id";
    private static final String FIRST_COL = "setname";
    private static final String SECOND_COL = "term";
    private static final String LAST_COL = "definition";
//login table

    private static final String LOGIN_TABLE = "logindata";
    private static final String LOGINID_COL = "loginID";
    private static final String USER_COL = "username";
    private static final String EMAIL_COL = "email";
    private static final String PASS_COL = "password";
// table
    private static final String SUBJECT_SET = "setsandsubjects";
    private static final String ID_COL_2 = "id";
    private static final String  SUBJECT_SET_FIRST_COL = "setname";

    private static final String  SUBJECT_SET_LAST_COL = "Subject";

    public DBHandler(@Nullable Context context) {
        super(context, DB_NAME, null,  DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query =
                "CREATE TABLE " + TABLE_NAME + " ("
                        + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + FIRST_COL + " TEXT,"
                        + SECOND_COL+ " TEXT,"
                        + LAST_COL + " TEXT)";
        db.execSQL(query);

        String query2 =
                "CREATE TABLE " + SUBJECT_SET+ " ("
                        + ID_COL_2 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + SUBJECT_SET_FIRST_COL + " TEXT,"
                        + SUBJECT_SET_LAST_COL + " TEXT)";
        db.execSQL(query2);
        /*    private static final String LOGIN_TABLE = "logindata";
    private static final String LOGINID_COL = "loginID";
    private static final String USER_COL = "username";
    private static final String EMAIL_COL = "email";
    private static final String PASS_COL = "password";*/
        String query3 =
                "CREATE TABLE " + LOGIN_TABLE + " ("
                        + LOGINID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + USER_COL + " TEXT,"
                        + EMAIL_COL+ " TEXT,"
                        + PASS_COL + " TEXT)";
        db.execSQL(query3);
        //login

    }
    //add methods
    public void add(FlashCard n) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put( FIRST_COL, n.getSetName());
        values.put(SECOND_COL, n.getTerm());
        values.put(LAST_COL, n.getDef());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }
    public void add(Set n) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put( SUBJECT_SET_FIRST_COL, n.getSetName());
        values.put(SUBJECT_SET_LAST_COL , n.getSubject());
        db.insert(SUBJECT_SET, null, values);
        db.close();
    }

    public void add(Login x){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put( USER_COL, x.getUser());
        values.put(EMAIL_COL, x.getEmail());
        //INSERT ANOTHER COLUMN
        values.put(PASS_COL, x.getPass());
        db.insert(LOGIN_TABLE , null, values);
        db.close();
    }

    // Get the whole list of names.
    public ArrayList<FlashCard> getFlashCards() {
        SQLiteDatabase db = this.getReadableDatabase();
// This will be the result.
        ArrayList<FlashCard> flashcards = new ArrayList<>();
        Cursor cursor =
                db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                flashcards.add(new FlashCard(cursor.getString(1), cursor.getString(2), cursor.getString(3)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return flashcards;
    }
    public ArrayList<Set> getSets() {
        SQLiteDatabase db = this.getReadableDatabase();
// This will be the result.
        ArrayList<Set> sets= new ArrayList<>();
        Cursor cursor =
                db.rawQuery("SELECT * FROM " + SUBJECT_SET, null);
        if (cursor.moveToFirst()) {
            do {
                sets.add(new Set(cursor.getString(1), cursor.getString(2)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return sets;
    }

    public ArrayList<String> getSetNames() {
        SQLiteDatabase db = this.getReadableDatabase();
// This will be the result.
        ArrayList<String> sets= new ArrayList<>();
        Cursor cursor =
                db.rawQuery("SELECT distinct "+SUBJECT_SET_FIRST_COL+" FROM " + SUBJECT_SET, null);
        if (cursor.moveToFirst()) {
            do {
                sets.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return sets;
    }
    public ArrayList<String> getSpecificSetID(String set) {
        SQLiteDatabase db = this.getReadableDatabase();
// This will be the result.
        ArrayList<String> sets= new ArrayList<>();
        Cursor cursor =
                db.rawQuery("SELECT  "+ID_COL+" " +
                        "FROM " + TABLE_NAME+
                        " WHERE "+SUBJECT_SET_FIRST_COL+" = \""+set+"\"",null);
        if (cursor.moveToFirst()) {
            do {
               // sets.add(new Set(cursor.getString(0),cursor.getString(1)));
                String temp=cursor.getString(0);
                sets.add(temp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return sets;
    }
    public void delSpecificSetID(String ID) {
        SQLiteDatabase db = this.getReadableDatabase();
// This will be the result.
        ArrayList<String> sets= new ArrayList<>();
       Cursor cursor =
                db.rawQuery("DELETE FROM "+TABLE_NAME+" " +

                        " WHERE "+ID_COL+" = \""+ID+"\"",null);
        if (cursor.moveToFirst()) {
            do {
                // sets.add(new Set(cursor.getString(0),cursor.getString(1)));
                String temp=cursor.getString(0);
                sets.add(temp);
            } while (cursor.moveToNext());
        }
        cursor.close();

    }
    public ArrayList<String> getSpecificSet(String set) {
        SQLiteDatabase db = this.getReadableDatabase();
// This will be the result.
        ArrayList<String> sets= new ArrayList<>();
        Cursor cursor =
                db.rawQuery("SELECT  "+SECOND_COL+","+LAST_COL+" " +
                        "FROM " + TABLE_NAME+
                        " WHERE "+SUBJECT_SET_FIRST_COL+" = \""+set+"\"",null);
        if (cursor.moveToFirst()) {
            do {
                // sets.add(new Set(cursor.getString(0),cursor.getString(1)));
                String temp=cursor.getString(0)+"="+cursor.getString(1);
                sets.add(temp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return sets;
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
