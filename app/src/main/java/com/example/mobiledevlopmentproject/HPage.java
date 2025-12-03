package com.example.mobiledevlopmentproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HPage extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static int current = 0;
    private static ArrayList<Fragment> fragments;

    Button addbutton;

    public HPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HPage.
     */
    // TODO: Rename and change types and number of parameters
    public static HPage newInstance(String param1, String param2) {
        HPage fragment = new HPage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragments = new ArrayList<>();
        fragments.add(new MakeFlashCards());
        fragments.add(new MakeSets());
        fragments.add(new DeleteFlashCard());
        fragments.add(new CalendarPage());
        fragments.add(new FlashcardPlayerFragment());
        fragments.add(new LibraryFragment());

        View makingView = inflater.inflate(R.layout.fragment_hpage, container, false);
        // Inflate the layout for this fragment
        addbutton = makingView.findViewById(R.id.Add);
        addbutton.setOnClickListener(this);


        return inflater.inflate(R.layout.fragment_hpage, container, false);
    }

    @Override
    public void onClick(View v){

        if(this.getContext()!=null){
            if(v.getId() == R.id.Add){
                assert getView() != null;
                if (current == fragments.size()) {
                    current = 0;
                }
                Fragment fragment = fragments.get(current++);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.fragment_layout, fragment);
                fragmentTransaction.commit();
            }
            }
        }

    }

