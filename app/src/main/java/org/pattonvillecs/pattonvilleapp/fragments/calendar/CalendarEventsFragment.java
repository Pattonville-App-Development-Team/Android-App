package org.pattonvillecs.pattonvilleapp.fragments.calendar;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.pattonvillecs.pattonvilleapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarEventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarEventsFragment extends Fragment {
    private RecyclerView mRecyclerView;

    public CalendarEventsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CalendarEventsFragment.
     */
    public static CalendarEventsFragment newInstance() {
        return new CalendarEventsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_calendar_events, container, false);

        mRecyclerView = (RecyclerView) layout.findViewById(R.id.event_recycler_view);
        List<EventFlexibleItem> items = Arrays.asList(new EventFlexibleItem("A"), new EventFlexibleItem("B"), new EventFlexibleItem("C"), new EventFlexibleItem("D"), new EventFlexibleItem("E"),
                new EventFlexibleItem("F"), new EventFlexibleItem("G"), new EventFlexibleItem("H"), new EventFlexibleItem("I"));

        @SuppressWarnings("unchecked")
        FlexibleAdapter<EventFlexibleItem> flexibleAdapter = new EventAdapter(new ArrayList<>(items))
                .setEndlessScrollThreshold(10);

        mRecyclerView.setAdapter(flexibleAdapter);
        mRecyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(getContext(), OrientationHelper.VERTICAL, false));


        return layout;
    }

    private static class EventAdapter extends FlexibleAdapter<EventFlexibleItem> {
        public EventAdapter(@Nullable List<EventFlexibleItem> items) {
            this(items, null);
        }

        public EventAdapter(@Nullable List<EventFlexibleItem> items, @Nullable Object listeners) {
            this(items, listeners, false);
        }

        public EventAdapter(@Nullable List<EventFlexibleItem> items, @Nullable Object listeners, boolean stableIds) {
            super(items, listeners, stableIds);
            this.setEndlessScrollListener(new EndlessScrollListener() {
                private int inc = 0;

                @Override
                public void onLoadMore() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            List<EventFlexibleItem> items = new ArrayList<>();
                            for (int i = 0; i < 4; i++) {
                                items.add(new EventFlexibleItem("New item #" + inc++));
                            }
                            onLoadMoreComplete(items);
                        }
                    }, 1000);
                }
            }, new LoadingEventFlexibleItem());
        }


    }

    private static class EventViewHolder extends FlexibleViewHolder {
        final TextView mTitle;

        public EventViewHolder(View view, FlexibleAdapter adapter) {
            this(view, adapter, false);
        }

        public EventViewHolder(View view, FlexibleAdapter adapter, boolean stickyHeader) {
            super(view, adapter, stickyHeader);
            mTitle = (TextView) view.findViewById(R.id.title);
        }
    }

    private static class LoadingEventFlexibleItem extends EventFlexibleItem {
        public LoadingEventFlexibleItem() {
            super(null);
        }

        @Override
        public int getLayoutRes() {
            return R.layout.calendar_event_view_card_loading;
        }

        @Override
        public void bindViewHolder(FlexibleAdapter adapter, EventViewHolder holder, int position, List payloads) {
        }
    }

    private static class EventFlexibleItem extends AbstractFlexibleItem<EventViewHolder> {

        private final String name;

        public EventFlexibleItem(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            return this == o;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public int getLayoutRes() {
            return R.layout.calendar_event_view_card;
        }

        @Override
        public EventViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
            return new EventViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
        }

        @Override
        public void bindViewHolder(FlexibleAdapter adapter, EventViewHolder holder, int position, List payloads) {
            holder.mTitle.setText(name + " Pos: " + position);
        }
    }
}
