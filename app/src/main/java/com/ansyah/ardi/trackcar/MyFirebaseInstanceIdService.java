package com.ansyah.ardi.trackcar;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

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
