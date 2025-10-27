package com.example.mobiledevlopmentproject;

import android.content.Context;

import java.util.ArrayList;

public class DBSetStore extends SetStore {
    private static DBHandler sets;
    public DBSetStore(Context context) {
        super();
        if (sets == null) {
            sets = new DBHandler(context);

        }
    }

    @Override
    public void writeSets(Context context, ArrayList<Set> values) {
        if (!values.isEmpty()) {
            for (Set n : values) {
                sets.add(n);
            }
        }
    }

    @Override
    public ArrayList<Set> getSets(Context context) {
        //super();
        return sets.getSets();
    }

}
