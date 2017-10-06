/*
 * Copyright (C) 2017 Mitchell Skaggs, Keturah Gadson, Ethan Holtgrieve, Nathan Skelton, Pattonville School District
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.pattonvillecs.pattonvilleapp.directory.detail;

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

    static class FacultyHeaderViewHolder extends FlexibleViewHolder {
        final TextView headerText;

        public FacultyHeaderViewHolder(View view, FlexibleAdapter adapter, boolean stickyHeader) {
            super(view, adapter, stickyHeader);
            headerText = (TextView) view.findViewById(R.id.directory_header_text);
        }

    }
}
