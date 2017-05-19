package org.pattonvillecs.pattonvilleapp.about.secret;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.DrawableRes;

import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.about.DeveloperHeaderItem;
import org.pattonvillecs.pattonvilleapp.about.DeveloperItem;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;

/**
 * Created by skaggsm on 5/12/17.
 */

public class SecretDeveloperItem extends DeveloperItem {
    public SecretDeveloperItem(DeveloperHeaderItem headerItem, String name, String text, @DrawableRes int imageRes) {
        super(headerItem, name, text, imageRes);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, DeveloperViewHolder holder, int position, List payloads) {
        Context context = adapter.getRecyclerView().getContext();

        holder.image.setImageDrawable(null);
        holder.image.setBackground(null);
        holder.view.setBackground(null);
        holder.name.setText(null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.view.setBackgroundResource(context.obtainStyledAttributes(new int[]{R.attr.selectableItemBackgroundBorderless}).getResourceId(0, -1));
        } else {
            holder.view.setBackgroundResource(context.obtainStyledAttributes(new int[]{R.attr.selectableItemBackground}).getResourceId(0, -1));
        }

        holder.view.setOnLongClickListener(v -> {
            context.startActivity(new Intent(context, SecretActivity.class));
            return true;
        });
    }
}
