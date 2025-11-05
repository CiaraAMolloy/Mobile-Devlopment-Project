package com.example.mobiledevlopmentproject;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.util.Log;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Locale;

public class DBSubject {

   public static final int RC_OK = 0, RC_DUPLICATE = 1, RC_HAS_CARD = 2, RC_NOT_FOUND = 3, RC_DB_ERROR = 4;

    private static DBHandler db;

    private static final String TAG = "DBSubject";

    public DBSubject(Context con){
        if(db == null){
            db = new DBHandler(con.getApplicationContext());
        }
    }

    private SQLiteDatabase getDbRead(){
        return db.getReadableDatabase();
    }

    private SQLiteDatabase setDbWrite(){
        return db.getWritableDatabase();
    }

    public ArrayList<String> listSubjects(){
        ArrayList<String> result = new ArrayList<>();
        SQLiteDatabase sqldb = this.getDbRead();

        final String sqlSubjectResult = "SELECT DISTINCT Subject " +
                                        "From setsandsubjects " +
                                        "ORDER BY Subject COLLATE NOCASE";
        Cursor cursor = sqldb.rawQuery(sqlSubjectResult, null);
        if(cursor.moveToFirst()){
            do{
                String subject = cursor.getString(0);
                result.add(subject);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public ArrayList<String> listBySubjects(String subject){
        ArrayList<String> result = new ArrayList<>();
        SQLiteDatabase sqldb = this.getDbRead();
        final String sqlSetResult = "SELECT DISTINCT setname " +
                                    "From setsandsubjects " +
                                    "WHERE Subject = ? " +
                                    "ORDER BY setname COLLATE NOCASE";
        Cursor cursor = sqldb.rawQuery(sqlSetResult, new String[]{subject});
        if(cursor.moveToFirst()){
            do{
                String setname = cursor.getString(0);
                result.add(setname);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return result;

    }

    private boolean duplicateCheck(String setName){
        if(setName == null){
            Log.w(TAG,"duplicateCheck(): name is null");
            return false;
        }
        String t = setName.trim();
        if(t.isEmpty()){
            Log.w(TAG,"duplicateCheck(): name is empty after trim");
            return false;
        }

        SQLiteDatabase db = getDbRead();
        final String sqlDuplicateCheck =    "SELECT 1 " +
                                            "FROM setsandsubjects " +
                                            "WHERE setname = ? " +
                                            "COLLATE NOCASE LIMIT 1";
        Cursor cursor = db.rawQuery(sqlDuplicateCheck, new String[]{t});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public int addSet(String subject, String setName){

        if(setName == null){
            return RC_DB_ERROR;
        }

        if(subject == null ){
            return RC_DB_ERROR;
        }

        String t = setName.trim();
        String s = subject.trim();

        if(t.isEmpty()){
            return RC_DB_ERROR;
        }

        if (s.isEmpty()){
            return RC_DB_ERROR;
        }

        if (duplicateCheck(t)){
            return RC_DUPLICATE;
        }


        SQLiteDatabase sqldb = setDbWrite();
        ContentValues cvs = new ContentValues();
        cvs.put("setname", t);
        cvs.put("Subject", s);

        long rowId = sqldb.insert("setsandsubjects", null, cvs);
        if(rowId == -1){
            return RC_DB_ERROR;
        }else {
            Log.d(TAG,"insert succesful, rowId = " + rowId);
            return RC_OK;
        }
    }
}
