package org.pattonvillecs.pattonvilleapp.about;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.pattonvillecs.pattonvilleapp.R;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by skaggsm on 5/11/17.
 */

public class DeveloperItem extends AbstractSectionableItem<DeveloperItem.DeveloperViewHolder, DeveloperHeaderItem> {
    private final String name;
    private final String text;
    private final int imageRes;

    public DeveloperItem(DeveloperHeaderItem headerItem, String name, String text, @DrawableRes int imageRes) {
        super(headerItem);
        this.name = name;
        this.text = text;
        this.imageRes = imageRes;
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, DeveloperViewHolder holder, int position, List payloads) {
        Context context = adapter.getRecyclerView().getContext();

        holder.name.setText(this.name);
        holder.text.setText(this.text);

        Picasso.with(context)
                .load(imageRes)
                .error(imageRes)
                .fit()
                .centerCrop()
                .transform(new CircleTransformation())
                .into(holder.image);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_about_developer_item;
    }

    @Override
    public DeveloperViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new DeveloperViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
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

    protected class DeveloperViewHolder extends FlexibleViewHolder {
        public final View view;
        public final TextView name, text;
        public final ImageView image;

        public DeveloperViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            this.view = view;
            this.name = (TextView) view.findViewById(R.id.developer_name);
            this.text = (TextView) view.findViewById(R.id.developer_text);
            this.image = (ImageView) view.findViewById(R.id.developer_image);
        }
    }
}
