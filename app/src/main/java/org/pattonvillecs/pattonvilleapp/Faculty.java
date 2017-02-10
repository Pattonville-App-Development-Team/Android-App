package org.pattonvillecs.pattonvilleapp;

/**
 * Created by gadsonk on 12/7/16.
 */

public class Faculty {

    private String mFirstName, mLastName, mFullName;
    private String mDepartment;
    private String mEmail;
    private String mExtensionNumber;

    //list of phone numbers and extension, location

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String mFirstName) {
        this.mFirstName = mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String mLastName) {
        this.mLastName = mLastName;
    }

    public String getDepartment() {
        return mDepartment;
    }

    public void setDepartment(String mDepartment) {
        this.mDepartment = mDepartment;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getExtenstionNumber() {
        return mExtensionNumber;
    }

    public void setExtensionNumber(String extension) {
        this.mExtensionNumber = "x" + extension;
    }

    public String getFullName() {
        return mFullName;
    }

    public void setFullName(String fName, String lName) {
        mFullName = String.format("%s %s", fName, lName);
    }

    //dummy data for now
    public Faculty setFaculty() {
        setFullName(getFirstName(), getLastName());
        setDepartment("Department...");
        setEmail("Email");
        return this;
    }
}

