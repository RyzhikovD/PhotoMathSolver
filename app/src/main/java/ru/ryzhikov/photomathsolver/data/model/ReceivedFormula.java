package ru.ryzhikov.photomathsolver.data.model;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class ReceivedFormula {

    private List<String> mDetectionList;
    private Map<String, Integer> mDetectionMap;
    private String mLatexNormal;

    @JsonCreator
    public ReceivedFormula(
            @JsonProperty("detection_list") List<String> detectionList,
            @JsonProperty("detection_map") Map<String, Integer> detectionMap,
            @JsonProperty("latex_normal") String latexNormal) {
        mDetectionList = detectionList;
        mDetectionMap = detectionMap;
        mLatexNormal = latexNormal;
    }

    @NonNull
    @Override
    public String toString() {
        return "ReceivedFormula{" +
                "mDetectionList=" + mDetectionList +
                ", mDetectionMap=" + mDetectionMap +
                ", mLatexNormal='" + mLatexNormal + '\'' +
                '}';
    }

    public List<String> getDetectionList() {
        return mDetectionList;
    }

    public Map<String, Integer> getDetectionMap() {
        return mDetectionMap;
    }

    public String getLatexNormal() {
        return mLatexNormal;
    }
}
