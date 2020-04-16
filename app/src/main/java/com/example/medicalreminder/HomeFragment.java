package com.example.medicalreminder;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.techisfun.onelinecalendar.DateSelectionListener;
import com.github.techisfun.onelinecalendar.OneLineCalendarView;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private View fragView;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragView = inflater.inflate(R.layout.fragment_home, container, false);
        final OneLineCalendarView calendarView = (OneLineCalendarView) fragView.findViewById(R.id.calendar_view);

        calendarView.setOnDateClickListener(new DateSelectionListener() {
            @Override
            public boolean onDateSelected(@NonNull Date date) {
                calendarView.setStickyHeaderText(SimpleDateFormat.getDateInstance().format(date));
                return true;
            }


            @Override
            public boolean onDateUnselected() {
                calendarView.setStickyHeaderText("No Selection");
                return true;
            }
        });
        return fragView;
    }

}
