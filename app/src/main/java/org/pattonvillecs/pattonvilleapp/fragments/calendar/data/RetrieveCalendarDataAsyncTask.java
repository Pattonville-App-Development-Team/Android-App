package org.pattonvillecs.pattonvilleapp.fragments.calendar.data;

import android.os.AsyncTask;

import com.google.common.collect.HashMultimap;

import net.fortuna.ical4j.model.component.VEvent;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.fix.SerializableCalendarDay;

/**
 * Created by Mitchell on 1/25/2017.
 */

public class RetrieveCalendarDataAsyncTask extends AsyncTask<DataSource, Double, HashMultimap<SerializableCalendarDay, VEvent>> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected HashMultimap<SerializableCalendarDay, VEvent> doInBackground(DataSource... params) {
        return null;
    }

    @Override
    protected void onPostExecute(HashMultimap<SerializableCalendarDay, VEvent> result) {
        super.onPostExecute(result);
    }
}
