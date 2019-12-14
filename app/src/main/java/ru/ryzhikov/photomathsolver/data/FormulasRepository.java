package ru.ryzhikov.photomathsolver.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.ryzhikov.photomathsolver.data.model.Formula;
import ru.ryzhikov.photomathsolver.data.model.RequestBody;
import ru.ryzhikov.photomathsolver.data.room.FormulaDB;
import ru.ryzhikov.photomathsolver.data.room.FormulasDatabase;

public class FormulasRepository {

    private static final String BASE_URL = "https://api.mathpix.com";
    private static final String[] FORMATS = {"latex_normal", "wolfram"};

    private final IPhotoScanService mPhotoScanService;

    private FormulasDatabase mDatabase;
    private LiveData<List<FormulaDB>> mLiveData;

    public FormulasRepository(Context context) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .addHeader("content-type", "application/json")
                            .addHeader("app_id", "ryzhikov_dmvl_gmail_com")    //мой id для Mathpix
                            .addHeader("app_key", "28d1ed4c4d6458420a3f")   //мой ключ
                            .build();
                    return chain.proceed(request);
                });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build();
        mPhotoScanService = retrofit.create(IPhotoScanService.class);

        RoomDatabase.Builder<FormulasDatabase> builder = Room.databaseBuilder(context, FormulasDatabase.class, "formulas");
        mDatabase = builder.build();
    }

    @NonNull
    public Formula loadFormula(final String path, String src) throws IOException {
        FormulaDB formulaDB = mDatabase.getFormulasDao().getFormulaByPath(path);
        if (formulaDB != null) {
            Log.d("Repository", "loadFormulaViaDB() called");
            return new Formula(formulaDB.getLatexFormula(), formulaDB.getWolframFormula());
        } else {
            final Formula formula = loadFormulaViaRetrofit(src);
            new Thread(() -> {
                final FormulaDB newFormula = new FormulaDB();
                newFormula.setLatexFormula(formula.getLatex());
                newFormula.setWolframFormula(formula.getWolfram());
                newFormula.setPath(path);
                mDatabase.getFormulasDao().addFormula(newFormula);
            }).start();
            return formula;
        }
    }

    public List<FormulaDB> loadFormulasFromDB() {
        return mDatabase.getFormulasDao().getAllFormulas();
    }

    private Formula loadFormulaViaRetrofit(String src) throws IOException {

        Log.d("Repository", "loadFormulaViaRetrofit() called");

        RequestBody requestBody = new RequestBody("data:image/jpeg;base64," + src, FORMATS);
        Call<Formula> listCall = mPhotoScanService.scanImage(requestBody);
        retrofit2.Response<Formula> response = listCall.execute();
        if (response.body() == null || response.errorBody() != null) {
            throw new IOException("Не удалось отсканировать фото");
        }
        return response.body();
    }


}
