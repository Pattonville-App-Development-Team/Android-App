package org.pattonvillecs.pattonvilleapp.fragments.directory;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        //fix this to check for other extensions being empty as well so it finds the first not empty extension
        if (facultyMember.getExtension1().isEmpty()) {
            holder.extensionButton.setVisibility(View.INVISIBLE);
        } else {
            holder.extensionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String extension = facultyMember.getExtension1();
                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                    //currently not working with pause
                    phoneIntent.setData(Uri.parse("tel:3142138010;" + PhoneNumberUtils.PAUSE + facultyMember.getExtension1()));
                    adapter.getRecyclerView().getContext().startActivity(phoneIntent);
                }
            });
        }


        if (facultyMember.getEmail().isEmpty()) {
            holder.emailButton.setVisibility(View.INVISIBLE);
        } else {
            holder.emailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto:"));
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{facultyMember.getEmail()});
                    adapter.getRecyclerView().getContext().startActivity(emailIntent);
                }
            });
        }
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
