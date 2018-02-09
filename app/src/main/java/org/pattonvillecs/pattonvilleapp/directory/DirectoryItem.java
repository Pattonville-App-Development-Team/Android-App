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

package org.pattonvillecs.pattonvilleapp.directory;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;
import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.directory.detail.DirectoryDetailActivity;

import java.util.List;
import java.util.Set;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by skaggsm on 5/9/17.
 */

public class DirectoryItem extends AbstractFlexibleItem<DirectoryItem.DirectoryItemViewHolder> {
    private final Set<DataSource> dataSources;

    public DirectoryItem(Set<DataSource> dataSources) {
        this.dataSources = dataSources;
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, DirectoryItemViewHolder holder, int position, List payloads) {
        Context context = adapter.getRecyclerView().getContext();

        holder.view.setOnClickListener(
                v -> context.startActivity(DirectoryDetailActivity.createIntent(context, dataSources))
        );

        DataSource dataSource;
        if (dataSources.size() == 1) {
            dataSource = dataSources.iterator().next();
            holder.name.setText(dataSource.name);
        } else if (dataSources.equals(DataSource.ALL)) {
            dataSource = DataSource.DISTRICT;
            holder.name.setText(R.string.directory_name_all_schools);
        } else {
            dataSource = DataSource.DISTRICT;
            holder.name.setText(StringUtils.join(dataSources, ", "));
        }

        Picasso.with()
                .load(dataSource.mascotDrawableRes)
                .error(dataSource.mascotDrawableRes) //Needed because VectorDrawables are not loaded properly when using .load(). See square/picasso/issues/1109
                .centerInside().fit().into(holder.icon);
    }

    @Override
    public DirectoryItemViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return new DirectoryItemViewHolder(view, adapter);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.directory_recycler_view_item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DirectoryItem that = (DirectoryItem) o;

        return dataSources != null ? dataSources.equals(that.dataSources) : that.dataSources == null;

    }

    @Override
    public int hashCode() {
        return dataSources != null ? dataSources.hashCode() : 0;
    }

    static class DirectoryItemViewHolder extends FlexibleViewHolder {
        final View view;
        final TextView name;
        final ImageView icon;

        public DirectoryItemViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            this.view = view;
            this.name = (TextView) view.findViewById(R.id.directory_name);
            this.icon = (ImageView) view.findViewById(R.id.directory_icon);
        }
    }
}
