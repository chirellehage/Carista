package com.carista.api;

import com.carista.api.models.UpdateResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UpdateApi {

    @GET("update/{version}")
    Call<UpdateResponse> getUpdates(@Path("version") String version);
}
