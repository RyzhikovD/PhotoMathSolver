package ru.ryzhikov.photomathsolver.data;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.ryzhikov.photomathsolver.data.model.Formula;
import ru.ryzhikov.photomathsolver.data.model.RequestBody;

public class ImageRepository {

    private static final String BASE_URL = "https://api.mathpix.com";
    private static final String[] FORMATS = {"latex_normal", "wolfram"};

    private final IPhotoScanService mPhotoScanService;

    public ImageRepository() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(@NonNull Interceptor.Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .addHeader("content-type", "application/json")
                                .addHeader("app_id", "")    //мой id для Mathpix
                                .addHeader("app_key", "")   //мой ключ
                                .build();
                        return chain.proceed(request);
                    }
                });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build();
        mPhotoScanService = retrofit.create(IPhotoScanService.class);
    }

    @NonNull
    public Formula loadFormula(String src) throws IOException {
        RequestBody requestBody = new RequestBody("data:image/jpeg;base64," + src, FORMATS);
        Call<Formula> listCall = mPhotoScanService.scanImage(requestBody);
        retrofit2.Response<Formula> response = listCall.execute();
        if (response.body() == null || response.errorBody() != null) {
            throw new IOException("Не удалось отсканировать фото");
        }
        return response.body();
    }
}
