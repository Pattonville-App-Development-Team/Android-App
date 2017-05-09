package org.pattonvillecs.pattonvilleapp.fragments.directory.detail;

import android.support.annotation.Nullable;

import org.apache.commons.lang.WordUtils;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;

/**
 * Created by gadsonk on 1/20/17.
 */

public class DirectoryAdapter<T extends IFlexible> extends FlexibleAdapter<T> {
    public DirectoryAdapter(@Nullable List items) {
        super(items);
    }

    public DirectoryAdapter(@Nullable List items, @Nullable Object listeners) {
        super(items, listeners);
    }

    public DirectoryAdapter(@Nullable List items, @Nullable Object listeners, boolean stableIds) {
        super(items, listeners, stableIds);
    }

    @Override
    public String onCreateBubbleText(int position) {
        T item = getItem(position);
        if (item instanceof Faculty)
            return WordUtils.capitalizeFully(((Faculty) item).getLongDesc());
        else
            return "";
    }
}
