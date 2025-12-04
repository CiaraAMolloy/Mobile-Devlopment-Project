package com.example.mobiledevlopmentproject;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeleteFlashCard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeleteFlashCard extends Fragment implements View.OnClickListener {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Button selbutton;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DeleteFlashCard() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeleteFlashCard.
     */
    // TODO: Rename and change types and number of parameters
    public static DeleteFlashCard newInstance(String param1, String param2) {
        DeleteFlashCard fragment = new DeleteFlashCard();
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
        // Inflate the layout for this fragment

        View create= inflater.inflate(R.layout.fragment_delete_flash_card, container, false);
        ListView flashcardlist=create.findViewById(R.id.list);
        ArrayList<String> testlist=new ArrayList<>();
if(!(this.getContext() ==null)) {
    ArrayAdapter<String> adapter =
            new ArrayAdapter<>
                    (this.getContext(),
                            android.R.layout.simple_list_item_multiple_choice,
                            testlist);
    flashcardlist.setAdapter(adapter);
    flashcardlist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


    Spinner setnames = create.findViewById(R.id.setnamesDEL);
    //String[] SETS= new String[]{"set1", "set2", "set3"};
    ArrayList<String> Setnames;
    try (DBHandler db = new DBHandler(this.getContext())) {
        Setnames = db.getSetNames();
    }
    ArrayAdapter<String> adapter2 =
            new ArrayAdapter<>
                    (this.getContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            Setnames);


    setnames.setAdapter(adapter2);


    selbutton = (Button) create.findViewById(R.id.select);
    selbutton.setOnClickListener(this);
    Button delbutton = (Button) create.findViewById(R.id.DELETEBUTTON);
    delbutton.setOnClickListener(this);

}
    return create;
    }




    @Override
    public void onClick(View v) {
        if(this.getContext()!=null){
        if(v.getId() == R.id.select){


            assert getView() != null;
            Spinner getset=getView().findViewById(R.id.setnamesDEL);
            String setname= getset.getSelectedItem().toString();

            ArrayList<String> set;
            try (DBHandler db = new DBHandler(this.getContext())) {
                set = db.getSpecificSet(setname);
            }

            ListView flashcardlist=getView().findViewById(R.id.list);
            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>
                            (this.getContext(),
                                    android.R.layout.simple_list_item_multiple_choice,
                                    set );
            flashcardlist.setAdapter(adapter);


        }
        if(v.getId() == R.id.DELETEBUTTON ){
            assert getView() != null;
            Spinner getset=getView().findViewById(R.id.setnamesDEL);
            String setname= getset.getSelectedItem().toString();

            ListView flashcardlist;
            ArrayList<String> set2;
            try (DBHandler db = new DBHandler(this.getContext())) {
                ArrayList<String> set = db.getSpecificSetID(setname);
                // FlashCardText
               // TextView debug = getView().findViewById(R.id.FlashCardText);

                flashcardlist = getView().findViewById(R.id.list);

                //Object selectedItem = flashcardlist.getSelectedItem();
                SparseBooleanArray arr = flashcardlist.getCheckedItemPositions();
                String x = "";
                for (int i = 0; i < flashcardlist.getAdapter().getCount(); i++) {
                    if (arr.get(i)) {
                        // x = x + i;
                        x += set.get(i);
                        db.delSpecificSetID(set.get(i));
                        //debug.setText(x);


                    }
                }
                set2 = db.getSpecificSet(setname);
            }

            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>
                            (this.getContext(),
                                    android.R.layout.simple_list_item_multiple_choice,
                                    set2 );
            flashcardlist.setAdapter(adapter);
            //debug.setText(item);


        }

    }}
}