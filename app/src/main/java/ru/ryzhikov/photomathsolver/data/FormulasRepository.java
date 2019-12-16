package ru.ryzhikov.photomathsolver.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.ryzhikov.photomathsolver.data.model.FormulaData;
import ru.ryzhikov.photomathsolver.data.model.RequestBody;
import ru.ryzhikov.photomathsolver.data.room.FormulaDB;
import ru.ryzhikov.photomathsolver.data.room.FormulasDatabase;
import ru.ryzhikov.photomathsolver.domain.IFormulasRepository;
import ru.ryzhikov.photomathsolver.domain.model.Formula;

public class FormulasRepository implements IFormulasRepository {

    private static final String BASE_URL = "https://api.mathpix.com";
    private static final String[] FORMATS = {"latex_normal", "wolfram"};
    private static final String IMAGE_ROOT_URL = "https://chart.googleapis.com/chart?cht=tx&chl=";
    private static final String SIZE_OF_IMAGE_URL_ARGUMENT = "&chs=200";
    private static final String BASE64_PREFIX = "data:image/jpeg;base64,";

    private final IPhotoScanService mPhotoScanService;
    private final FormulasDatabase mDatabase;
    private final FormulasConverter mFormulasConverter = new FormulasConverter();

    public FormulasRepository(Context context) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .addHeader("content-type", "application/json")
                            .addHeader("app_id", "ryzhikov_dmvl_gmail_com")
                            .addHeader("app_key", "28d1ed4c4d6458420a3f")
                            .build();
                    return chain.proceed(request);
                });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build();
        mPhotoScanService = retrofit.create(IPhotoScanService.class);

        RoomDatabase.Builder<FormulasDatabase> builder =
                Room.databaseBuilder(context, FormulasDatabase.class, "formulas");
        mDatabase = builder.build();
    }

    @NonNull
    public Formula loadFormula(final String path, String src) throws IOException {
        FormulaDB formulaDB = mDatabase.getFormulasDao().getFormulaByPath(path);
        if (formulaDB != null) {
            return mFormulasConverter.convert(formulaDB);
        } else {
            final FormulaData formulaData = loadFormulaViaRetrofit(src);
            new Thread(() -> {
                final FormulaDB newFormula = new FormulaDB();
                newFormula.setLatexFormula(formulaData.getLatex());
                newFormula.setWolframFormula(formulaData.getWolfram());
                newFormula.setPath(path);
                mDatabase.getFormulasDao().addFormula(newFormula);
            }).start();
            return mFormulasConverter.convert(formulaData, path);
        }
    }

    public List<Formula> loadFormulasFromDB() {
        return mFormulasConverter.convert(mDatabase.getFormulasDao().getAllFormulas());
    }

    @Nullable
    public Bitmap loadImageForFormula(String latexFormula) throws IOException {
        InputStream is = new URL(IMAGE_ROOT_URL + latexFormula + SIZE_OF_IMAGE_URL_ARGUMENT)
                .openStream();
        Bitmap image = BitmapFactory.decodeStream(is);
        if (is != null) {
            is.close();
        }
        return image;
    }

    private FormulaData loadFormulaViaRetrofit(String src) throws IOException {
        RequestBody requestBody = new RequestBody(BASE64_PREFIX + src, FORMATS);
        Call<FormulaData> listCall = mPhotoScanService.scanImage(requestBody);
        retrofit2.Response<FormulaData> response = listCall.execute();
        if (response.body() == null) {
            throw new IOException("Не удалось отсканировать фото");
        }
        return response.body();
    }
}
