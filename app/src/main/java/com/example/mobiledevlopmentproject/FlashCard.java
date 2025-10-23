package com.example.mobiledevlopmentproject;

public class FlashCard {
    String term;
    String definition;

    public FlashCard(String t, String d) {
        term=t;
        definition=d;

    }

    public String getTerm() {
        return term;
    }

    public String getDef() {
        return definition;
    }
}
