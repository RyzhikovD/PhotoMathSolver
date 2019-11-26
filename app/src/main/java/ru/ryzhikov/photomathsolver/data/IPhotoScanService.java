package ru.ryzhikov.photomathsolver.data;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import ru.ryzhikov.photomathsolver.data.model.Formula;
import ru.ryzhikov.photomathsolver.data.model.RequestBody;

public interface IPhotoScanService {

    @Headers("Content-Type:application/json; charset=utf-8")
    @POST("/v3/latex")
    Call<Formula> scanImage(@Body RequestBody src);
}
