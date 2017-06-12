package com.simplified.text.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class BaseModel implements Serializable {

    @SerializedName("error")
    public boolean error;

    @SerializedName("error_msg")
    public String error_msg;





}
