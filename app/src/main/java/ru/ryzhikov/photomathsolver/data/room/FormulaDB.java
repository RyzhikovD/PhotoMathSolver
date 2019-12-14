package ru.ryzhikov.photomathsolver.data.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class FormulaDB {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;

    @ColumnInfo(name = "wolfram_formula")
    private String mWolframFormula;

    @ColumnInfo(name = "latex_formula")
    private String mLatexFormula;

    @ColumnInfo(name = "path")
    private String mPath;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getWolframFormula() {
        return mWolframFormula;
    }

    public void setWolframFormula(String wolframFormula) {
        mWolframFormula = wolframFormula;
    }

    public String getLatexFormula() {
        return mLatexFormula;
    }

    public void setLatexFormula(String latexFormula) {
        mLatexFormula = latexFormula;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }
}
