package org.gdgkobe.example.cloudvision;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.gdgkobe.example.cloudvision.entity.Poly;
import org.gdgkobe.example.cloudvision.entity.Response;
import org.gdgkobe.example.cloudvision.listener.ImageUpdateListener;
import org.gdgkobe.example.cloudvision.model.ApiCallback;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

public class FaceDetectionActivity extends VisionBaseActivity implements ImageUpdateListener {


    private static final String TAG = FaceDetectionActivity.class.getSimpleName();

    //パーツ検出エリアのボーダーカラー
    private static final int LANDMARK_AREA_COLOR = Color.argb(255, 255, 255, 255);
    //顔検出エリアのボーダーカラー
    private static final int POLY_AREA_COLOR = Color.argb(255, 255, 0, 0);
    //パーツ検出エリアのエリアサイズ
    private static final int LANDMARK_AREA_SIZE = 10;

    /**
     * Activity表示用Intentを作成する
     *
     * @param context Context
     * @return intent
     */
    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, FaceDetectionActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facedetection);
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

        App.getCloudVisionApi().requestFaceDetection(mBitmap, new ApiCallback() {
            @Override
            public void onSuccess(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.body().getResponses().get(0).getFaceAnnotations().size() <= 0) {
                    Snackbar.make(mCoordinator, R.string.face_annotation_error, Snackbar.LENGTH_LONG).show();
                } else {
                    mOriginalJson = response.message();
                    showFaceDetectionFragment(response.message());
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

    private void showFaceDetectionFragment(String json) {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment);
        if (f instanceof FaceDetectionFragment) {
            ((FaceDetectionFragment) f).update(json);
        } else {
            getSupportFragmentManager().
                    beginTransaction()
                    .replace(R.id.fragment, FaceDetectionFragment.newInstance(json))
                    .commitAllowingStateLoss();
        }
    }


    /**
     * 座標を選択されたときにメインViewが表示されるように
     * ページ下部の情報シートを閉じる
     */
    private void collapseBottomSheet() {
        BottomSheetBehavior behavior = BottomSheetBehavior.from(mBottomSheet);
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }


    @Override
    public void setPoint(float x, float y) {
        Canvas canvas;
        Bitmap newBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(newBitmap);
        canvas.drawBitmap(mBitmap, 0, 0, null);
        Paint paint = new Paint();
        paint.setColor(LANDMARK_AREA_COLOR);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        canvas.drawRect(x - LANDMARK_AREA_SIZE / 2, y - LANDMARK_AREA_SIZE / 2, x + LANDMARK_AREA_SIZE + 2, y +
                LANDMARK_AREA_SIZE + 2, paint);
        mImage.setImageBitmap(newBitmap);
        collapseBottomSheet();
    }

    @Override
    public void setPoly(Poly boundingPoly, Poly fbBoundingPoly) {
        Bitmap newBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas;
        canvas = new Canvas(newBitmap);
        canvas.drawBitmap(mBitmap, 0, 0, null);
        Paint paint = new Paint();
        paint.setColor(POLY_AREA_COLOR);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        drawPoly(boundingPoly, canvas, paint);
        drawPoly(fbBoundingPoly, canvas, paint);
        mImage.setImageBitmap(newBitmap);
    }

    private void drawPoly(Poly poly, Canvas canvas, Paint paint) {
        canvas.drawRect(poly.getVertices().get(0).getX(),
                poly.getVertices().get(0).getY(),
                poly.getVertices().get(2).getX(),
                poly.getVertices().get(2).getY()
                , paint);
    }
}
