package com.george.tcsdemo.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class HitsList {
    @SerializedName("list")
    private ArrayList<Lista> results;

    public ArrayList<Lista> getResults() {
        return results;
    }
}
