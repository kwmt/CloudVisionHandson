package org.gdgkobe.example.cloudvision.api;

import org.gdgkobe.example.cloudvision.entity.TweetRequest;
import org.gdgkobe.example.cloudvision.entity.TweetResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface TweetApi {
    @Headers({"Content-Type: application/json",
            "ApiKey: 1VU4d7USD2w7THXEqPW2D"})
    @POST("/api/Tweet")
    Call<TweetResponse> post(@Body TweetRequest request);
}
