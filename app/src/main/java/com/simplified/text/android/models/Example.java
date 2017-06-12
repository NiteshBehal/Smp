package com.simplified.text.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

    public class Example implements Serializable
    {
        public String id;

        @SerializedName("example")
        public String example;

        @SerializedName("url")
        public String url;
    }