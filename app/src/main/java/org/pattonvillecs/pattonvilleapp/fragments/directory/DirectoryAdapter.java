package org.pattonvillecs.pattonvilleapp.fragments.directory;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;

/**
 * Created by gadsonk on 1/20/17.
 */

public class DirectoryAdapter extends FlexibleAdapter<Faculty> {

    public DirectoryAdapter() {
        this(new ArrayList<Faculty>());
    }

    public DirectoryAdapter(@Nullable List<Faculty> items) {
        this(items, null);
    }

    public DirectoryAdapter(@Nullable List<Faculty> items, @Nullable Object listeners) {
        this(items, listeners, false);
    }

    public DirectoryAdapter(@Nullable List<Faculty> items, @Nullable Object listeners, boolean stableIds) {
        super(items, listeners, stableIds);
    }
}
