package com.example.mobiledevlopmentproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MakeSets#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MakeSets extends Fragment implements View.OnClickListener{
    Button addbutton;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MakeSets() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MakeSets.
     */
    // TODO: Rename and change types and number of parameters
    public static MakeSets newInstance(String param1, String param2) {
        MakeSets fragment = new MakeSets();
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
        View makingView = inflater.inflate(R.layout.fragment_make_sets, container, false);

        addbutton =  makingView.findViewById(R.id.Addset);
        addbutton.setOnClickListener(this);
        Spinner Subjects=makingView.findViewById(R.id.Subject);
        ArrayList<String> SubjectList=new ArrayList<>();
        SubjectList.add("maths");
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>
                        (this.getContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                SubjectList);
        Subjects.setAdapter(adapter);
        return makingView;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.Addset){

            DBSetStore n = new DBSetStore(this.getContext());

            ArrayList<Set> sets = n.getSets(this.getContext());
            sets.clear();
            assert getView() != null;
            Spinner Subject=getView().findViewById(R.id.Subject);
            String Sub= Subject.getSelectedItem().toString();

            EditText Setval = getView().findViewById(R.id.Set);
            String Setvalue = Setval.getText().toString();


            sets.add(new Set(Setvalue,Sub));

            n.writeSets(this.getContext(), sets);

            n.getSets(this.getContext());


            StringBuilder namesStr = new StringBuilder();
            for (Set x : n.getSets(this.getContext())) {
                namesStr.append("\n").append(x.getSubject()).append(" ").append(x.getSetName());

            }//to do clear strings when done

        }
    }
}