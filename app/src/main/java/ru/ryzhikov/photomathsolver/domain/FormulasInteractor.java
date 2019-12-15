package ru.ryzhikov.photomathsolver.domain;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.List;

import ru.ryzhikov.photomathsolver.domain.model.Formula;

public class FormulasInteractor {

    private IFormulasRepository mFormulasRepository;

    public FormulasInteractor(IFormulasRepository formulasRepository) {
        mFormulasRepository = formulasRepository;
    }

    /**
     * Загружает картинку для latex формулы
     *
     * @param latexFormula - latex формула
     * @return
     */
    @Nullable
    public Bitmap loadImage(String latexFormula) {
        return mFormulasRepository.loadImageForFormula(latexFormula);
    }

    public Formula loadFormula(String path, String base64Photo) throws IOException {
        return mFormulasRepository.loadFormula(path, base64Photo);
    }

    public List<Formula> loadFormulasFromDB() {
        return mFormulasRepository.loadFormulasFromDB();
    }
}
