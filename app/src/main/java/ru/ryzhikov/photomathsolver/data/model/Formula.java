package ru.ryzhikov.photomathsolver.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Полученный от Mathpix json
 */
public class Formula {

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
        // snake-case не надо, обычный camelCase: mContainsChart, аналогично с остальными полями
        public double contains_chart;

        @SerializedName("contains_diagram")
        @Expose
        public double contains_diagram;

        @SerializedName("contains_graph")
        @Expose
        public double contains_graph;

        @SerializedName("contains_table")
        @Expose
        public double contains_table;

        @SerializedName("is_inverted")
        @Expose
        public double is_inverted;

        @SerializedName("is_not_math")
        @Expose
        public double is_not_math;

        @SerializedName("is_printed")
        @Expose
        public double is_printed;

        @SerializedName("is_blank")
        @Expose
        public double is_blank;
    }

    public static class Position {
        @SerializedName("width")
        @Expose
        public double width;

        @SerializedName("height")
        @Expose
        public double height;

        @SerializedName("top_left_x")
        @Expose
        public double top_left_x;

        @SerializedName("top_left_y")
        @Expose
        public double top_left_y;
    }

    public Formula(String latex, String wolfram) {
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
