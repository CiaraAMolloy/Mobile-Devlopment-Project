package com.example.mobiledevlopmentproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.util.Log;
import android.content.ContentValues;
import java.util.ArrayList;

/**
 * DBSubject
 * A small data access class for Subjects and Sets in the app.
 * It uses SQLite through DBHandler. It returns simple result codes
 * for common cases (OK, duplicate, not found, etc.).
 *
 * This class offers:
 * - List all subjects.
 * - List all sets by subject.
 * - Add a set.
 * - Delete a set (with different modes).
 * - Rename a set.
 *
 * Notes:
 * - The code uses case-insensitive comparisons with COLLATE NOCASE.
 * - "UNTAG" is a special set name used when moving cards out of a deleted set.
 */
public class DBSubject {

    // Return codes for many methods. Keep UI logic simple.
    public static final int RC_OK = 0,            // Success
            RC_DUPLICATE = 1,                    // Same name already exists
            RC_HAS_CARD = 2,                     // Set is not empty
            RC_NOT_FOUND = 3,                    // Target does not exist
            RC_DB_ERROR = 4;                     // Any database error

    // Single DBHandler instance (shared). Helps avoid many open connections.
    private static DBHandler db;

    private static final String TAG = "DBSubject";

    /**
     * Build this helper. We keep a single DBHandler (singleton-like) to
     * reduce overhead. The app context is used to avoid leaks.
     */
    public DBSubject(Context con) {
        if (db == null) {
            db = new DBHandler(con.getApplicationContext());
        }
    }

    /**
     * Delete behavior when removing a set.
     * EMPTY_ONLY   -> delete only if the set has no flashcards.
     * CASCADE      -> delete the set and all its flashcards.
     * MOVE_TO_UNTAG-> move all flashcards to set "UNTAG" then delete the set.
     */
    public enum DeleteMode {
        EMPTY_ONLY,
        CASCADE,
        MOVE_TO_UNTAG
    }

    /**
     * Get a readable database. Use for SELECT queries.
     */
    private SQLiteDatabase getDbRead() {
        return db.getReadableDatabase();
    }

    /**
     * Get a writable database. Use for INSERT/UPDATE/DELETE.
     * (Method name kept as in original code.)
     */
    private SQLiteDatabase setDbWrite() {
        return db.getWritableDatabase();
    }

