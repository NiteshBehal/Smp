package com.simplified.text.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.annotations.RealmClass;

@RealmClass
public class Result implements Serializable, RealmModel {
    @SerializedName("meaning")
    public String meaning;

    @SerializedName("part_of_speech")
    public String part_of_speech;

    @SerializedName("pronunciations")
    public RealmList<Pronunciations> pronunciations;

    @SerializedName("example")
    public Example example;
}