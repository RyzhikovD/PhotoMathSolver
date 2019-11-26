package ru.ryzhikov.photomathsolver.data;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ru.ryzhikov.photomathsolver.data.model.Formula;
import ru.ryzhikov.photomathsolver.data.model.RequestBody;

public interface IPhotoScanService {

//    @FormUrlEncoded
    @Headers("Content-Type:application/json; charset=utf-8")
    @POST("/v3/latex")
    public Call<Formula> scanImage(@Body RequestBody src);


//    public Call<Formula> scanImage(@Query(value = "src", encoded = true) String src);


//    public Call<Formula> scanImage(@Field("formats[]") List<String> formats, @Field("src") String src);
    //    public Call<Formula> getPostWithID(@Body);
}
