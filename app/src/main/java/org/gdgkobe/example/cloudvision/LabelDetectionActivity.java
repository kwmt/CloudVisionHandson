package org.gdgkobe.example.cloudvision;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.TextView;

import org.gdgkobe.example.cloudvision.entity.LabelAnnotation;
import org.gdgkobe.example.cloudvision.entity.Response;
import org.gdgkobe.example.cloudvision.model.ApiCallback;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

public class LabelDetectionActivity extends VisionBaseActivity {

    private static final String TAG = LabelDetectionActivity.class.getSimpleName();
    @Bind(R.id.text_view)
    TextView mTextView;

    /**
     * Activity表示用Intentを作成する
     *
     * @param context Context
     * @return intent
     */
    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, LabelDetectionActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labeldetection);
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

        App.getCloudVisionApi().requestLabelDetection(mBitmap, new ApiCallback() {
            @Override
            public void onSuccess(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.body().getResponses().get(0).getLabelAnnotations().size() <= 0) {
                    Snackbar.make(mCoordinator, R.string.label_annotation_error, Snackbar.LENGTH_LONG).show();
                } else {
                    mOriginalJson = response.message();
                    setText(response.body().getResponses().get(0).getLabelAnnotations());
                    Snackbar.make(mCoordinator, R.string.success, Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t );
            }

        });
        Snackbar.make(mCoordinator, R.string.vision_api_upload, Snackbar.LENGTH_LONG).show();

    }


    private void setText(List<LabelAnnotation> labelAnnotations) {
        StringBuilder stringBuilder = new StringBuilder();
        for (LabelAnnotation labelAnnotation : labelAnnotations) {
            stringBuilder.append(getString(R.string.label_detection_result,
                    labelAnnotation.getDescription(), labelAnnotation.getScore() * 100));
        }
        mTextView.setText(stringBuilder.toString());
    }
}
