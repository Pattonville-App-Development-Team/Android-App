package org.pattonvillecs.pattonvilleapp.fragments.calendar.events;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Summary;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.CalendarEventDetailsActivity;

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
    public void bindViewHolder(FlexibleAdapter adapter, EventViewHolder holder, final int position, List payloads) {
        Summary summary = vEvent.getSummary();
        Location location = vEvent.getLocation();

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

                    Intent intent = new Intent(activity, CalendarEventDetailsActivity.class).putExtra("calendarEvent", EventFlexibleItem.this);
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

            private Activity getActivity(View v) {
                Context context = v.getContext();
                while (context instanceof ContextWrapper) {
                    if (context instanceof Activity) {
                        return (Activity) context;
                    }
                    context = ((ContextWrapper) context).getBaseContext();
                }
                return null;
            }
        });
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
        final TextView topText, bottomText, shortSchoolName;
        final ImageView schoolColorImageView;
        final View view;

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
        }
    }
}
