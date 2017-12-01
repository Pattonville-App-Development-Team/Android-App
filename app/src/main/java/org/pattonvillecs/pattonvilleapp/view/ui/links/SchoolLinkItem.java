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

package org.pattonvillecs.pattonvilleapp.view.ui.links;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.R;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * @author Mitchell Skaggs
 * @since 1.0.0
 */

public abstract class SchoolLinkItem extends AbstractFlexibleItem<SchoolLinkItem.SchoolLinkItemViewHolder> {
    protected final DataSource dataSource;

    public SchoolLinkItem(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, SchoolLinkItemViewHolder holder, int position, List payloads) {
        Context context = adapter.getRecyclerView().getContext();

        Picasso.with()
                .load(dataSource.mascotDrawableRes)
                .error(dataSource.mascotDrawableRes)
                .fit()
                .centerInside()
                .into(holder.schoolMascot);

        holder.schoolName.setText(dataSource.name);

        holder.itemView.setOnClickListener(v -> onClick(context));
    }

    protected abstract void onClick(Context context);

    @Override
    public int getLayoutRes() {
        return R.layout.school_links_list_item;
    }

    @Override
    public SchoolLinkItemViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return new SchoolLinkItemViewHolder(view, adapter);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SchoolLinkItem that = (SchoolLinkItem) o;

        return dataSource == that.dataSource;

    }

    @Override
    public int hashCode() {
        return dataSource != null ? dataSource.hashCode() : 0;
    }

    static class SchoolLinkItemViewHolder extends FlexibleViewHolder {
        final ImageView schoolMascot;
        final TextView schoolName;

        public SchoolLinkItemViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            this.schoolMascot = view.findViewById(R.id.school_mascot);
            this.schoolName = view.findViewById(R.id.school_name);
        }
    }
}
