package org.pattonvillecs.pattonvilleapp;

/**
 * Created by gadsonk on 12/7/16.
 */

public class Faculty {

    private String mFirstName, mLastName, mLongDesc, mLocation, mEmail, mOffice1,
            mExtension1, mOffice2, mExtension2, mOffice3, mExtension3;
    private int mRank;

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
        mRank = getRank();
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

    private int getRank() {
        return 0;
    }
}


