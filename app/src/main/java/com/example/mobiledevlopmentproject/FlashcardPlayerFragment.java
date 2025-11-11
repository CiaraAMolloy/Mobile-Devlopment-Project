package com.example.mobiledevlopmentproject;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FlashcardPlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FlashcardPlayerFragment extends Fragment {

    private static final String ARG_SET_NAME = "setName";
    private String setName;


    public FlashcardPlayerFragment() {
        // Required empty public constructor
    }


    public static FlashcardPlayerFragment newInstance(String setName) {
        FlashcardPlayerFragment fragment = new FlashcardPlayerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SET_NAME, setName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            setName = getArguments().getString(ARG_SET_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_flashcard_player, container, false);
    }
}