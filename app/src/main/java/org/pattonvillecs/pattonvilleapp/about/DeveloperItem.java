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

package org.pattonvillecs.pattonvilleapp.about;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.about.detail.AboutDetailActivity;
import org.pattonvillecs.pattonvilleapp.about.detail.LinkItem;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.viewholders.FlexibleViewHolder;

import static org.pattonvillecs.pattonvilleapp.calendar.events.EventFlexibleItem.getActivity;

/**
 * Created by skaggsm on 5/11/17.
 */

public class DeveloperItem extends AbstractSectionableItem<DeveloperItem.DeveloperViewHolder, DeveloperHeaderItem> {
    private static final String TAG = DeveloperItem.class.getSimpleName();

    private final String name;
    private final String text;
    private final int imageRes;
    private final LinkItem[] linkItems;

    public DeveloperItem(DeveloperHeaderItem headerItem, String name, String text, @DrawableRes int imageRes, LinkItem... linkItems) {
        super(headerItem);
        this.name = name;
        this.text = text;
        this.imageRes = imageRes;
        this.linkItems = linkItems;
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, DeveloperViewHolder holder, int position, List payloads) {
        Context context = adapter.getRecyclerView().getContext();

        holder.name.setText(this.name);

        Picasso.with()
                .load(imageRes)
                .error(imageRes)
                .fit()
                .centerCrop()
                .transform(new CircleTransformation())
                .into(holder.image);

        holder.view.setOnClickListener(v -> {
            @SuppressWarnings("unchecked")
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(context),
                    Pair.create(holder.image, "developer_image"),
                    Pair.create(holder.name, "developer_name"));

            context.startActivity(AboutDetailActivity.createIntent(context, name, text, imageRes, linkItems), options.toBundle());
        });
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_about_developer_item;
    }

    @Override
    public DeveloperViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return new DeveloperViewHolder(view, adapter);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeveloperItem that = (DeveloperItem) o;

        if (imageRes != that.imageRes) return false;
        return text != null ? text.equals(that.text) : that.text == null;

    }

    @Override
    public int hashCode() {
        int result = text != null ? text.hashCode() : 0;
        result = 31 * result + imageRes;
        return result;
    }

    protected static class DeveloperViewHolder extends FlexibleViewHolder {
        public final View view;
        public final TextView name;
        public final ImageView image;

        public DeveloperViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            this.view = view;
            this.name = (TextView) view.findViewById(R.id.developer_name);
            this.image = (ImageView) view.findViewById(R.id.developer_image);
        }
    }
}
