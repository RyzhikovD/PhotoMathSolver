package ru.ryzhikov.photomathsolver.provider;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import ru.ryzhikov.photomathsolver.data.FormulasRepository;
import ru.ryzhikov.photomathsolver.data.model.Formula;
import ru.ryzhikov.photomathsolver.data.room.FormulaDB;

public class WebDataProvider {

    private static final String IMAGE_ROOT_URL = "https://chart.googleapis.com/chart?cht=tx&chl=";
    private static final String SIZE_OF_IMAGE_URL_ARGUMENT = "&chs=200";

    private FormulasRepository mFormulasRepository;

    public WebDataProvider(Context context) {
        mFormulasRepository = new FormulasRepository(context);
    }

    /**
     * Загружает картинку для latex формулы
     *
     * @param formula - latex формула
     * @return
     */
    @Nullable
    public static Bitmap loadImage(String formula) {
        Bitmap image = null;
        InputStream is = null;

        try {
            is = new URL(IMAGE_ROOT_URL + formula + SIZE_OF_IMAGE_URL_ARGUMENT).openStream();
            image = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
//        mImage = image != null ? image.copy(image.getConfig(), true) : null;
        return image;
    }

    public Formula loadFormula(String path, String base64Photo) throws IOException {
        return mFormulasRepository.loadFormula(path, base64Photo);
    }

    public List<FormulaDB> loadFormulasFromDB() {
        List<FormulaDB> formulasDB= mFormulasRepository.loadFormulasFromDB();
//        List<Formula> formulas = new ArrayList<>(formulasDB.size());
//        for (FormulaDB formulaDB : formulasDB) {
//            formulas.add(new Formula(formulaDB.getLatexFormula(), formulaDB.getWolframFormula()));
//        }
        return formulasDB;
    }
}
