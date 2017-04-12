package org.pattonvillecs.pattonvilleapp.fragments.calendar.events;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.annimon.stream.Collector;
import com.annimon.stream.Stream;
import com.annimon.stream.function.BiConsumer;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Supplier;
import com.google.common.base.Stopwatch;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Summary;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.CalendarEventDetailsActivity;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.pinned.PinnedEventsContract;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.flexibleadapter.items.IFilterable;
import eu.davidea.viewholders.FlexibleViewHolder;
import me.xdrop.fuzzywuzzy.FuzzySearch;

/**
 * Created by Mitchell Skaggs on 1/4/17.
 */
public class EventFlexibleItem extends AbstractSectionableItem<EventFlexibleItem.EventViewHolder, EventHeader> implements FlexibleHasCalendarDay<EventFlexibleItem.EventViewHolder>, Parcelable, IFilterable {
    public static final Creator<EventFlexibleItem> CREATOR = new Creator<EventFlexibleItem>() {
        @Override
        public EventFlexibleItem createFromParcel(Parcel in) {
            return new EventFlexibleItem(in);
        }

        @Override
        public EventFlexibleItem[] newArray(int size) {
            return new EventFlexibleItem[size];
        }
    };
    private static final String TAG = EventFlexibleItem.class.getSimpleName();

    public final Set<DataSource> dataSources;
    public final VEvent vEvent;
    private final CalendarDay calendarStartDay;

    protected EventFlexibleItem(Parcel in) {
        //noinspection unchecked
        this((EnumSet<DataSource>) in.readSerializable(), (VEvent) in.readSerializable());
    }

    public EventFlexibleItem(DataSource dataSource, VEvent vEvent) {
        this(EnumSet.of(dataSource), vEvent);
    }

    public EventFlexibleItem(Set<DataSource> dataSource, VEvent vEvent) {
        //super(new EventHeader(CalendarDay.from(pair.getValue().getStartDate().getDate())));
        super(null);
        this.dataSources = dataSource;
        this.vEvent = vEvent;
        this.calendarStartDay = CalendarDay.from(vEvent.getStartDate().getDate());
    }

