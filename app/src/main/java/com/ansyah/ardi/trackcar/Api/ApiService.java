package com.ansyah.ardi.trackcar.Api;

import com.ansyah.ardi.trackcar.Model.DriverModel;
import com.ansyah.ardi.trackcar.Model.DriversModel;
import com.ansyah.ardi.trackcar.Model.KoordinatModel;
import com.ansyah.ardi.trackcar.Model.LocationModel;
import com.ansyah.ardi.trackcar.Model.LogsModel;
import com.ansyah.ardi.trackcar.Model.RelayModel;
import com.ansyah.ardi.trackcar.Model.UserLoginModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by ardi on 18/04/18.
 */

public interface ApiService {
    @GET("api/mobil/driver/getone/{idmobil}")
    Call<List<DriverModel>> getDriver(@Path("idmobil") String idmobil);

    @GET("api/mobil/driver/getall/{idmobil}")
    Call<DriversModel> getDrivers(@Path("idmobil") String idmobil);

    @GET("api/mobil/koordinat/getall/{idmobil}")
    Call<KoordinatModel> getAllKoordinat(@Path("idmobil") String idmobil);

    @GET("api/mobil/relay/getone/{idmobil}")
    Call<RelayModel> getRelay(@Path("idmobil") String idmobil);

    @GET("api/mobil/koordinat/getone/{idmobil}")
    Call<LocationModel> getLocation(@Path("idmobil") String idmobil);

    @GET("api/mobil/log/desc/{idmobil}")
    Call<ArrayList<LogsModel>> getLogsData(@Path("idmobil") String idmobil);

    @FormUrlEncoded
    @PUT("api/mobil/token/{idmobil}")
    Call<String> setTokenMobil(@Field("tokenFirebase") String token, @Path("idmobil") String idmobil);

    @FormUrlEncoded
    @POST("api/mobil/user/login")
    Call<UserLoginModel> setUserLogin(@Field("email") String email, @Field("password") String password);

}
