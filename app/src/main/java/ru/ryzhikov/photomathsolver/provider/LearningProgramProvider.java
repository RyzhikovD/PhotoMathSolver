package ru.ryzhikov.photomathsolver.provider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ru.ryzhikov.photomathsolver.data.ImageRepository;
import ru.ryzhikov.photomathsolver.data.model.Formula;

public class LearningProgramProvider {

    private static final String IMAGE_ROOT_URL = "https://chart.googleapis.com/chart?cht=tx&chl=";
    private static final String SIZE_OF_IMAGE_URL_ARGUMENT = "&chs=100";
    private static final String FORMULA_ROOT_URL = "https://api.mathpix.com/v3/latex";
    private static final List<String> FORMATS = Collections.singletonList("wolfram");
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

//    @Nullable
//    public ReceivedFormula getFormula(Uri imageUri) {
//        if (mFormula != null) {
//            return mFormula;
//        }
//        InputStream is = null;
//        try {
//            final URL url = new URL(FORMULA_ROOT_URL);
//
//            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
//            httpConnection.setRequestProperty("content-type", "application/json");
//            httpConnection.setRequestProperty("app_id", "ryzhikov_dmvl_gmail_com");
//            httpConnection.setRequestProperty("app_key", "28d1ed4c4d6458420a3f");
//            httpConnection.setRequestProperty("src", imageUri.getPath());
////            httpConnection.setRequestProperty("formats", FORMATS);
//
//            is = httpConnection.getInputStream();
//            ObjectMapper mapper = new ObjectMapper();
//            return mapper.readValue(is, ReceivedFormula.class);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (is != null) {
//                try {
//                    is.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return null;
//    }

    public Formula loadFormula(Bitmap bitmap) throws IOException {
        mFormula = mImageRepository.loadCurrencies(FORMATS, bitmapToBase64(bitmap));
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
