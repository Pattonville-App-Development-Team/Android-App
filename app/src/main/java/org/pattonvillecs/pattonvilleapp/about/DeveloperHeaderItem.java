package org.pattonvillecs.pattonvilleapp.about;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.pattonvillecs.pattonvilleapp.R;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractHeaderItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by skaggsm on 5/12/17.
 */

public class DeveloperHeaderItem extends AbstractHeaderItem<DeveloperHeaderItem.DeveloperHeaderItemViewHolder> {
    private final String text;

    public DeveloperHeaderItem(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeveloperHeaderItem that = (DeveloperHeaderItem) o;

        return text != null ? text.equals(that.text) : that.text == null;

    }

    @Override
    public int hashCode() {
        return text != null ? text.hashCode() : 0;
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, DeveloperHeaderItemViewHolder holder, int position, List payloads) {
        holder.text.setText(this.text);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_about_developer_header;
    }

    @Override
    public DeveloperHeaderItemViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new DeveloperHeaderItemViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    class DeveloperHeaderItemViewHolder extends FlexibleViewHolder {
        final TextView text;

        public DeveloperHeaderItemViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            text = (TextView) view.findViewById(R.id.about_us_header_text);
        }
    }
}
