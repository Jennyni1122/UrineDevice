package com.leo.device.model.net;

import com.leo.device.bean.data.Urine;
import com.leo.device.bean.data.User;
import com.leo.device.bean.data.Water;
import com.leo.device.bean.response.BaseResponse;
import io.reactivex.Observable;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

/**
 * @author Leo
 * @date 2019-05-22
 */
public interface Service {

    @GET("api/busi/urineList")
    Observable<BaseResponse<List<Urine>>> getUrineList(@QueryMap Map<String, String> request);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/busi/urineUpload")
    Observable<BaseResponse> uploadUrineList(@Body List<Urine> urineList);

    @GET("api/busi/waterList")
    Observable<BaseResponse<List<Water>>> getWaterList(@QueryMap Map<String, String> request);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/busi/waterUpload")
    Observable<BaseResponse> uploadWaterList(@Body List<Water> waterList);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/user/login")
    Observable<BaseResponse<User>> login(@Body User user);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/user/register")
    Observable<BaseResponse<User>> register(@Body User user);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @PUT("api/user")
    Observable<BaseResponse<User>> update(@Body User user);

    @GET("api/common/login/capture")
    Observable<BaseResponse> getLoginCapture(@Query("mobileNumber") String mobileNumber);

    @GET("api/common/valid/registered")
    Observable<BaseResponse> getMobileRegistered(@Query("mobileNumber") String mobileNumber);

    @GET("api/common/register/capture")
    Observable<BaseResponse> getRegisterCapture(@Query("mobileNumber") String mobileNumber);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/common/saveToken")
    Observable<BaseResponse> saveToken(@Body User user);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/user/validation")
    Observable<BaseResponse<Object>> checkCapture(@Body User user);
}
