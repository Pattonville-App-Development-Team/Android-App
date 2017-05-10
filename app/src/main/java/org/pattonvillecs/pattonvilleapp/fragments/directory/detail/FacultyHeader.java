package org.pattonvillecs.pattonvilleapp.fragments.directory.detail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.R;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractHeaderItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by skaggsm on 5/10/17.
 */

public class FacultyHeader extends AbstractHeaderItem<FacultyHeader.FacultyHeaderViewHolder> {
    private final DataSource dataSource;

    public FacultyHeader(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.directory_datasource_header;
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, FacultyHeaderViewHolder holder, int position, List payloads) {
        holder.headerText.setText(dataSource.name);
    }

    @Override
    public FacultyHeaderViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new FacultyHeaderViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter, true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FacultyHeader that = (FacultyHeader) o;

        return dataSource == that.dataSource;

    }

    @Override
    public int hashCode() {
        return dataSource != null ? dataSource.hashCode() : 0;
    }

    class FacultyHeaderViewHolder extends FlexibleViewHolder {
        final TextView headerText;

        public FacultyHeaderViewHolder(View view, FlexibleAdapter adapter, boolean stickyHeader) {
            super(view, adapter, stickyHeader);
            headerText = (TextView) view.findViewById(R.id.directory_header_text);
        }

    }
}
