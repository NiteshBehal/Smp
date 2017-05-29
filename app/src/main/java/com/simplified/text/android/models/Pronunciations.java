package com.simplified.text.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmModel;
import io.realm.annotations.RealmClass;

@RealmClass
public class Pronunciations implements Serializable, RealmModel {
    @SerializedName("ipa")
    public String ipa;

    @SerializedName("lang")
    public String lang;

    @SerializedName("url")
    public String url;
}