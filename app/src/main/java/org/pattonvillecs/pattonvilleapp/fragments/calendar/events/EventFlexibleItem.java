package org.pattonvillecs.pattonvilleapp.fragments.calendar.events;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Summary;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.CalendarEventDetailsActivity;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.pinned.PinnedEventsContract;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by Mitchell Skaggs on 1/4/17.
 */
public class EventFlexibleItem extends AbstractSectionableItem<EventFlexibleItem.EventViewHolder, EventHeader> implements FlexibleHasCalendarDay<EventFlexibleItem.EventViewHolder>, Parcelable {
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

    public final DataSource dataSource;
    public final VEvent vEvent;

    protected EventFlexibleItem(Parcel in) {
        this((DataSource) in.readSerializable(), (VEvent) in.readSerializable());
    }

    public EventFlexibleItem(DataSource dataSource, VEvent vEvent) {
        //super(new EventHeader(CalendarDay.from(pair.getValue().getStartDate().getDate())));
        super(null);
        this.dataSource = dataSource;
        this.vEvent = vEvent;
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
                null,
                PinnedEventsContract.PinnedEventsTable.COLUMN_NAME_UID + "=?",
                new String[]{uid},
                null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventFlexibleItem that = (EventFlexibleItem) o;

        if (dataSource != that.dataSource) return false;
        return vEvent != null ? vEvent.equals(that.vEvent) : that.vEvent == null;

    }

    @Override
    public int hashCode() {
        int result = dataSource != null ? dataSource.hashCode() : 0;
        result = 31 * result + (vEvent != null ? vEvent.hashCode() : 0);
        return result;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.calendar_dateless_event_list_item;
    }

    @Override
    public EventViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new EventViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, final EventViewHolder holder, final int position, List payloads) {
        if (holder.cursor != null && !holder.cursor.isClosed()) {
            holder.cursor.close();
            holder.cursor = null;
            //Make is so.
        }
        Summary summary = vEvent.getSummary();
        Location location = vEvent.getLocation();
        final String uid = vEvent.getUid().getValue();

        if (summary != null)
            holder.topText.setText(summary.getValue());
        else
            holder.topText.setText(R.string.no_summary);

        if (location != null) {
            holder.bottomText.setVisibility(View.VISIBLE);
            holder.bottomText.setText(location.getValue());
        } else {
            holder.bottomText.setVisibility(View.INVISIBLE);
        }

        holder.schoolColorImageView.setColorFilter(dataSource.calendarColor);

        holder.shortSchoolName.setText(dataSource.shortName);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity(v);
                if (activity != null) {

                    Intent intent = new Intent(activity, CalendarEventDetailsActivity.class).putExtra(CalendarEventDetailsActivity.CALENDAR_EVENT_KEY, EventFlexibleItem.this);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        View schoolColorCircle = v.findViewById(R.id.school_color_circle);
                        View textTop = v.findViewById(R.id.text_top);
                        View textBottom = v.findViewById(R.id.text_bottom);

                        activity.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity,
                                Pair.create(textTop, activity.getResources().getString(R.string.text_top_transition_name)),
                                Pair.create(textBottom, activity.getResources().getString(R.string.text_bottom_transition_name)),
                                Pair.create(schoolColorCircle, activity.getResources().getString(R.string.school_color_circle_transition_name))
                        ).toBundle());
                    } else {
                        activity.startActivity(intent);
                    }
                }
            }
        });
        holder.sparkButton.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                Log.i(TAG, "State: " + buttonState + " UID: " + uid);
                Log.v(TAG, "Full table is now: \n" + cursorToString(holder.view.getContext().getContentResolver().query(PinnedEventsContract.PinnedEventsTable.CONTENT_URI, null, null, null, null)));

                if (buttonState) {
                    deleteEntriesWithUid(button.getContext(), uid);
                    insertEntry(button.getContext(), uid);
                } else {
                    deleteEntriesWithUid(button.getContext(), uid);
                }
            }
        });
        ContentObserver contentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            @Override
            public void onChange(boolean selfChange) {
                onChange(selfChange, null);
            }

            @Override
            public void onChange(boolean selfChange, Uri uri) {
                if (holder.cursor != null) // Only needed for the first check
                    holder.cursor.close(); // The old view is closed, not needed anymore
                holder.cursor = requeryByUid(holder.view.getContext(), uid); // Get a new look at the dataset
                holder.cursor.registerContentObserver(this); // Attach this anonymous class to it so we keep getting updates

                Log.v(TAG, "Count of " + uid + ": " + holder.cursor.getCount());
                if (holder.cursor.getCount() > 0)
                    holder.sparkButton.setChecked(true);
                else
                    holder.sparkButton.setChecked(false);
            }
        };
        contentObserver.onChange(false); // Fire the first "event" to kick-start the requerying process. After this, the ContentObserver is automatically reregistered and cursors are closed/created
    }

    @Override
    public CalendarDay getCalendarDay() {
        return CalendarDay.from(vEvent.getStartDate().getDate());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(dataSource);
        dest.writeSerializable(vEvent);
    }

    /**
     * Created by Mitchell Skaggs on 1/4/17.
     */
    public static class EventViewHolder extends FlexibleViewHolder {
        private final TextView topText, bottomText, shortSchoolName;
        private final ImageView schoolColorImageView;
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
            schoolColorImageView = (ImageView) view.findViewById(R.id.school_color_circle);
            shortSchoolName = (TextView) view.findViewById(R.id.school_short_name);
            sparkButton = (SparkButton) view.findViewById(R.id.pinned_button);
        }
    }
}
