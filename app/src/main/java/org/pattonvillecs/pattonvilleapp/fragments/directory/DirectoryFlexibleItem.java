package org.pattonvillecs.pattonvilleapp.fragments.directory;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.lang3.tuple.Pair;
import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.Faculty;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.events.EventViewHolder;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;

/**
 * Created by gadsonk on 1/20/17.
 */

public class DirectoryFlexibleItem extends AbstractFlexibleItem<DirectoryViewHolder> {

    private final Faculty facultyMember;

    public DirectoryFlexibleItem(Faculty facultyMember) {
        this.facultyMember = facultyMember;
    }

    @Override
    public void bindViewHolder(final FlexibleAdapter adapter, DirectoryViewHolder holder, int position, List payloads) {
        holder.nameText.setText(facultyMember.getName());

        holder.departmentText.setText(facultyMember.getDepartment());

        holder.emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facultyMember.setEmail("exampleemail@psdr3.org");
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{facultyMember.getEmail()});
                adapter.getRecyclerView().getContext().startActivity(emailIntent);
            }
        });
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.list_faculty_item;
    }

    @Override
    public DirectoryViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new DirectoryViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }
}
