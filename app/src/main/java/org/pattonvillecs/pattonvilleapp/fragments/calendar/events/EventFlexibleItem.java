package org.pattonvillecs.pattonvilleapp.fragments.calendar.events;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Summary;

import org.apache.commons.lang3.tuple.Pair;
import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.R;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;

/**
 * Created by Mitchell Skaggs on 1/4/17.
 */
public class EventFlexibleItem extends AbstractFlexibleItem<EventViewHolder> {

    public final Pair<DataSource, VEvent> pair;

    public EventFlexibleItem(Pair<DataSource, VEvent> pair) {
        this.pair = pair;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventFlexibleItem that = (EventFlexibleItem) o;

        return pair != null ? pair.equals(that.pair) : that.pair == null;
    }

    @Override
    public int hashCode() {
        return pair != null ? pair.hashCode() : 0;
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
    public void bindViewHolder(FlexibleAdapter adapter, EventViewHolder holder, int position, List payloads) {
        VEvent calendarEvent = pair.getRight();
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
            holder.bottomText.setVisibility(View.INVISIBLE);
        }

        holder.schoolColorImageView.setColorFilter(pair.getLeft().calendarColor);

        holder.shortSchoolName.setText(pair.getLeft().shortName);
    }
}
