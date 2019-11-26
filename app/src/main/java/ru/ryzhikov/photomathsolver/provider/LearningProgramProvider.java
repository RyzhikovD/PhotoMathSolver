package ru.ryzhikov.photomathsolver.provider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import ru.ryzhikov.photomathsolver.data.ImageRepository;
import ru.ryzhikov.photomathsolver.data.model.Formula;

public class LearningProgramProvider {

    private static final String IMAGE_ROOT_URL = "https://chart.googleapis.com/chart?cht=tx&chl=";
    private static final String SIZE_OF_IMAGE_URL_ARGUMENT = "&chs=200";
    private Bitmap mImage;
    private Formula mFormula;

    private ImageRepository mImageRepository = new ImageRepository();

    @Nullable
    public Bitmap loadImage(String formula) {
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
        mImage = image != null ? image.copy(image.getConfig(), true) : null;
        return image;
    }

    public Formula loadFormula(Bitmap bitmap) throws IOException {
        mFormula = mImageRepository.loadCurrencies(bitmapToBase64(bitmap));
        return mFormula;
    }

    public Bitmap provideImage() {
        return mImage;
    }

    private static String bitmapToBase64(Bitmap image) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, os);
        return Base64.encodeToString(os.toByteArray(), Base64.DEFAULT);
    }

    public Formula provideFormula() {
        return mFormula;
    }
}
