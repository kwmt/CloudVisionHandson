package org.gdgkobe.example.cloudvision.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.squareup.moshi.Moshi;

import org.gdgkobe.example.cloudvision.JsonInterceptor;
import org.gdgkobe.example.cloudvision.R;
import org.gdgkobe.example.cloudvision.api.VisionApi;
import org.gdgkobe.example.cloudvision.entity.Feature;
import org.gdgkobe.example.cloudvision.entity.Request;
import org.gdgkobe.example.cloudvision.entity.Response;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

/**
 * Created by kwmt on 2016/04/30.
 */
public class CloudVisionAPI {

    private static final String API_BASE_URL = "https://vision.googleapis.com/";
    private static final String TAG = CloudVisionAPI.class.getSimpleName();

    private Context mContext;
    private boolean mIsRequest = false;


    public CloudVisionAPI(Context context) {
        mContext = context;
    }

    OkHttpClient mClient = new OkHttpClient.Builder()
            .addInterceptor(new JsonInterceptor())
            .build();

    Retrofit mAdapter = new Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(new Moshi.Builder().build()))
            .client(mClient)
            .build();


    private void request(Feature.FeatureType featureType, Bitmap bitmap, final ApiCallback callback) {
        Request request = new Request();
        request.addRequest(bitmap, new Feature(featureType));
        mAdapter.create(VisionApi.class).post(mContext.getString(R.string.vision_api), request)
                .enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        mIsRequest = false;
                        if (callback != null) {
                            callback.onSuccess(call, response);
                        }
                    }
                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                        mIsRequest = false;
                        if(callback != null){
                            callback.onFailure(call, t);
                        }
                    }
                });
    }

    public void requestLabelDetection(Bitmap bitmap, final ApiCallback callback) {
        Log.d(TAG, "requestLabelDetection: " + mIsRequest);
        if (mIsRequest){
            Log.d(TAG, "リクエスト中です");
            return;
        }
        mIsRequest = true;
        request(Feature.FeatureType.LABEL_DETECTION, bitmap, callback);
    }
    public void requestFaceDetection(Bitmap bitmap, final ApiCallback callback) {
        Log.d(TAG, "requestLabelDetection: " + mIsRequest);
        if (mIsRequest){
            Log.d(TAG, "リクエスト中です");
            return;
        }
        mIsRequest = true;
        request(Feature.FeatureType.FACE_DETECTION, bitmap, callback);
    }
    public void requestTextDetection(Bitmap bitmap, final ApiCallback callback) {
        Log.d(TAG, "requestTextDetection: " + mIsRequest);
        if (mIsRequest){
            Log.d(TAG, "リクエスト中です");
            return;
        }
        mIsRequest = true;
        request(Feature.FeatureType.TEXT_DETECTION, bitmap, callback);
    }
    public void requestSafeSearchDetection(Bitmap bitmap, final ApiCallback callback) {
        Log.d(TAG, "requestSafeSearchDetection: " + mIsRequest);
        if (mIsRequest){
            Log.d(TAG, "リクエスト中です");
            return;
        }
        mIsRequest = true;
        request(Feature.FeatureType.SAFE_SEARCH_DETECTION, bitmap, callback);
    }


}
