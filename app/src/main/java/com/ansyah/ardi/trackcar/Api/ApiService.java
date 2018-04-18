package com.ansyah.ardi.trackcar.Api;

import com.ansyah.ardi.trackcar.Model.DriverModel;
import com.ansyah.ardi.trackcar.Model.LocationModel;
import com.ansyah.ardi.trackcar.Model.RelayModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by ardi on 18/04/18.
 */

public interface ApiService {
    @GET("api/mobil/driver/getone/5ab851b9b397a927081303b5")
    Call<List<DriverModel>> getDriver();

    @GET("api/mobil/relay/getone/5ab851b9b397a927081303b5")
    Call<RelayModel> getRelay();

    @GET("api/mobil/koordinat/getone/5ab851b9b397a927081303b5")
    Call<LocationModel> getLocation();
}
