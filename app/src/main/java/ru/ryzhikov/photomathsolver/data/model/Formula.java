package ru.ryzhikov.photomathsolver.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Formula {

    @SerializedName("latex")
    @Expose
    private String mLatex;

    public String getLatex() {
        return mLatex;
    }
}
