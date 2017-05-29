package com.simplified.text.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class MeaningModel implements Serializable,  RealmModel
{
    @Ignore
    @SerializedName("error_code")
    public boolean error_code;

    @Ignore
    @SerializedName("error_msg")
    public String error_msg;

    @PrimaryKey
    @SerializedName("word")
    public String word;

    @SerializedName("results")
    public RealmList<Result> results;

}