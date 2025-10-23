package com.example.mobiledevlopmentproject;


import android.content.Context;

import java.util.ArrayList;

public class DBFlashCardStore extends FlashCardStore {
    private static DBHandler flashcards;
    public DBFlashCardStore(Context context,String f, String l) {
        super();
        if (flashcards == null) {
            flashcards = new DBHandler(context);
        }
    }

    @Override
    public void writeNames(Context context, ArrayList<FlashCard> values) {
        if (!values.isEmpty()) {
            for (FlashCard n : values) {
                flashcards.add(n);
            }
        }
    }

    @Override
    public ArrayList<FlashCard> getFlashCards(Context context) {
        //super();
        return flashcards.getFlashCards();
    }

}
