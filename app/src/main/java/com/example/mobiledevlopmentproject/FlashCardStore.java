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

