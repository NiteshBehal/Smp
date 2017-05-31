package com.simplified.text.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.annotations.Ignore;


public class BaseModel implements Serializable {

    @Ignore
    @SerializedName("error")
    public boolean error;
    @Ignore
    @SerializedName("error_msg")
    public String error_msg;





}
