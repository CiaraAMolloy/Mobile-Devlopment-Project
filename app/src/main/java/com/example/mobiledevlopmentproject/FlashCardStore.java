package com.example.mobiledevlopmentproject;


import java.util.ArrayList;
import android.content.Context;
public abstract class FlashCardStore {
    private String term;
    private String definition;

    private String setName;


    public FlashCardStore() {


    }
    public FlashCardStore(String s,String f, String l) {
        this.setName=s;
        this.term=f;
        this.definition=l;

    }

    public void setFlashcard(String s, String f, String l) {
        setName=s;
        term=f;
        definition=l;
    }
    public String getSetName() {
        return setName;
    }
    public void setSetName(String s) {
        setName=s;
    }

    public String getTerm() {
        return term;
    }
    public void setTerm(String t) {
        term=t;
    }
    public String getDef()  {
        return definition;
    }
    public void setDefinition(String d) {
        definition=d;
    }

    public abstract void writeFlashCards(Context context,
                                    ArrayList<FlashCard> values) ;

    public  abstract ArrayList<FlashCard> getFlashCards(Context context);
}

