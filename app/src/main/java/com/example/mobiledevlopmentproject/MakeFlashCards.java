package com.example.mobiledevlopmentproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MakeFlashCards#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MakeFlashCards extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button addbutton;

    public MakeFlashCards() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MakeFlashCards.
     */
    // TODO: Rename and change types and number of parameters
    public static MakeFlashCards newInstance(String param1, String param2) {
        MakeFlashCards fragment = new MakeFlashCards();
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
        View makingView = inflater.inflate(R.layout.fragment_make_flash_cards, container, false);
        addbutton =makingView.findViewById(R.id.Add);
        addbutton.setOnClickListener(this);
        Spinner setnames=makingView.findViewById(R.id.setnames);
        //String[] SETS= new String[]{"set1", "set2", "set3"};
        ArrayList<String> Setnames;
        try (DBHandler db = new DBHandler(this.getContext())) {
            Setnames = db.getSetNames();
        }
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>
                        (this.getContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                Setnames);
        setnames.setAdapter(adapter);

        ListView flashcardlist=makingView.findViewById(R.id.listmakeflashcards);
        ArrayList<String> testlist=new ArrayList<>();

        ArrayAdapter<String> adapter2 =
                new ArrayAdapter<>
                        (this.getContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                testlist);
        flashcardlist.setAdapter(adapter2);



        return makingView;

    }



    @Override
    public void onClick(View v) {

    if(this.getContext()!=null) {
        if (v.getId() == R.id.Add) {
            assert getView() != null;

                DBFlashCardStore n = new DBFlashCardStore(this.getContext());

                ArrayList<FlashCard> FlashCards = n.getFlashCards(this.getContext());
                FlashCards.clear();

                Spinner setnames = getView().findViewById(R.id.setnames);
            if (setnames.getSelectedItem() != null) {

                String setname = setnames.getSelectedItem().toString();

                EditText textt = getView().findViewById(R.id.term);
                String valuet = textt.getText().toString();
                EditText textd = getView().findViewById(R.id.definition);
                String valued = textd.getText().toString();

                FlashCards.add(new FlashCard(setname, valuet, valued));

                n.writeFlashCards(this.getContext(), FlashCards);

                n.getFlashCards(this.getContext());


                String namesStr = "";
                for (FlashCard x : n.getFlashCards(this.getContext())) {
                    namesStr = namesStr + "\n" + x.getTerm() + " " + x.getDef();

                }//to do clear strings when done


                ArrayList<String> set;
                try (DBHandler db = new DBHandler(this.getContext())) {
                    set = db.getSpecificSet(setname);
                }

                ListView flashcardlist = getView().findViewById(R.id.listmakeflashcards);
                ArrayAdapter<String> adapter =
                        new ArrayAdapter<>
                                (this.getContext(),
                                        android.R.layout.simple_spinner_dropdown_item,
                                        set);
                flashcardlist.setAdapter(adapter);

            }
            else{
                
                TextView error = getView().findViewById(R.id.ERRORMESSAGE);
                error.setText("It seems like you didn't make a set yet\nplease make a set or choose a set to put this flashcard in!");
               
            }
        }
    }
    }
    }