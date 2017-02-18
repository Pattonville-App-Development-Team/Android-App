package org.pattonvillecs.pattonvilleapp.fragments.calendar;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.events.EventAdapter;

import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarPinnedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarPinnedFragment extends Fragment implements SwipeRefreshLayout.OnChildScrollUpCallback {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private TextView noItemsTextView;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_calendar_pinned, container, false);


        recyclerView = (RecyclerView) layout.findViewById(R.id.event_recycler_view);

        eventAdapter = new EventAdapter(null);

        recyclerView.setAdapter(eventAdapter);
        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(getContext(), OrientationHelper.VERTICAL, false));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        noItemsTextView = (TextView) layout.findViewById(R.id.no_items_textview);

        return layout;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean canChildScrollUp(SwipeRefreshLayout parent, @Nullable View child) {
        if (recyclerView != null)
            return recyclerView.canScrollVertically(-1);
        else
            return false;
    }
}
