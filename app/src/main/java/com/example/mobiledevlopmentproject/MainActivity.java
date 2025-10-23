package com.example.mobiledevlopmentproject;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragments = new ArrayList<Fragment>();
        fragments.add(new MakeFlashCards());




        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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