package com.example.integratelibraryauroscholarapp;

import com.google.android.gms.common.api.Status;

public interface SdkCallBack {

    void callBack(String message);

    void logOut();

    void commonCallback(Status status, Object o);
}
