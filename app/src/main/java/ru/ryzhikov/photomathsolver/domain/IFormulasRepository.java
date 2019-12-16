package ru.ryzhikov.photomathsolver.domain;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.List;

import ru.ryzhikov.photomathsolver.domain.model.Formula;

public interface IFormulasRepository {

    @NonNull
    Formula loadFormula(final String path, String src) throws IOException;

    List<Formula> loadFormulasFromDB();

    @Nullable
    Bitmap loadImageForFormula(String latexFormula) throws IOException;
}
