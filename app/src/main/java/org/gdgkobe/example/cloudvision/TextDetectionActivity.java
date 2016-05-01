package org.gdgkobe.example.cloudvision;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import org.gdgkobe.example.cloudvision.entity.Response;
import org.gdgkobe.example.cloudvision.entity.Vertex;
import org.gdgkobe.example.cloudvision.model.ApiCallback;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.Icepick;
import retrofit2.Call;

public class TextDetectionActivity extends VisionBaseActivity {

    private static final String TAG = TextDetectionActivity.class.getSimpleName();

    //テキスト検出エリアのボーダーカラー
    private static final int POLY_AREA_COLOR = Color.argb(255, 255, 0, 0);

    @Bind(R.id.language)
    TextView mLanguage;
    @Bind(R.id.result_text)
    TextView mResultText;
    private Response mResponse;

    /**
     * Activity表示用Intentを作成する
     *
     * @param context Context
     * @return intent
     */
    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, TextDetectionActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        return intent;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textdetection);
        ButterKnife.bind(this);
        super.restoreView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        showResult();
    }

    /**
     * JsonをParse
     */
    void parse() {
        try {
            if (!TextUtils.isEmpty(mOriginalJson)) {
                mResponse = Response.fromJson(mOriginalJson);
            }
        } catch (Exception e) {
            Log.d(TAG, "json parse error");
        }
    }


    @OnClick(R.id.upload)
    void callCloudVision() {
        //Imageが選択されていないかどうかのチェック
        if (mBitmap == null) {
            Snackbar.make(mCoordinator, R.string.not_selected_image, Snackbar.LENGTH_LONG).show();
            return;
        }

        App.getCloudVisionApi().requestTextDetection(mBitmap, new ApiCallback() {
            @Override
            public void onSuccess(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.body().getResponses().get(0).getTextAnnotations().size() <= 0) {
                    Snackbar.make(mCoordinator, R.string.text_annotation_error, Snackbar.LENGTH_LONG).show();
                } else {
                    mOriginalJson = response.message();
                    showResult();
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


    /**
     * OCR結果を表示
     */
    void showResult() {
        parse();
        if (mResponse != null) {
            mLanguage.setText(mResponse.getResponses().get(0).getTextAnnotations().get(0).getLocale());
            mResultText.setText(mResponse.getResponses().get(0).getTextAnnotations().get(0).getDescription());
            List<Vertex> vertexList = mResponse.getResponses().get(0).getTextAnnotations().get(0).getBoundingPoly().getVertices();
            Canvas canvas;
            Bitmap newBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            canvas = new Canvas(newBitmap);
            canvas.drawBitmap(mBitmap, 0, 0, null);
            Paint paint = new Paint();
            paint.setColor(POLY_AREA_COLOR);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            canvas.drawRect(vertexList.get(0).getX(),
                    vertexList.get(0).getY(),
                    vertexList.get(2).getX(),
                    vertexList.get(2).getY()
                    , paint);
            mImage.setImageBitmap(newBitmap);
        }
    }


}
