package com.example.mobiledevlopmentproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_login_page#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_login_page extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_login_page() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_login_page.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_login_page newInstance(String param1, String param2) {
        fragment_login_page fragment = new fragment_login_page();
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
        View view = inflater.inflate(R.layout.fragment_login_page, container, false);

        Button loginButton = view.findViewById(R.id.login_button); // adjust ID to match your layout

        loginButton.setOnClickListener(v -> {
            // TODO: Add your login validation here
            /*
            Spinner Subject=getView().findViewById(R.id.Subject);
            String Sub= Subject.getSelectedItem().toString();
            */

            EditText user=getView().findViewById(R.id.username_input);
            if(user.getText()!=null) {
                String n = user.getText().toString();
                TextView name = getActivity().findViewById(R.id.name);
                name.setText(n);
            }
            else {
                TextView name = getActivity().findViewById(R.id.name);
                name.setText("Anonymous");
            }
            /*
              else{
                TextView error = getView().findViewById(R.id.MESSAGE);
                error.setText("No set created to put flashcard in/nplease make a set or choose a set to put this flashcard in");

            }*/
            // When successful:
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showMainApp();
            }
        });

        return view;
    }
}