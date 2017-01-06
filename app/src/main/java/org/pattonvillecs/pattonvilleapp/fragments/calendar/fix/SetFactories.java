package org.pattonvillecs.pattonvilleapp.fragments.calendar.fix;

import android.os.Parcel;
import android.os.Parcelable;

import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.collections4.Factory;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Created by skaggsm on 1/4/17.
 */

public final class SetFactories {
    private SetFactories() {
    }

    public static final class HashSetVEventFactory implements Factory<HashSet<VEvent>>, Parcelable, Serializable {
        public static final Creator<HashSetVEventFactory> CREATOR = new Creator<HashSetVEventFactory>() {
            @Override
            public HashSetVEventFactory createFromParcel(Parcel in) {
                return new HashSetVEventFactory(in);
            }

            @Override
            public HashSetVEventFactory[] newArray(int size) {
                return new HashSetVEventFactory[size];
            }
        };

        public HashSetVEventFactory() {
            super();
        }

        protected HashSetVEventFactory(Parcel in) {
            this();
        }

        @Override
        public HashSet<VEvent> create() {
            return new HashSet<>();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
        }
    }
}
