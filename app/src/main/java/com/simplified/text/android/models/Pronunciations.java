package com.simplified.text.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Pronunciations implements Serializable {

    public String id;

    @SerializedName("ipa")
    public String ipa;

    @SerializedName("lang")
    public String lang;

    @SerializedName("url")
    public String url;
}