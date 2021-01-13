package com.carista.api;

import android.content.Context;

import com.carista.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {

    private final UpdateApi updateApi;

    private static RetrofitManager retrofitManager;

    public RetrofitManager(Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.api_root))
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();

        updateApi = retrofit.create(UpdateApi.class);
    }

    public static RetrofitManager getInstance(Context context) {
        if (retrofitManager != null)
            return retrofitManager;

        retrofitManager = new RetrofitManager(context);
        return retrofitManager;
    }

    public UpdateApi getUpdateApi() {
        return updateApi;
    }
}
