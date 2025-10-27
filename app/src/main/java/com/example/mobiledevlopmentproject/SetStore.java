package com.example.mobiledevlopmentproject;

import android.content.Context;

import java.util.ArrayList;

public abstract class SetStore {
    String setName;
    String subjectname;

    public SetStore() {

    }

    public SetStore(String s ,String sub) {
        setName=s;

        subjectname=sub;

    }


    public String getSetName() {
        return setName;
    }
    public String getSubject() {
        return subjectname;
    }

    public abstract void writeSets(Context context,
                                         ArrayList<Set> values) ;

    public  abstract ArrayList<Set> getSets(Context context);
}
