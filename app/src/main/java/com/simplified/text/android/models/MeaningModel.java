package com.simplified.text.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class MeaningModel implements Serializable
{
    @SerializedName("error_code")
    public boolean error_code;

    @SerializedName("error_msg")
    public String error_msg;

    public String id;

    @SerializedName("word")
    public String word;

    @SerializedName("results")
    public ArrayList<Result> results;

}