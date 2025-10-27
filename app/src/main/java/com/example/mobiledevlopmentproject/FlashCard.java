package com.example.mobiledevlopmentproject;

public class FlashCard {

    String setName;
    String term;
    String definition;

    public FlashCard(String s ,String t, String d) {
        setName=s;
        term=t;
        definition=d;

    }

    public String getTerm() {
        return term;
    }

    public String getDef() {
        return definition;
    }
    public String getSetName() {
        return setName;
    }
}
