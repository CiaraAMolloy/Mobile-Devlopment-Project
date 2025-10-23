package com.example.mobiledevlopmentproject;


import java.util.ArrayList;
import android.content.Context;
public abstract class FlashCardStore {
    private String term;
    private String definition;


    public FlashCardStore() {


    }
    public FlashCardStore(String f, String l) {
        this.term=f;
        this.definition=l;

    }

    public void setName(String f, String l) {
        term=f;
        definition=l;
    }
    public String getFirst() {
        return term;
    }
    public void setFirst(String f) {
        term=f;
    }
    public String getLast() {
        return definition;
    }
    public void setLast(String l) {
        definition=l;
    }

    public abstract void writeNames(Context context,
                                    ArrayList<FlashCard> values) ;

    public  abstract ArrayList<FlashCard> getFlashCards(Context context);
}

