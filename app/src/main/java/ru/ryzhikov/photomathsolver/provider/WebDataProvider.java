package ru.ryzhikov.photomathsolver.provider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import ru.ryzhikov.photomathsolver.data.ImageRepository;
import ru.ryzhikov.photomathsolver.data.model.Formula;

public class WebDataProvider {

    private static final String IMAGE_ROOT_URL = "https://chart.googleapis.com/chart?cht=tx&chl=";
    private static final String SIZE_OF_IMAGE_URL_ARGUMENT = "&chs=200";

    private ImageRepository mImageRepository = new ImageRepository();

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

    public Formula loadFormula(String base64Photo) throws IOException {
        return mImageRepository.loadFormula(base64Photo);
    }
}
