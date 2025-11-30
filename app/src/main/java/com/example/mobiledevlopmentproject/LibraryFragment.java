package com.example.mobiledevlopmentproject;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;

public class LibraryFragment extends Fragment {

    public LibraryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind Buttons
        Button btnStudy = view.findViewById(R.id.btn_hub_study);
        Button btnGame = view.findViewById(R.id.btn_hub_game);

        // Set Click Listeners
        btnStudy.setOnClickListener(v -> showSetSelectionForStudy());
        btnGame.setOnClickListener(v -> showSetSelectionForGame());
    }

    // ==========================================
    // LOGIC 1: STUDY (RECITE WORDS)
    // ==========================================

    private void showSetSelectionForStudy() {
        if (getContext() == null) return;

        // 1. Get sets from DB
        DBSetStore store = new DBSetStore(getContext());
        ArrayList<Set> sets = store.getSets(getContext());

        if (checkSetsEmpty(sets)) return;

        // 2. Prepare list for Dialog
        String[] setNames = getSetNames(sets);

        // 3. Show Dialog -> Go directly to Player
        new AlertDialog.Builder(getContext())
                .setTitle("Select a Set to Study")
                .setItems(setNames, (dialog, which) -> {
                    String selectedSet = setNames[which];
                    // Go to FlashcardPlayerFragment
                    openPlayerFragment(selectedSet);
                })
                .show();
    }

    private void openPlayerFragment(String setName) {
        FlashcardPlayerFragment playerFragment = FlashcardPlayerFragment.newInstance(setName);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_layout, playerFragment)
                .addToBackStack(null)
                .commit();
    }


    // ==========================================
    // LOGIC 2: PLAY GAME
    // ==========================================

    private void showSetSelectionForGame() {
        if (getContext() == null) return;

        // 1. Get sets from DB
        DBSetStore store = new DBSetStore(getContext());
        ArrayList<Set> sets = store.getSets(getContext());

        if (checkSetsEmpty(sets)) return;

        String[] setNames = getSetNames(sets);

        // 2. Show Dialog -> Go to Game Mode Selection
        new AlertDialog.Builder(getContext())
                .setTitle("Select a Set to Play")
                .setItems(setNames, (dialog, which) -> {
                    String selectedSet = setNames[which];
                    // Next step: Choose Game Mode
                    showGameModeDialog(selectedSet);
                })
                .show();
    }

    private void showGameModeDialog(String setName) {
        String[] options = {"Finish All Cards", "Timed Game"};
        new AlertDialog.Builder(getContext())
                .setTitle("Choose Game Mode")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        startGame(setName, FlashcardGameFragment.GameMode.FINISH_ALL, 0);
                    } else {
                        showTimeLimitDialog(setName);
                    }
                })
                .show();
    }

    private void showTimeLimitDialog(String setName) {
        String[] times = {"15 Seconds", "30 Seconds", "45 Seconds"};
        int[] secondsValues = {15, 30, 45};
        new AlertDialog.Builder(getContext())
                .setTitle("Select Time Limit")
                .setItems(times, (dialog, which) -> {
                    startGame(setName, FlashcardGameFragment.GameMode.FIXED_TIME, secondsValues[which]);
                })
                .show();
    }

    private void startGame(String setName, FlashcardGameFragment.GameMode mode, int limitSeconds) {
        FlashcardGameFragment gameFragment = FlashcardGameFragment.newInstance(setName, mode, limitSeconds);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_layout, gameFragment)
                .addToBackStack(null)
                .commit();
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================

    private boolean checkSetsEmpty(ArrayList<Set> sets) {
        if (sets == null || sets.isEmpty()) {
            Toast.makeText(getContext(), "No sets found! Go create one first.", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private String[] getSetNames(ArrayList<Set> sets) {
        String[] names = new String[sets.size()];
        for (int i = 0; i < sets.size(); i++) {
            names[i] = sets.get(i).getSetName();
        }
        return names;
    }
}