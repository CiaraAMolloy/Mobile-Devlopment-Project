package com.example.mobiledevlopmentproject;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class CalendarPage extends Fragment {

    private CalendarView calendarView;
    private Button addEventButton;
    private ListView eventList;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> events = new ArrayList<>();
    private String selectedDate;
    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        addEventButton = view.findViewById(R.id.addEventButton);
        eventList = view.findViewById(R.id.eventList);

        prefs = requireContext().getSharedPreferences("Events", requireContext().MODE_PRIVATE);
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, events);
        eventList.setAdapter(adapter);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDate = sdf.format(new Date());

        loadEvents();

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
            loadEvents();
        });
        addEventButton.setOnClickListener(v -> showAddEventDialog());

        return view;
    }

    private void showAddEventDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add Event for " + selectedDate);

        final EditText input = new EditText(requireContext());
        input.setHint("Enter event title");
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String eventTitle = input.getText().toString();
            if (!eventTitle.isEmpty()) {
                saveEvent(selectedDate, eventTitle);
                loadEvents();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void saveEvent(String date, String title) {
        SharedPreferences.Editor editor = prefs.edit();
        java.util.Set<String> eventSet = prefs.getStringSet(date, new java.util.HashSet<>());
        eventSet.add(title);
        editor.putStringSet(date, eventSet);
        editor.apply();
    }

    private void loadEvents() {
        events.clear();
        java.util.Set<String> eventSet = prefs.getStringSet(selectedDate, new java.util.HashSet<>());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date now = new Date();
            Date eventDate = sdf.parse(selectedDate);
            long diff = eventDate.getTime() - now.getTime();
            long daysRemaining = diff / (1000 * 60 * 60 * 24);
            for (String e : eventSet) {
                events.add(e + " (" + Math.max(daysRemaining, 0) + " days left)");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();
    }
}
