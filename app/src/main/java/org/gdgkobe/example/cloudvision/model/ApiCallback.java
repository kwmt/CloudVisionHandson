package org.gdgkobe.example.cloudvision.model;

import org.gdgkobe.example.cloudvision.entity.Response;

import retrofit2.Call;

public interface ApiCallback {

    void onSuccess(Call<Response> call, retrofit2.Response<Response> response);
    void onFailure(Call<Response> call, Throwable t);
}
