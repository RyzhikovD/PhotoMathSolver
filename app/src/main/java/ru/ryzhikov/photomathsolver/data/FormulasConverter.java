package ru.ryzhikov.photomathsolver.data;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ru.ryzhikov.photomathsolver.data.model.FormulaData;
import ru.ryzhikov.photomathsolver.data.room.FormulaDB;
import ru.ryzhikov.photomathsolver.domain.model.Formula;

/**
 * Конвертирует список формул из data сущностей в domain
 */
class FormulasConverter {

    @NonNull
    List<Formula> convert(@NonNull List<FormulaDB> formulas) {
        List<Formula> result = new ArrayList<>();
        for (FormulaDB formula : formulas) {
            result.add(new Formula(
                    formula.getLatexFormula(),
                    formula.getWolframFormula(),
                    formula.getPath()
            ));
        }
        return result;
    }

    @NonNull
    Formula convert(@NonNull FormulaDB formula) {
        return new Formula(formula.getLatexFormula(), formula.getWolframFormula(), formula.getPath());
    }

    @NonNull
    Formula convert(@NonNull FormulaData formula, String imagePath) {
        return new Formula(formula.getLatex(), formula.getWolfram(), imagePath);
    }
}
