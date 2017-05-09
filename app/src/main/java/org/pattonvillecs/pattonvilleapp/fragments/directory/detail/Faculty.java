package org.pattonvillecs.pattonvilleapp.fragments.directory.detail;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.apache.commons.lang3.text.WordUtils;
import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.R;

import java.io.Serializable;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFilterable;
import eu.davidea.viewholders.FlexibleViewHolder;
import me.xdrop.fuzzywuzzy.FuzzySearch;


/**
 * Created by gadsonk on 12/7/16.
 */

public class Faculty extends AbstractFlexibleItem<Faculty.DirectoryViewHolder> implements IFilterable, DirectoryItem<Faculty.DirectoryViewHolder>, Serializable {

    private String mFirstName, mLastName, mPCN, mLongDesc, mLocation, mEmail, mOffice1,
            mExtension1, mOffice2, mExtension2, mOffice3, mExtension3;
    private int mRank;
    private DataSource directoryKey;

    //list of phone numbers and extension, location

    public Faculty(String[] facultyMember) {
        this(facultyMember[0], facultyMember[1], facultyMember[2], facultyMember[3],
                facultyMember[4], facultyMember[5], facultyMember[6], facultyMember[7],
                facultyMember[8], facultyMember[9], facultyMember[10], facultyMember[11]);
    }

    public Faculty(String mFirstName, String mLastName, String mPCN, String mLongDesc, String mLocation,
                   String mEmail, String mOffice1, String mExtension1, String mOffice2,
                   String mExtension2, String mOffice3, String mExtension3) {
        this.mFirstName = mFirstName;
        this.mLastName = mLastName;
        this.mPCN = mPCN;
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
        setDirectoryKey();
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getFormattedFullName() {
        return WordUtils.capitalizeFully(getFirstName() + " " + getLastName());
    }

    public String getLastName() {
        return mLastName;
    }

    public String getPCN() {
        return mPCN;
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

    private void setDirectoryKey() {
        DataSource key;
        switch (mLocation.trim().toUpperCase()) {
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
        if (mPCN.contains("ADMSPR") || mPCN.contains("ADMPRN")) {
            mRank = 0;
        } else if (mPCN.contains("ASCPRN")) {
            mRank = 1;
        } else if (mPCN.contains("ADMAPR")) {
            mRank = 2;
        } else if (mPCN.contains("TCH")) {
            mRank = 3;
        } else if (mPCN.contains("EXSEC1")) {
            mRank = 4;
        } else if (mPCN.contains("EXSEC2")) {
            mRank = 5;
        } else if (mPCN.contains("GUISEC")) {
            mRank = 6;
        } else if (mPCN.contains("CONSEC")) {
            mRank = 7;
        } else if (mPCN.contains("SECRTY")) {
            mRank = 8;
        } else {
            mRank = 9;
        }
    }

    @Override
    public boolean filter(String constraint) {
        constraint = constraint.toLowerCase();
        int nameRatio = FuzzySearch.partialRatio(constraint, getFormattedFullName().toLowerCase());
        int descriptionRatio = FuzzySearch.partialRatio(constraint, getLongDesc().toLowerCase());
        int dataSourceRatio = FuzzySearch.partialRatio(constraint, getDirectoryKey().toString().toLowerCase());
        return nameRatio > 80 || descriptionRatio > 80 || dataSourceRatio > 80;
    }

    @Override
    public void bindViewHolder(final FlexibleAdapter adapter, DirectoryViewHolder holder, int position, List payloads) {
        Context context = adapter.getRecyclerView().getContext();

        holder.nameText.setText(getFormattedFullName());

        holder.longDesText.setText(WordUtils.capitalizeFully(getLongDesc()));

        if (getExtension1().isEmpty()) {
            holder.extensionButton.setVisibility(View.INVISIBLE);
        } else {
            holder.extensionButton.setVisibility(View.VISIBLE);
            holder.extensionButton.setOnClickListener(
                    v -> context.startActivity(CallExtensionActivity.newIntent(context, Faculty.this)));
        }

        if (getEmail().isEmpty()) {
            holder.emailButton.setVisibility(View.INVISIBLE);
        } else {
            holder.emailButton.setVisibility(View.VISIBLE);
            holder.emailButton.setOnClickListener(v -> {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getEmail()});
                context.startActivity(emailIntent);
            });
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Faculty faculty = (Faculty) o;

        return mRank == faculty.mRank
                && (mFirstName != null ? mFirstName.equals(faculty.mFirstName) : faculty.mFirstName == null
                && (mLastName != null ? mLastName.equals(faculty.mLastName) : faculty.mLastName == null
                && (mPCN != null ? mPCN.equals(faculty.mPCN) : faculty.mPCN == null
                && (mLongDesc != null ? mLongDesc.equals(faculty.mLongDesc) : faculty.mLongDesc == null
                && (mLocation != null ? mLocation.equals(faculty.mLocation) : faculty.mLocation == null
                && (mEmail != null ? mEmail.equals(faculty.mEmail) : faculty.mEmail == null
                && (mOffice1 != null ? mOffice1.equals(faculty.mOffice1) : faculty.mOffice1 == null
                && (mExtension1 != null ? mExtension1.equals(faculty.mExtension1) : faculty.mExtension1 == null
                && (mOffice2 != null ? mOffice2.equals(faculty.mOffice2) : faculty.mOffice2 == null
                && (mExtension2 != null ? mExtension2.equals(faculty.mExtension2) : faculty.mExtension2 == null
                && (mOffice3 != null ? mOffice3.equals(faculty.mOffice3) : faculty.mOffice3 == null
                && (mExtension3 != null ? mExtension3.equals(faculty.mExtension3) : faculty.mExtension3 == null
                && directoryKey == faculty.directoryKey))))))))))));

    }

    @Override
    public int hashCode() {
        int result = mFirstName != null ? mFirstName.hashCode() : 0;
        result = 31 * result + (mLastName != null ? mLastName.hashCode() : 0);
        result = 31 * result + (mPCN != null ? mPCN.hashCode() : 0);
        result = 31 * result + (mLongDesc != null ? mLongDesc.hashCode() : 0);
        result = 31 * result + (mLocation != null ? mLocation.hashCode() : 0);
        result = 31 * result + (mEmail != null ? mEmail.hashCode() : 0);
        result = 31 * result + (mOffice1 != null ? mOffice1.hashCode() : 0);
        result = 31 * result + (mExtension1 != null ? mExtension1.hashCode() : 0);
        result = 31 * result + (mOffice2 != null ? mOffice2.hashCode() : 0);
        result = 31 * result + (mExtension2 != null ? mExtension2.hashCode() : 0);
        result = 31 * result + (mOffice3 != null ? mOffice3.hashCode() : 0);
        result = 31 * result + (mExtension3 != null ? mExtension3.hashCode() : 0);
        result = 31 * result + mRank;
        result = 31 * result + (directoryKey != null ? directoryKey.hashCode() : 0);
        return result;
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


