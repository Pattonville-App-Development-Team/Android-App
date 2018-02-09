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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.R;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractHeaderItem;
import eu.davidea.viewholders.FlexibleViewHolder;

import static org.pattonvillecs.pattonvilleapp.directory.detail.DirectoryDetailActivity.startMapsActivityForPattonvilleLocation;

/**
 * Created by skaggsm on 5/8/17.
 */

public class DirectoryDetailHeaderItem extends AbstractHeaderItem<DirectoryDetailHeaderItem.DirectoryDetailHeaderViewHolder> implements DirectoryItem<DirectoryDetailHeaderItem.DirectoryDetailHeaderViewHolder> {

    private final DataSource dataSource;

    public DirectoryDetailHeaderItem(@NonNull DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @DrawableRes
    private static int getImageResourceForDataSource(DataSource dataSource) {
        switch (dataSource) {
            case HIGH_SCHOOL:
                return R.drawable.highschool_building;
            case HEIGHTS_MIDDLE_SCHOOL:
                return R.drawable.heights_building;
            case HOLMAN_MIDDLE_SCHOOL:
                return R.drawable.holman_building;
            case REMINGTON_TRADITIONAL_SCHOOL:
                return R.drawable.remington_building;
            case BRIDGEWAY_ELEMENTARY:
                return R.drawable.bridgeway_building;
            case DRUMMOND_ELEMENTARY:
                return R.drawable.drummond_building;
            case PARKWOOD_ELEMENTARY:
                return R.drawable.parkwood_building;
            case ROSE_ACRES_ELEMENTARY:
                return R.drawable.roseacres_building;
            case WILLOW_BROOK_ELEMENTARY:
                return R.drawable.willowbrook_building;
            case DISTRICT:
            default:
                return R.drawable.learningcenter_building;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DirectoryDetailHeaderItem that = (DirectoryDetailHeaderItem) o;

        return dataSource == that.dataSource;

    }

    @Override
    public int hashCode() {
        return dataSource.hashCode();
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, DirectoryDetailHeaderViewHolder holder, int position, List payloads) {
        Context context = adapter.getRecyclerView().getContext();
        Picasso.with().load(getImageResourceForDataSource(dataSource)).centerInside().fit().into(holder.schoolImage);

        holder.schoolName.setText(dataSource.name);

        holder.schoolAddress.setText(dataSource.address);
        holder.schoolAddress.setOnClickListener(v -> startMapsActivityForPattonvilleLocation(dataSource.address, context));

        holder.schoolPhoneNumber.setText(dataSource.mainNumber);
        holder.schoolPhoneNumber.setOnClickListener(v -> {
            Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
            //currently not working with pauseString extension = getExtension1();
            //currently not working with pause
            phoneIntent.setData(Uri.parse("tel:" + dataSource.mainNumber));
            context.startActivity(phoneIntent);
        });

        dataSource.attendanceNumber.ifPresentOrElse(s -> {
            holder.schoolAttendanceNumber.setText(s);
            holder.schoolAttendanceNumber.setOnClickListener(v -> {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse("tel:" + s));
                context.startActivity(phoneIntent);
            });
        }, () -> holder.schoolAttendanceNumber.setText(R.string.directory_info_unavaiable));

        dataSource.faxNumber.ifPresentOrElse(s -> {
            holder.schoolFaxNumber.setText(s);
            holder.schoolFaxNumber.setOnClickListener(v -> {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse("tel:" + s));
                context.startActivity(phoneIntent);
            });
        }, () -> holder.schoolFaxNumber.setText(R.string.directory_info_unavaiable));

        if (dataSource == DataSource.DISTRICT)
            holder.schoolWebsite.setText(R.string.title_district_website);
        else
            holder.schoolWebsite.setText(R.string.title_school_website);

        holder.schoolWebsite.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dataSource.websiteURL));
            context.startActivity(browserIntent);
        });
    }

    @Override
    public int getLayoutRes() {
        return R.layout.directory_detail_header;
    }

    @Override
    public DirectoryDetailHeaderViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return new DirectoryDetailHeaderViewHolder(view, adapter);
    }

    static class DirectoryDetailHeaderViewHolder extends FlexibleViewHolder {
        final TextView schoolName, schoolAddress, schoolPhoneNumber, schoolAttendanceNumber, schoolFaxNumber, schoolWebsite;
        final ImageView schoolImage;

        DirectoryDetailHeaderViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            schoolName = (TextView) view.findViewById(R.id.directory_detail_schoolName);
            schoolAddress = (TextView) view.findViewById(R.id.directory_address_textView);
            schoolPhoneNumber = (TextView) view.findViewById(R.id.directory_phoneNumber_textView);
            schoolAttendanceNumber = (TextView) view.findViewById(R.id.directory_attendanceNumber_textView);
            schoolFaxNumber = (TextView) view.findViewById(R.id.directory_faxNumber_textView);
            schoolWebsite = (TextView) view.findViewById(R.id.directory_website_textView);
            schoolImage = (ImageView) view.findViewById(R.id.directory_school_image);
        }
    }
}