    /**
     * List all distinct subjects in alphabetical order (case-insensitive).
     * @return a list of subject names.
     */
    public ArrayList<String> listSubjects() {
        ArrayList<String> result = new ArrayList<>();
        SQLiteDatabase sqldb = this.getDbRead();

        final String sqlSubjectResult = "SELECT DISTINCT Subject " +
                "From setsandsubjects " +
                "ORDER BY Subject COLLATE NOCASE";

        Cursor cursor = sqldb.rawQuery(sqlSubjectResult, null);
        if (cursor.moveToFirst()) {
            do {
                String subject = cursor.getString(0);
                result.add(subject);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    /**
     * List all distinct set names under a given subject. Sorted A-Z.
     * @param subject the subject name to filter by.
     * @return a list of set names for that subject.
     */
    public ArrayList<String> listBySubjects(String subject) {
        ArrayList<String> result = new ArrayList<>();
        SQLiteDatabase sqldb = this.getDbRead();

        final String sqlSetResult = "SELECT DISTINCT setname " +
                "From setsandsubjects " +
                "WHERE Subject = ? " +
                "ORDER BY setname COLLATE NOCASE";

        Cursor cursor = sqldb.rawQuery(sqlSetResult, new String[]{subject});
        if (cursor.moveToFirst()) {
            do {
                String setname = cursor.getString(0);
                result.add(setname);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    /**
     * Check if a set name already exists (case-insensitive).
     * Returns true if the name exists, false otherwise.
     * If the input is null/empty, returns false and logs a warning.
     */
    private boolean duplicateCheck(String setName) {
        if (setName == null) {
            Log.w(TAG, "duplicateCheck(): name is null");
            return false; // Treat null as non-duplicate to avoid crash. Caller should validate.
        }
        String t = setName.trim();
        if (t.isEmpty()) {
            Log.w(TAG, "duplicateCheck(): name is empty after trim");
            return false;
        }

        SQLiteDatabase db = getDbRead();
        final String sqlDuplicateCheck = "SELECT 1 " +
                "FROM setsandsubjects " +
                "WHERE setname = ? " +
                "COLLATE NOCASE LIMIT 1";

        Cursor cursor = db.rawQuery(sqlDuplicateCheck, new String[]{t});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    /**
     * Add a new set under a subject.
     * Validation steps:
     * - subject and setName cannot be null/blank.
     * - set name cannot duplicate an existing one.
     * @return RC_OK, RC_DUPLICATE, or RC_DB_ERROR.
     */
    public int addSet(String subject, String setName) {
        // Basic null checks
        if (setName == null) {
            return RC_DB_ERROR;
        }
        if (subject == null) {
            return RC_DB_ERROR;
        }

        // Trim values and check empty
        String t = setName.trim();
        String s = subject.trim();

        if (t.isEmpty()) {
            return RC_DB_ERROR;
        }
        if (s.isEmpty()) {
            return RC_DB_ERROR;
        }

        // Prevent duplicates (case-insensitive)
        if (duplicateCheck(t)) {
            return RC_DUPLICATE;
        }

        // Insert new row
        SQLiteDatabase sqldb = setDbWrite();
        ContentValues cvs = new ContentValues();
        cvs.put("setname", t);
        cvs.put("Subject", s);

        long rowId = sqldb.insert("setsandsubjects", null, cvs);
        if (rowId == -1) {
            return RC_DB_ERROR; // Insert failed
        } else {
            Log.d(TAG, "insert succesful, rowId = " + rowId);
            return RC_OK;
        }
    }

    /**
     * Delete a set by name using one of three modes.
     * @param setName the set to delete.
     * @param mode behavior when the set has cards.
     * @return a result code (OK, NOT_FOUND, HAS_CARD, DB_ERROR).
     */
    public int deleteSet(String setName, DeleteMode mode) {
        // Validate input
        if (setName == null) {
            return RC_DB_ERROR;
        }
        String t = setName.trim();
        if (t.isEmpty()) {
            return RC_DB_ERROR;
        }

        SQLiteDatabase sqldb = setDbWrite();

        switch (mode) {

            case EMPTY_ONLY: {
                // Only delete if there are no flashcards in this set
                int countCard = 0;
                Cursor c = getDbRead().rawQuery("SELECT COUNT(*) " +
                                "FROM flashcards " +
                                "WHERE setname=? " +
                                "COLLATE NOCASE",
                        new String[]{t}
                );
                if (c.moveToFirst()) {
                    countCard = c.getInt(0);
                }
                c.close();

                if (countCard > 0) {
                    return RC_HAS_CARD; // Not empty, stop here
                }

                try {
                    int rows = sqldb.delete(
                            "setsandsubjects",
                            "setname=? COLLATE NOCASE",
                            new String[]{t}
                    );
                    return (rows == 0) ? RC_NOT_FOUND : RC_OK;
                } catch (Exception e) {
                    Log.w("DBSubject", "EMPTY_ONLY delete DB error", e);
                    return RC_DB_ERROR;
                }
            }

            case CASCADE: {
                // Delete the set and all its flashcards as one transaction
                sqldb.beginTransaction();
                try {
                    // Remove all flashcards first
                    sqldb.delete("flashcards", "setname=? COLLATE NOCASE", new String[]{t});

                    // Then remove the set itself
                    int rows = sqldb.delete("setsandsubjects", "setname=? COLLATE NOCASE", new String[]{t});
                    if (rows == 0) {
                        sqldb.endTransaction();
                        return RC_NOT_FOUND;
                    }

                    // Commit
                    sqldb.setTransactionSuccessful();
                    sqldb.endTransaction();
                    return RC_OK;

                } catch (Exception e) {
                    Log.w("DBSubject", "CASCADE delete DB error", e);
                    try {
                        sqldb.endTransaction();
                        return RC_DB_ERROR;
                    } catch (Exception exception) {
                        return RC_DB_ERROR;
                    }
                }
            }

            case MOVE_TO_UNTAG: {
                // Ensure the special set "UNTAG" exists. Use subject "Unassigned".
                boolean unTagFolder;
                Cursor cursor = getDbRead().rawQuery(
                        "SELECT 1 FROM setsandsubjects WHERE setname=? COLLATE NOCASE LIMIT 1",
                        new String[]{"UNTAG"}
                );
                unTagFolder = cursor.moveToFirst();
                cursor.close();

                if (!unTagFolder) {
                    int rc = addSet("Unassigned", "UNTAG");
                    if (rc != RC_OK && rc != RC_DUPLICATE) {
                        return RC_DB_ERROR; // Could not make UNTAG
                    }
                }

                // Do not allow deleting or moving UNTAG itself
                if (t.equalsIgnoreCase("UNTAG")) {
                    return RC_DB_ERROR;
                }

                // Move all cards to UNTAG, then delete the set
                sqldb.beginTransaction();
                try {
                    ContentValues cvs = new ContentValues();
                    cvs.put("setname", "UNTAG");
                    sqldb.update("flashcards", cvs, "setname=? COLLATE NOCASE", new String[]{t});

                    int rows = sqldb.delete("setsandsubjects", "setname=? COLLATE NOCASE", new String[]{t});
                    if (rows == 0) {
                        sqldb.endTransaction();
                        return RC_NOT_FOUND;
                    }

                    sqldb.setTransactionSuccessful();
                    sqldb.endTransaction();
                    return RC_OK;
                } catch (Exception e) {
                    Log.w("DBSubject", "MOVE_TO_UNTAG DB error", e);
                    try {
                        sqldb.endTransaction();
                    } catch (Exception exception) {
                        // ignore
                    }
                    return RC_DB_ERROR;
                }
            }
        }
        // Should not reach here, but keep a safe default.
        return RC_DB_ERROR;
    }

    /**
     * Rename a set. Also updates all flashcards that belong to this set.
     * Rules:
     * - Names cannot be null/blank.
     * - Same name (ignoring case) -> no change, return OK.
     * - "UNTAG" cannot be old or new name.
     * - New name must not already exist.
     *
     * @param oldName current set name
     * @param newName desired set name
     * @return RC_OK, RC_DUPLICATE, RC_NOT_FOUND, or RC_DB_ERROR
     */
    public int renameSet(String oldName, String newName) {
        // Validate input
        if (oldName == null || newName == null) {
            return RC_DB_ERROR;
        }

        String o = oldName.trim();
        String n = newName.trim();

        if (o.isEmpty() || n.isEmpty()) {
            return RC_DB_ERROR;
        }

        // If only the case is different, treat as OK
        if (o.equalsIgnoreCase(n)) {
            return RC_OK;
        }

        // Do not allow rename to/from UNTAG
        if (o.equalsIgnoreCase("UNTAG") || n.equalsIgnoreCase("UNTAG")) {
            return RC_DB_ERROR;
        }

        // Check if target name already exists
        try (Cursor c = getDbRead().rawQuery(
                "SELECT 1 FROM setsandsubjects WHERE setname=? COLLATE NOCASE LIMIT 1",
                new String[]{n})) {
            if (c.moveToFirst()) return RC_DUPLICATE;
        } catch (Exception e) {
            Log.w(TAG, "renameSet() duplicate check error", e);
            return RC_DB_ERROR;
        }

        // Update both flashcards and the set name in a single transaction
        SQLiteDatabase dbw = setDbWrite();
        dbw.beginTransaction();
        try {
            // Update flashcards first
            ContentValues cv = new ContentValues();
            cv.put("setname", n);
            dbw.update(
                    "flashcards",
                    cv,
                    "setname=? COLLATE NOCASE",
                    new String[]{o}
            );

            // Then update the set name in setsandsubjects
            int rows = dbw.update(
                    "setsandsubjects",
                    cv,
                    "setname=? COLLATE NOCASE",
                    new String[]{o}
            );

            if (rows == 0) {
                return RC_NOT_FOUND; // No set matched the old name
            }

            dbw.setTransactionSuccessful();
            return RC_OK;

        } catch (Exception e) {
            Log.w(TAG, "renameSet() DB error", e);
            return RC_DB_ERROR;
        } finally {
            // Always end the transaction
            dbw.endTransaction();
        }
    }
}
