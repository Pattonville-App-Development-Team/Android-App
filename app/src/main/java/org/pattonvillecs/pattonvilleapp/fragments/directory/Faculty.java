package org.pattonvillecs.pattonvilleapp.fragments.directory;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.apache.commons.lang.WordUtils;
import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.R;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFilterable;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by gadsonk on 12/7/16.
 */

public class Faculty extends AbstractFlexibleItem<Faculty.DirectoryViewHolder> implements IFilterable {

    private String mFirstName, mLastName, mLongDesc, mLocation, mEmail, mOffice1,
            mExtension1, mOffice2, mExtension2, mOffice3, mExtension3;
    private int mRank;
    private DataSource directoryKey;

    //list of phone numbers and extension, location

    public Faculty(String[] facultyMember) {
        this(facultyMember[0], facultyMember[1], facultyMember[2], facultyMember[3],
                facultyMember[4], facultyMember[5], facultyMember[6], facultyMember[7],
                facultyMember[8], facultyMember[9], facultyMember[10]);
    }

    public Faculty(String mFirstName, String mLastName, String mLongDesc, String mLocation,
                   String mEmail, String mOffice1, String mExtension1, String mOffice2,
                   String mExtension2, String mOffice3, String mExtension3) {
        this.mFirstName = mFirstName;
        this.mLastName = mLastName;
        this.mLongDesc = mLongDesc;
        this.mLocation = mLocation;
        this.mEmail = mEmail;
        this.mOffice1 = mOffice1;
        this.mExtension1 = mExtension1;
        this.mOffice2 = mOffice2;
        this.mExtension2 = mExtension2;
        this.mOffice3 = mOffice3;
        this.mExtension3 = mExtension3;
        setRank();
        setDirectoryKey(mLocation);
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getLongDesc() {
        return mLongDesc;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getLocation() {
        return mLocation;
    }

    public String getOffice1() {
        return mOffice1;
    }

    public String getExtension1() {
        return mExtension1;
    }

    public String getOffice2() {
        return mOffice2;
    }

    public String getExtension2() {
        return mExtension2;
    }

    public String getOffice3() {
        return mOffice3;
    }

    public String getExtension3() {
        return mExtension3;
    }

    public int getRank() {
        return mRank;
    }

    public DataSource getDirectoryKey() {
        return directoryKey;
    }

    private void setDirectoryKey(String location) {
        DataSource key = null;
        switch (location.trim().toUpperCase()) {
            case "BRIDGEWAY ELEMENTARY":
                key = DataSource.BRIDGEWAY_ELEMENTARY;
                break;
            case "ROBERT DRUMMOND ELEMENTARY":
                key = DataSource.DRUMMOND_ELEMENTARY;
                break;
            case "HOLMAN MIDDLE SCHOOL":
                key = DataSource.HOLMAN_MIDDLE_SCHOOL;
                break;
            case "PATTONVILLE HEIGHTS":
                key = DataSource.HEIGHTS_MIDDLE_SCHOOL;
                break;
            case "PARKWOOD ELEMENTARY":
                key = DataSource.PARKWOOD_ELEMENTARY;
                break;
            case "ROSE ACRES ELEMENTARY":
                key = DataSource.ROSE_ACRES_ELEMENTARY;
                break;
            case "REMINGTON TRADITIONAL":
                key = DataSource.REMINGTON_TRADITIONAL_SCHOOL;
                break;
            case "PATTONVILLE HIGH SCHOOL":
            case "POSITIVE SCHOOL":
                key = DataSource.HIGH_SCHOOL;
                break;
            case "WILLOW BROOK ELEMENTARY":
                key = DataSource.WILLOW_BROOK_ELEMENTARY;
                break;
            case "LEARNING CENTER":
            default:
                key = DataSource.DISTRICT;
        }
        directoryKey = key;
    }

    public void setRank() {
        if (mLongDesc.contains("PRINCIPAL")) {
            if (mLongDesc.contains("ASSOCIATE PRINCIPAL")) {
                mRank = 1;
            } else if (mLongDesc.contains("ASSISTANT PRINCIPAL")) {
                mRank = 2;
            } else {
                mRank = 0;
            }
        } else if (org.apache.commons.lang3.StringUtils.containsIgnoreCase(mLongDesc, "TEA") ||
                mLongDesc.contains("TCHR")) {
            mRank = 3;
        } else if (mLongDesc.contains("SECRETARY")) {
            if (mLongDesc.contains("EXECUTIVE SECRETARY 1")) {
                mRank = 4;
            } else if (mLongDesc.contains("EXECUTIVE SECRETARY 2")) {
                mRank = 5;
            } else {
                mRank = 6;
            }
        } else {
            mRank = 7;
        }
    }

    @Override
    public boolean filter(String constraint) {

        return mLastName.toLowerCase().contains(constraint.toLowerCase()) ||
                mFirstName.toLowerCase().contains(constraint.toLowerCase()) ||
                mLongDesc.toLowerCase().contains(constraint.toLowerCase());
    }

    @Override
    public void bindViewHolder(final FlexibleAdapter adapter, DirectoryViewHolder holder, int position, List payloads) {
        holder.nameText.setText(WordUtils.capitalizeFully(String.format("%s %s",
                getFirstName(), getLastName())));

        holder.longDesText.setText(WordUtils.capitalizeFully(getLongDesc()));

        if (getExtension1().isEmpty()) {
            holder.extensionButton.setVisibility(View.INVISIBLE);
        } else {
            holder.extensionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String extension = getExtension1();
                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                    //currently not working with pause
                    phoneIntent.setData(Uri.parse("tel:3142138010;" + PhoneNumberUtils.PAUSE + getExtension1()));
                    adapter.getRecyclerView().getContext().startActivity(phoneIntent);
                }
            });
        }

        if (getEmail().isEmpty()) {
            holder.emailButton.setVisibility(View.INVISIBLE);
        } else {
            holder.emailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto:"));
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getEmail()});
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

    public class DirectoryViewHolder extends FlexibleViewHolder {
        private static final String TAG = "DirectoryViewHolder";
        final RecyclerView facultyView;
        final TextView nameText, longDesText;
        final ImageButton emailButton, extensionButton;

        public DirectoryViewHolder(View view, FlexibleAdapter adapter) {
            this(view, adapter, false);
        }

        public DirectoryViewHolder(View view, FlexibleAdapter adapter, boolean stickyHeader) {
            super(view, adapter, stickyHeader);
            nameText = (TextView) view.findViewById(R.id.directory_facultyName_textView);
            longDesText = (TextView) view.findViewById(R.id.directory_facultyDepartment_textView);
            emailButton = (ImageButton) view.findViewById(R.id.directory_facultyEmail_imageButton);
            extensionButton = (ImageButton) view.findViewById(R.id.directory_facultyExtension_imageButton);
            facultyView = (RecyclerView) view.findViewById(R.id.directory_detail_recyclerView);
        }

        @Override
        public void onClick(View view) {
            super.onClick(view);
            Log.i(TAG, "Clicked view: " + view);
        }
    }
}


