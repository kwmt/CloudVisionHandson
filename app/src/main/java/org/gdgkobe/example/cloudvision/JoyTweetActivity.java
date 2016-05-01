package org.gdgkobe.example.cloudvision;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.moshi.Moshi;

import org.gdgkobe.example.cloudvision.api.TweetApi;
import org.gdgkobe.example.cloudvision.api.VisionApi;
import org.gdgkobe.example.cloudvision.entity.FaceAnnotation;
import org.gdgkobe.example.cloudvision.entity.Feature;
import org.gdgkobe.example.cloudvision.entity.Request;
import org.gdgkobe.example.cloudvision.entity.Response;
import org.gdgkobe.example.cloudvision.entity.TweetRequest;
import org.gdgkobe.example.cloudvision.entity.TweetResponse;
import org.gdgkobe.example.cloudvision.util.ImageUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class JoyTweetActivity extends VisionBaseActivity {

    private static final String AZURE_URL = "https://visionapitwitapi.azurewebsites.net";

    @Bind(R.id.screen_name)
    EditText mName;
    @Bind(R.id.comment)
    EditText mComment;
    @Bind(R.id.picture_switch)
    CompoundButton mPictureSwitch;
    @Bind(R.id.tweet_operation_panel)
    View mTweetOperationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joy_tweet);
        ButterKnife.bind(this);
        super.restoreView();
    }

    @OnClick(R.id.upload)
    void callCloudVision() {
        //Imageが選択されていないかどうかのチェック
        if (mBitmap == null) {
            Snackbar.make(mCoordinator, R.string.not_selected_image, Snackbar.LENGTH_LONG).show();
            return;
        }

        //HttpClientの生成
        OkHttpClient client = new OkHttpClient.Builder().
                addInterceptor(new JsonInterceptor()).
                build();
        // RetrofitAdapterの生成
        Retrofit adapter = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(new Moshi.Builder().build()))
                .client(client)
                .build();
        // 非同期処理の実行
        Request request = new Request();
        request.addRequest(mBitmap, new Feature(Feature.FeatureType.FACE_DETECTION));
        adapter.create(VisionApi.class).post(getString(R.string.vision_api), request)
                .enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        if (response.body().getResponses().get(0).getFaceAnnotations().size() <= 0) {
                            Snackbar.make(mCoordinator, R.string.face_annotation_error, Snackbar.LENGTH_LONG).show();
                        } else {
                            showTweetArea(response.body().getResponses().get(0).getFaceAnnotations().get(0));
                        }
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {

                    }
                });
        Snackbar.make(mCoordinator, R.string.vision_api_upload, Snackbar.LENGTH_LONG).show();
    }


    void showTweetArea(FaceAnnotation faceAnnotation) {
        if ((faceAnnotation.getJoyLikelihood() == FaceAnnotation.LikeLihood.LIKELY ||
                faceAnnotation.getJoyLikelihood() == FaceAnnotation.LikeLihood.VERY_LIKELY)) {
            mTweetOperationView.setVisibility(View.VISIBLE);
            Snackbar.make(mCoordinator, R.string.likely, Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(mCoordinator, R.string.not_likely, Snackbar.LENGTH_LONG).show();
        }
    }


    /**
     * Activity表示用Intentを作成する
     *
     * @param context Context
     * @return intent
     */
    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, JoyTweetActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        return intent;
    }

    @OnClick(R.id.tweet)
    void onTweetClick(View view) {
        //HttpClientの生成
        OkHttpClient client = new OkHttpClient.Builder().
                addInterceptor(new JsonInterceptor()).
                build();
        // RetrofitAdapterの生成
        Retrofit adapter = new Retrofit.Builder()
                .baseUrl(AZURE_URL)
                .addConverterFactory(MoshiConverterFactory.create(new Moshi.Builder().build()))
                .client(client)
                .build();
        // 非同期処理の実行
        TweetRequest request = new TweetRequest(
                mName.getText().toString(),
                mComment.getText().toString(),
                mPictureSwitch.isChecked() ?
                        ImageUtil.convertBase64(mBitmap) : "");
        adapter.create(TweetApi.class).post(request)
                .enqueue(new Callback<TweetResponse>() {
                    @Override
                    public void onResponse(Call<TweetResponse> call, retrofit2.Response<TweetResponse> response) {
                        if (response.body().isSuccess()) {
                            Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Snackbar.make(mCoordinator, "Tweet Api Error" + response.message(), Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<TweetResponse> call, Throwable t) {
                        Snackbar.make(mCoordinator, t.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
        Snackbar.make(mCoordinator, R.string.tweet_api_called, Snackbar.LENGTH_LONG).show();
    }
}
