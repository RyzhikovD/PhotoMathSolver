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
        return image;
    }

    public Formula loadFormula(String path, String base64Photo) throws IOException {
        // получается, что этот класс оперирует сразу на 2 уровнях абстракции:
        // на болле высоком, когда управляет репозиторием, как в этом методе
        // и на более низком, когда в loadImage реализует сам выполняет функции репозитория.
        // Класс должен либо только управлять репозиториями, либо, наоборот, сам всё реализовывать.
        // 2й вариант так себе, поэтому я бы переименовал его в интерактор, считал частью доменного слоя приложения
        // и завёл бы ещё 1 репозиторий, куда положил бы loadImage (либо в FormulasRepository,
        // если сочтёшь возможным)
        return mFormulasRepository.loadFormula(path, base64Photo);
    }

    public List<FormulaDB> loadFormulasFromDB() {
        return mFormulasRepository.loadFormulasFromDB();
    }
}
