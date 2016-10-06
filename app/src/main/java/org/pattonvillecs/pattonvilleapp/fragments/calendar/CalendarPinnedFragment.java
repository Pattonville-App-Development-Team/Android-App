package org.pattonvillecs.pattonvilleapp.fragments.calendar;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.pattonvillecs.pattonvilleapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarPinnedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarPinnedFragment extends Fragment {

    public CalendarPinnedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CalendarPinnedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalendarPinnedFragment newInstance() {
        CalendarPinnedFragment fragment = new CalendarPinnedFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar_pinned, container, false);
    }

}
