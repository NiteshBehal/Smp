package com.simplified.text.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Result implements Serializable {

    public String id;

    @SerializedName("headword")
    public String headword;

    @SerializedName("meaning")
    public String meaning;

    @SerializedName("part_of_speech")
    public String part_of_speech;

    @SerializedName("pronunciations")
    public ArrayList<Pronunciations> pronunciations;

    @SerializedName("example")
    public Example example;
}