package org.pattonvillecs.pattonvilleapp.about.detail;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.pattonvillecs.pattonvilleapp.R;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by Mitchell on 5/26/2017.
 */

public class LinkItem extends AbstractFlexibleItem<LinkItem.LinkViewHolder> implements Parcelable {
    public static final Creator<LinkItem> CREATOR = new Creator<LinkItem>() {
        @Override
        public LinkItem createFromParcel(Parcel in) {
            return new LinkItem(in);
        }

        @Override
        public LinkItem[] newArray(int size) {
            return new LinkItem[size];
        }
    };
    @DrawableRes
    private final int image;
    private final String link;

    public LinkItem(@DrawableRes int image, String link) {
        this.image = image;
        this.link = link;
    }

    private LinkItem(Parcel in) {
        image = in.readInt();
        link = in.readString();
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, LinkViewHolder holder, int position, List payloads) {
        Context context = adapter.getRecyclerView().getContext();

        holder.imageView.setOnClickListener(v -> context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link))));
        Picasso.with(context)
                .load(image)
                .error(image)
                .fit()
                .centerInside()
                .into(holder.imageView);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_about_detail_link_item;
    }

    @Override
    public LinkViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new LinkViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LinkItem linkItem = (LinkItem) o;

        if (image != linkItem.image) return false;
        return link != null ? link.equals(linkItem.link) : linkItem.link == null;

    }

    @Override
    public int hashCode() {
        int result = image;
        result = 31 * result + (link != null ? link.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(image);
        dest.writeString(link);
    }

    class LinkViewHolder extends FlexibleViewHolder {
        final ImageView imageView;

        LinkViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            imageView = (ImageView) view.findViewById(R.id.link_image);
        }
    }
}