    private static Activity getActivity(View v) {
        Context context = v.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    private static String cursorToString(Cursor cursor) {
        StringBuilder cursorString = new StringBuilder();
        if (cursor.moveToFirst()) {
            String[] columnNames = cursor.getColumnNames();
            for (String name : columnNames)
                cursorString.append(String.format("%s ][ ", name));
            cursorString.append('\n');
            do {
                for (String name : columnNames) {
                    cursorString.append(String.format("%s ][ ", cursor.getString(cursor.getColumnIndex(name))));
                }
                cursorString.append('\n');
            } while (cursor.moveToNext());
        }
        cursor.close();
        return cursorString.toString();
    }

    private static void deleteEntriesWithUid(Context context, String uid) {
        context.getContentResolver().delete(
                PinnedEventsContract.PinnedEventsTable.CONTENT_URI,
                PinnedEventsContract.PinnedEventsTable.COLUMN_NAME_UID + "=?",
                new String[]{uid});
    }


    private static void insertEntry(Context context, String uid) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PinnedEventsContract.PinnedEventsTable.COLUMN_NAME_UID, uid);
        context.getContentResolver().insert(
                PinnedEventsContract.PinnedEventsTable.CONTENT_URI,
                contentValues);
    }

    private static Cursor requeryByUid(Context context, String uid) {
        return context.getContentResolver().query(
                PinnedEventsContract.PinnedEventsTable.CONTENT_URI,
                new String[]{PinnedEventsContract.PinnedEventsTable.COLUMN_NAME_UID},
                PinnedEventsContract.PinnedEventsTable.COLUMN_NAME_UID + "=?",
                new String[]{uid},
                null);
    }

    public static int compare(CalendarDay o1, CalendarDay o2) {
        if (o1.isAfter(o2))
            return 1;
        else if (o2.isAfter(o1))
            return -1;
        else
            return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventFlexibleItem that = (EventFlexibleItem) o;

        return !(vEvent == null || that.vEvent == null)
                && !(vEvent.getUid() == null || that.vEvent.getUid() == null)
                && vEvent.getUid().getValue().equals(that.vEvent.getUid().getValue());

    }

    @Override
    public int hashCode() {
        return vEvent != null ? vEvent.hashCode() : 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.calendar_dateless_event_list_item;
    }

    public int getXlLayoutRes() {

        return R.layout.calendar_dateless_event_item_xl;

    }

    @Override
    public EventViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {

        DisplayMetrics displayMetrics = inflater.getContext().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;

        if (dpHeight > 700) {
            return new EventViewHolder(inflater.inflate(getXlLayoutRes(), parent, false), adapter);
        } else {
            return new EventViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
        }
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, final EventViewHolder holder, final int position, List payloads) {
        Summary summary = vEvent.getSummary();
        DtStart startDate = vEvent.getStartDate();
        final String uid = vEvent.getUid().getValue();

        if (summary != null)
            holder.topText.setText(summary.getValue());
        else
            holder.topText.setText(R.string.no_summary);

        if (startDate != null) {
            holder.bottomText.setVisibility(View.VISIBLE);
            holder.bottomText.setText(SimpleDateFormat.getDateTimeInstance().format(startDate.getDate()));
        } else {
            holder.bottomText.setVisibility(View.INVISIBLE);
        }

        holder.shortSchoolName.setText(Stream.of(dataSources).map(new Function<DataSource, SpannableString>() {
            @Override
            public SpannableString apply(DataSource dataSource) {
                SpannableString spannableString = new SpannableString(("⬤ " + dataSource.shortName).replace(' ', '\u00A0'));

                spannableString.setSpan(new ForegroundColorSpan(dataSource.calendarColor), 0, 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                spannableString.setSpan(new RelativeSizeSpan(1.5f), 0, 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                return spannableString;
            }
        }).collect(new Collector<SpannableString, SpannableStringBuilder, SpannableStringBuilder>() {
            @Override
            public Supplier<SpannableStringBuilder> supplier() {
                return new Supplier<SpannableStringBuilder>() {
                    @Override
                    public SpannableStringBuilder get() {
                        return new SpannableStringBuilder();
                    }
                };
            }

            @Override
            public BiConsumer<SpannableStringBuilder, SpannableString> accumulator() {
                return new BiConsumer<SpannableStringBuilder, SpannableString>() {
                    @Override
                    public void accept(SpannableStringBuilder value1, SpannableString value2) {
                        value1.append(value2).append(", ");
                    }
                };
            }

            @Override
            public Function<SpannableStringBuilder, SpannableStringBuilder> finisher() {
                return new Function<SpannableStringBuilder, SpannableStringBuilder>() {
                    @Override
                    public SpannableStringBuilder apply(SpannableStringBuilder spannableStringBuilder) {
                        return spannableStringBuilder.delete(spannableStringBuilder.length() - 2, spannableStringBuilder.length());
                    }
                };
            }
        }));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity(v);
                if (activity != null) {

                    Intent intent = new Intent(activity, CalendarEventDetailsActivity.class).putExtra(CalendarEventDetailsActivity.CALENDAR_EVENT_KEY, EventFlexibleItem.this);
                    /*
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        View schoolColorCircle = v.findViewById(R.id.school_color_circle);
                        View textTop = v.findViewById(R.id.text_top);
                        View textBottom = v.findViewById(R.id.text_bottom);

                        activity.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity,
                                Pair.create(textTop, activity.getResources().getString(R.string.text_top_transition_name)),
                                Pair.create(textBottom, activity.getResources().getString(R.string.text_bottom_transition_name)),
                                Pair.create(schoolColorCircle, activity.getResources().getString(R.string.school_color_circle_transition_name))
                        ).toBundle());
                    } else
                    {*/
                    activity.startActivity(intent);
                    //}
                }
            }
        });
        holder.sparkButton.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(final ImageView button, final boolean buttonState) {
                Log.i(TAG, "State: " + buttonState + " UID: " + uid);
                //Expensive, not needed unless debugging
                //Log.v(TAG, "Full table is now: \n" + cursorToString(holder.view.getContext().getContentResolver().query(PinnedEventsContract.PinnedEventsTable.CONTENT_URI, null, null, null, null)));
                final Context context = button.getContext();

                Log.v(TAG, uid + " executing state " + buttonState);
                AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        Log.v(TAG, uid + " execution begin on thread " + Thread.currentThread().getName());
                        Stopwatch stopwatch = Stopwatch.createStarted();
                        if (buttonState) {
                            deleteEntriesWithUid(context, uid);
                            insertEntry(context, uid);
                        } else {
                            deleteEntriesWithUid(context, uid);
                        }
                        Log.v(TAG, uid + " execution finished on thread " + Thread.currentThread().getName() + " in " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
                    }
                });
                Log.v(TAG, uid + " finished executing state " + buttonState);
            }
        });

        ContentObserver contentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            @Override
            public void onChange(boolean selfChange) {
                onChange(selfChange, null);
            }

            @Override
            public void onChange(boolean selfChange, Uri uri) {
                Log.i(TAG, "On thread " + Thread.currentThread().getName());
                if (holder.cursor != null) // Only needed for the first check
                    holder.cursor.close(); // The old view is closed, not needed anymore
                holder.cursor = requeryByUid(holder.view.getContext(), uid); // Get a new look at the dataset
                holder.cursor.registerContentObserver(this); // Attach this anonymous class to it so we keep getting updates

                final int count = holder.cursor.getCount();
                Log.v(TAG, "Count of " + uid + ": " + count);

                holder.sparkButton.post(new Runnable() {
                    @Override
                    public void run() {
                        if (count > 0 != holder.sparkButton.isChecked())
                            holder.sparkButton.setChecked(!holder.sparkButton.isChecked());
                    }
                });
            }
        };
        contentObserver.onChange(false); // Fire the first "event" to kick-start the requerying process. After this, the ContentObserver is automatically reregistered and cursors are closed/created

    }

    @Override
    public CalendarDay getCalendarDay() {
        return calendarStartDay;
    }

    @Override
    public String toString() {
        return "EventFlexibleItem{" +
                "dataSources=" + dataSources +
                ", vEvent=\"" + vEvent.getSummary().getValue() +
                "\"}";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable((Serializable) dataSources);
        dest.writeSerializable(vEvent);
    }

    @Override
    public int compareTo(@NonNull FlexibleHasCalendarDay<?> o) {
        if (o instanceof EventFlexibleItem) {
            EventFlexibleItem other = (EventFlexibleItem) o;
            int result = this.vEvent.getStartDate().getDate().compareTo(other.vEvent.getStartDate().getDate());
            if (result != 0)
                return result;
            else
                return this.vEvent.getUid().getValue().compareTo(other.vEvent.getUid().getValue());
        } else
            return compare(this.getCalendarDay(), o.getCalendarDay());
    }

    @Override
    public boolean filter(String constraint) {
        constraint = constraint.toLowerCase();
        int summaryRatio = FuzzySearch.partialRatio(constraint, vEvent.getSummary().getValue().toLowerCase());
        int dataSourceRatio = FuzzySearch.partialRatio(constraint, dataSources.toString().toLowerCase());
        return summaryRatio > 80 || dataSourceRatio > 80;
    }

    /**
     * Created by Mitchell Skaggs on 1/4/17.
     */
    public static class EventViewHolder extends FlexibleViewHolder {
        private final TextView topText, bottomText, shortSchoolName;
        private final View view;
        private final SparkButton sparkButton;
        private Cursor cursor;

        public EventViewHolder(View view, FlexibleAdapter adapter) {
            this(view, adapter, false);
        }

        public EventViewHolder(View view, FlexibleAdapter adapter, boolean stickyHeader) {
            super(view, adapter, stickyHeader);
            this.view = view;
            topText = (TextView) view.findViewById(R.id.text_top);
            bottomText = (TextView) view.findViewById(R.id.text_bottom);
            shortSchoolName = (TextView) view.findViewById(R.id.school_short_names);
            sparkButton = (SparkButton) view.findViewById(R.id.pinned_button);
        }
    }
}
