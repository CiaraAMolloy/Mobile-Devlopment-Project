package com.example.mobiledevlopmentproject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static int current = 0;
    private static ArrayList<Fragment> fragments;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragments = new ArrayList<>();
        fragments.add(new MakeFlashCards());




        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        DBFlashCardStore n = new DBFlashCardStore(this);
        ArrayList<FlashCard> FlashCards = n.getFlashCards(this);
        //EditText text = findViewById(R.id.term);
        //String value = text.getText().toString();

        FlashCards.add(new FlashCard("", ""));

        n.writeFlashCards(this, FlashCards);

        n.getFlashCards(this);
        String namesStr ="";
        for (FlashCard x : n.getFlashCards(this)) {
            namesStr = namesStr + "\n" + x.getTerm() + " " + x.getDef();

        }




/*
        TextView target = findViewById(R.id.FlashCardText);
        target.setText("Persistence says hello, " + namesStr + "!");*/
    }
    public void rotateFragment(View view){
        if (current == fragments.size()) {
            current = 0;
        }
        Fragment fragment = fragments.get(current++);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_layout, fragment);
        fragmentTransaction.commit();
    }
}