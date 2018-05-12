package com.ansyah.ardi.trackcar;

import android.util.Log;
import android.widget.Toast;

import com.ansyah.ardi.trackcar.Api.ApiService;
import com.ansyah.ardi.trackcar.Config.Aplikasi;
import com.ansyah.ardi.trackcar.Model.RelayModel;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ardi on 05/05/18.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String tokenRefresh = FirebaseInstanceId.getInstance().getToken();
        Log.d("TOKEN:", tokenRefresh);
    }
}
