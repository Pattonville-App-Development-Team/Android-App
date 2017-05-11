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
    private final boolean useLongDesc;

    public DirectoryAdapter(@Nullable List<T> items, boolean useLongDesc) {
        super(items);
        this.useLongDesc = useLongDesc;
    }

    @Override
    public String onCreateBubbleText(int position) {
        T item = getItem(position);
        if (item instanceof Faculty) {
            Faculty faculty = (Faculty) item;

            if (useLongDesc)
                return WordUtils.capitalizeFully(faculty.getLongDesc());
            else
                return faculty.getDirectoryKey().name;
        } else
            return "";
    }
}
