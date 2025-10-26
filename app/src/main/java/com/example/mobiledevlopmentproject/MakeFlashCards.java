package com.example.mobiledevlopmentproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
       /* DBFlashCardStore n=new DBFlashCardStore(this.getContext());
        ArrayList<FlashCard> FlashCards =n.getFlashCards(this.getContext());

        FlashCards.add(new FlashCard("",""));

        n.writeFlashCards(this.getContext(),FlashCards);

        n.getFlashCards(this.getContext());
        String namesStr = new String();
        for (FlashCard x : n.getFlashCards(this.getContext())) {
            namesStr = namesStr + "\n" + x.getTerm() + " " + x.getDef();
        }*/
        // Inflate the layout for this fragment
        View makingView = inflater.inflate(R.layout.fragment_make_flash_cards, container, false);
        addbutton = (Button) makingView.findViewById(R.id.Add);
        addbutton.setOnClickListener(this);
        return makingView;

    }


    public void AddToDataBase(){
       /* View view=this.getView();

            // load First Fragment
            DBFlashCardStore n = new DBFlashCardStore(this.getContext());
            ArrayList<FlashCard> FlashCards = n.getFlashCards(this.getContext());
            EditText text = view.findViewById(R.id.term);
            String value = text.getText().toString();

            FlashCards.add(new FlashCard(value, ""));

            n.writeFlashCards(this.getContext(), FlashCards);

            n.getFlashCards(this.getContext());
            String namesStr ="";
            for (FlashCard x : n.getFlashCards(this.getContext())) {
                namesStr = namesStr + "\n" + x.getTerm() + " " + x.getDef();

            }*/
}

    @Override
    public void onClick(View v) {


            if(v.getId() == R.id.Add){

             DBFlashCardStore n = new DBFlashCardStore(this.getContext());
             ArrayList<FlashCard> FlashCards = n.getFlashCards(this.getContext());

                EditText text = getView().findViewById(R.id.term);
                String value = text.getText().toString();

            FlashCards.add(new FlashCard(value, ""));

            n.writeFlashCards(this.getContext(), FlashCards);

             n.getFlashCards(this.getContext());
             String namesStr = "";
             for (FlashCard x : n.getFlashCards(this.getContext())) {
            namesStr = namesStr + "\n" + x.getTerm() + " " + x.getDef();

                 }//to do clear strings when done

        }

    }
    }