package com.example.integratelibraryauroscholarapp;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;

public class AuroScholarInputModel implements Parcelable {
    String userid;
    String mobileNumber;
    Activity activity;
    String studentClass;
    String regitrationSource="";
    String referralLink="";
    String userPartnerId="";
    String partnerSource;
    String language="en";
    boolean applicationLang=false;
    String partnerLogoUrl="";
    String schoolName="";
    String schoolType="";
    String email="";
    String boardType="";
    String gender="";
    int fragmentContainerUiId;
    String setDeviceToken = "test123";
    String partner_api_key;

    public String getPartner_api_key() {
        return partner_api_key;
    }

    public void setPartner_api_key(String partner_api_key) {
        this.partner_api_key = partner_api_key;
    }

    String partner_unique_id;
    SdkCallBack sdkcallback;
    public SdkCallBack getSdkcallback() {
        return sdkcallback;
    }

    public void setSdkcallback(SdkCallBack sdkcallback) {
        this.sdkcallback = sdkcallback;
    }

    protected AuroScholarInputModel(Parcel in) {
        userid = in.readString();
        partner_unique_id = in.readString();
        mobileNumber = in.readString();
        setDeviceToken = in.readString();
        studentClass = in.readString();
        regitrationSource = in.readString();
        referralLink = in.readString();
        userPartnerId = in.readString();
        partnerSource = in.readString();
        language = in.readString();
        applicationLang = in.readByte() != 0;
        partnerLogoUrl = in.readString();
        schoolName = in.readString();
        schoolType = in.readString();
        email = in.readString();
        boardType = in.readString();
        gender = in.readString();
        partnerName = in.readString();
        fragmentContainerUiId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(partner_unique_id);
        dest.writeString(userid);
        dest.writeString(setDeviceToken);
        dest.writeString(mobileNumber);
        dest.writeString(studentClass);
        dest.writeString(regitrationSource);
        dest.writeString(referralLink);
        dest.writeString(userPartnerId);
        dest.writeString(partnerSource);
        dest.writeString(language);
        dest.writeByte((byte) (applicationLang ? 1 : 0));
        dest.writeString(partnerLogoUrl);
        dest.writeString(schoolName);
        dest.writeString(schoolType);
        dest.writeString(email);
        dest.writeString(boardType);
        dest.writeString(gender);
        dest.writeString(partnerName);
        dest.writeInt(fragmentContainerUiId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AuroScholarInputModel> CREATOR = new Creator<AuroScholarInputModel>() {
        @Override
        public AuroScholarInputModel createFromParcel(Parcel in) {
            return new AuroScholarInputModel(in);
        }

        @Override
        public AuroScholarInputModel[] newArray(int size) {
            return new AuroScholarInputModel[size];
        }
    };

    public String getPartner_unique_id() {
        return partner_unique_id;
    }

    public void setPartner_unique_id(String partner_unique_id) {
        this.partner_unique_id = partner_unique_id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    String partnerName="";




    public String getSchoolType() {
        return schoolType;
    }

    public void setSchoolType(String schoolType) {
        this.schoolType = schoolType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBoardType() {
        return boardType;
    }

    public void setBoardType(String boardType) {
        this.boardType = boardType;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getPartnerLogoUrl() {
        return partnerLogoUrl;
    }

    public void setPartnerLogoUrl(String partnerLogoUrl) {
        this.partnerLogoUrl = partnerLogoUrl;
    }

    public boolean isApplicationLang() {
        return applicationLang;
    }

    public void setApplicationLang(boolean applicationLang) {
        this.applicationLang = applicationLang;
    }

    public String getPartnerSource() {
        return partnerSource;
    }

    public void setPartnerSource(String partnerSource) {
        this.partnerSource = partnerSource;
    }

    public AuroScholarInputModel() {
    }

    public String getReferralLink() {
        return referralLink;
    }

    public void setReferralLink(String referralLink) {
        this.referralLink = referralLink;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public String getRegitrationSource() {
        return regitrationSource;
    }

    public void setRegitrationSource(String regitrationSource) {
        this.regitrationSource = regitrationSource;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public int getFragmentContainerUiId() {
        return fragmentContainerUiId;
    }

    public void setFragmentContainerUiId(int fragmentContainerUiId) {
        this.fragmentContainerUiId = fragmentContainerUiId;
    }

    public String getUserPartnerId() {
        return userPartnerId;
    }

    public void setUserPartnerId(String userPartnerId) {
        this.userPartnerId = userPartnerId;
    }

    public String getLanguage() { return language; }

    public void setLanguage(String language) { this.language = language; }


    public String setDeviceToken(String setDeviceToken) {
        return setDeviceToken;
    }

    public void getSetDeviceToken(String setDeviceToken) {
        this.setDeviceToken = setDeviceToken;
    }
}
