package com.simplified.text.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmModel;
import io.realm.annotations.RealmClass;

@RealmClass
    public class Example implements Serializable, RealmModel
    {
        @SerializedName("example")
        public String example;

        @SerializedName("url")
        public String url;
    }