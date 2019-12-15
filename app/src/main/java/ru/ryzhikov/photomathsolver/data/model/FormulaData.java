package ru.ryzhikov.photomathsolver.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Полученный от Mathpix json
 */
public class FormulaData {

    @SerializedName("latex_normal")
    @Expose
    private String mLatex;

    private DetectionMap mDetectionMap;

//    @SerializedName("error")
//    @Expose
//    private String mError;

    @SerializedName("detection_list")
    @Expose
    private String[] mLatexList;

    @SerializedName("latex_confidence")
    @Expose
    private double mLatexConfidence;

    @SerializedName("latex_confidence_rate")
    @Expose
    private double mLatexConfidenceRate;

    private Position mPosition;

    @SerializedName("wolfram")
    @Expose
    private String mWolfram;

    public static class DetectionMap {
        @SerializedName("contains_chart")
        @Expose
        public double mCcontainsChart;

        @SerializedName("contains_diagram")
        @Expose
        public double mContainsDiagram;

        @SerializedName("contains_graph")
        @Expose
        public double mContainsGraph;

        @SerializedName("contains_table")
        @Expose
        public double mContainsTable;

        @SerializedName("is_inverted")
        @Expose
        public double mIsInverted;

        @SerializedName("is_not_math")
        @Expose
        public double mIsNotMath;

        @SerializedName("is_printed")
        @Expose
        public double mIsPrinted;

        @SerializedName("is_blank")
        @Expose
        public double mIsBlank;
    }

    public static class Position {
        @SerializedName("width")
        @Expose
        public double mWidth;

        @SerializedName("height")
        @Expose
        public double mHeight;

        @SerializedName("top_left_x")
        @Expose
        public double mTopLeftX;

        @SerializedName("top_left_y")
        @Expose
        public double mTopLeftY;
    }

    public FormulaData(String latex, String wolfram) {
        mLatex = latex;
        mWolfram = wolfram;
    }

    public String getLatex() {
        return mLatex;
    }

    public String getWolfram() {
        return mWolfram;
    }
}
