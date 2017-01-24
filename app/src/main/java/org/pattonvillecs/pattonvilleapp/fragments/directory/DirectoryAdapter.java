package org.pattonvillecs.pattonvilleapp.fragments.directory;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;

/**
 * Created by gadsonk on 1/20/17.
 */

public class DirectoryAdapter extends FlexibleAdapter<DirectoryFlexibleItem> {

    public DirectoryAdapter() {
        this(new ArrayList<DirectoryFlexibleItem>());
    }

    public DirectoryAdapter(@Nullable List<DirectoryFlexibleItem> items) {
        this(items, null);
    }

    public DirectoryAdapter(@Nullable List<DirectoryFlexibleItem> items, @Nullable Object listeners) {
        this(items, listeners, false);
    }

    public DirectoryAdapter(@Nullable List<DirectoryFlexibleItem> items, @Nullable Object listeners, boolean stableIds) {
        super(items, listeners, stableIds);
    }
}
