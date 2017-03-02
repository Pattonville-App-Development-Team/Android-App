package org.pattonvillecs.pattonvilleapp.fragments.directory;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.apache.commons.lang.WordUtils;
import org.pattonvillecs.pattonvilleapp.Faculty;
import org.pattonvillecs.pattonvilleapp.R;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;

/**
 * Created by gadsonk on 1/20/17.
 */

public class DirectoryFlexibleItem extends AbstractFlexibleItem<DirectoryViewHolder> {

    private final String TAG = "DirectoryFlexibleItem";
    private final Faculty facultyMember;
    private final Context context;

    public DirectoryFlexibleItem(Context context, Faculty facultyMember) {
        this.context = context;
        this.facultyMember = facultyMember;
    }

    @Override
    public void bindViewHolder(final FlexibleAdapter adapter, DirectoryViewHolder holder, int position, List payloads) {
        holder.nameText.setText(WordUtils.capitalizeFully(String.format("%s %s",
                facultyMember.getFirstName(), facultyMember.getLastName())));

        holder.longDesText.setText(WordUtils.capitalizeFully(facultyMember.getLongDesc()));

        if (!facultyMember.getExtension1().isEmpty()) {
            holder.extensionText.setText("Ext: " + facultyMember.getExtension1());
        }

        holder.emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, facultyMember.getEmail());
                if (facultyMember.getEmail().isEmpty()) {
                    Toast.makeText(context, "No email available", Toast.LENGTH_SHORT).show();
                } else {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto:"));
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{facultyMember.getEmail()});
                    adapter.getRecyclerView().getContext().startActivity(emailIntent);
                }
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
