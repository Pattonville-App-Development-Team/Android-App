package org.pattonvillecs.pattonvilleapp.fragments.calendar;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Summary;

import org.apache.commons.collections4.map.MultiValueMap;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import eu.davidea.fastscroller.FastScroller;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.utils.Utils;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarEventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarEventsFragment extends Fragment implements CalendarFragment.OnCalendarDataUpdatedListener {
    private static final String TAG = "CalendarEventsFragment";
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private CalendarFragment calendarFragment;
    private CalendarData calendarData = new CalendarData();
    private TextView noItemsTextView;

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
        setHasOptionsMenu(true);
        calendarFragment = (CalendarFragment) getParentFragment();
        calendarFragment.addOnCalendarDataUpdatedListener(this);
    }

    private void goToCurrentDay() {
        int mostRecentEventPosition = 0;
        Date today = new Date();
        Log.e(TAG, "Today is: " + SimpleDateFormat.getDateInstance().format(today));

        for (int i = 0; i < eventAdapter.getItemCount(); i++) {
            Date eventDate = eventAdapter.getItem(i).pair.getValue().getStartDate().getDate();
            if (!eventDate.after(today))
                mostRecentEventPosition = i;
            else {
                mostRecentEventPosition = i;
                break;
            }
        }

        recyclerView.scrollToPosition(mostRecentEventPosition);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        calendarFragment.removeOnCalendarDataUpdatedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_goto_today:
                goToCurrentDay();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_calendar_action_bar_menu, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_calendar_events, container, false);

        recyclerView = (RecyclerView) layout.findViewById(R.id.event_recycler_view);

        eventAdapter = new EventAdapter();

        recyclerView.setAdapter(eventAdapter);
        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(getContext(), OrientationHelper.VERTICAL, false));

        eventAdapter.setFastScroller((FastScroller) layout.findViewById(R.id.fast_scroller), Utils.fetchAccentColor(getContext(), Color.RED));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        noItemsTextView = (TextView) layout.findViewById(R.id.no_items_textview);

        if (savedInstanceState != null)
            goToCurrentDay();

        return layout;
    }

    @Override
    public void updateCalendarData(CalendarData calendarData) {
        this.calendarData = calendarData;
        eventAdapter.clear();
        List<EventFlexibleItem> items = Stream.of(calendarData.getCalendars())
                .flatMap(new Function<Map.Entry<DataSource, MultiValueMap<CalendarDay, VEvent>>, Stream<Pair<DataSource, VEvent>>>() {
                    @Override
                    public Stream<Pair<DataSource, VEvent>> apply(final Map.Entry<DataSource, MultiValueMap<CalendarDay, VEvent>> dataSourceMultiValueMapEntry) {
                        return Stream.of(dataSourceMultiValueMapEntry.getValue().iterator()).map(new Function<Map.Entry<CalendarDay, VEvent>, Pair<DataSource, VEvent>>() {
                            @Override
                            public Pair<DataSource, VEvent> apply(Map.Entry<CalendarDay, VEvent> calendarDayVEventEntry) {
                                return new ImmutablePair<>(dataSourceMultiValueMapEntry.getKey(), calendarDayVEventEntry.getValue());
                            }
                        });
                    }
                })
                .sorted(new Comparator<Pair<DataSource, VEvent>>() {
                    @Override
                    public int compare(Pair<DataSource, VEvent> o1, Pair<DataSource, VEvent> o2) {
                        //This is Google Calendar style scrolling: future events to the bottom
                        return o1.getValue().getStartDate().getDate().compareTo(o2.getValue().getStartDate().getDate());
                    }
                })
                .map(new Function<Pair<DataSource, VEvent>, EventFlexibleItem>() {
                    @Override
                    public EventFlexibleItem apply(Pair<DataSource, VEvent> dataSourceVEventPair) {
                        return new EventFlexibleItem(dataSourceVEventPair);
                    }
                })
                .collect(Collectors.<EventFlexibleItem>toList());

        if (items.size() > 0)
            noItemsTextView.setVisibility(View.GONE);
        else
            noItemsTextView.setVisibility(View.VISIBLE);

        eventAdapter.addItems(eventAdapter.getItemCount(), items);
    }

    private static class EventAdapter extends FlexibleAdapter<EventFlexibleItem> {

        public EventAdapter() {
            this(new ArrayList<EventFlexibleItem>());
        }

        public EventAdapter(@Nullable List<EventFlexibleItem> items) {
            this(items, null);
        }

        public EventAdapter(@Nullable List<EventFlexibleItem> items, @Nullable Object listeners) {
            this(items, listeners, false);
        }

        public EventAdapter(@Nullable List<EventFlexibleItem> items, @Nullable Object listeners, boolean stableIds) {
            super(items, listeners, stableIds);
        }

        @Override
        public String onCreateBubbleText(int position) {
            EventFlexibleItem item = getItem(position);
            return DateFormat.getDateFormat(this.getRecyclerView().getContext()).format(item.pair.getValue().getStartDate().getDate());
        }
    }

    private static class EventViewHolder extends FlexibleViewHolder {
        final TextView topText, bottomText;
        public ImageView schoolColorImageView;

        public EventViewHolder(View view, FlexibleAdapter adapter) {
            this(view, adapter, false);
        }

        public EventViewHolder(View view, FlexibleAdapter adapter, boolean stickyHeader) {
            super(view, adapter, stickyHeader);
            topText = (TextView) view.findViewById(R.id.text_top);
            bottomText = (TextView) view.findViewById(R.id.text_bottom);
            schoolColorImageView = (ImageView) view.findViewById(R.id.school_color_circle);
        }
    }

    private static class EventFlexibleItem extends AbstractFlexibleItem<EventViewHolder> {

        private final Pair<DataSource, VEvent> pair;

        public EventFlexibleItem(Pair<DataSource, VEvent> pair) {
            this.pair = pair;
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
            return R.layout.dateless_event_list_item;
        }

        @Override
        public EventViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
            return new EventViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
        }

        @Override
        public void bindViewHolder(FlexibleAdapter adapter, EventViewHolder holder, int position, List payloads) {
            //holder.topText.setText(pair.getValue().getSummary().getValue());

            VEvent calendarEvent = pair.getValue();
            Summary summary = calendarEvent.getSummary();
            Location location = calendarEvent.getLocation();

            if (summary != null)
                holder.topText.setText(summary.getValue());
            else
                holder.topText.setText(R.string.no_summary);

            if (location != null) {
                holder.bottomText.setVisibility(View.VISIBLE);
                holder.bottomText.setText(location.getValue());
            } else {
                holder.bottomText.setVisibility(View.GONE);
                holder.bottomText.setText(R.string.no_location);
            }
            holder.schoolColorImageView.setColorFilter(pair.getKey().calendarColor);
        }
    }
}
