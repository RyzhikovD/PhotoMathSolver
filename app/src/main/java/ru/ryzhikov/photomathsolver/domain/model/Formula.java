package ru.ryzhikov.photomathsolver.domain.model;

public class Formula {
    private String mLatex;
    private String mWolfram;
    private String mImagePath;

    public Formula(String latex, String wolfram, String imagePath) {
        mLatex = latex;
        mWolfram = wolfram;
        mImagePath = imagePath;
    }

    public String getLatex() {
        return mLatex;
    }

    public String getWolfram() {
        return mWolfram;
    }

    public String getImagePath() {
        return mImagePath;
    }
}
