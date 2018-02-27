package com.polygalov.colors;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.polygalov.colors.data.ColorItem;
import com.polygalov.colors.data.ColorItems;
import com.polygalov.colors.utils.Cameras;
import com.polygalov.colors.views.CameraPreview;

/**
 * Created by Константин on 07.02.2018.
 */

class CameraBaseActivity extends AppCompatActivity implements View.OnClickListener, CameraPreview.ColorSelectListener {

    protected static final String TAG = CameraBaseActivity.class.getSimpleName();

    protected static final String PICKED_COLOR_PROGRESS_PROPERTY_NAME = "pickedColorProgress";
    protected static final String SAVE_COMPLETED_PROGRESS_PROPERTY_NAME = "saveCompletedProgress";

    protected static final long DURATION_CONFIRM_SAVE_MESSAGE = 400;
    protected static final long DELAY_HIDE_CONFIRM_SAVE_MESSAGE = 1400;

    private static Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return camera;
    }

    protected Camera mCamera;

    protected boolean mIsPortrait;

    protected FrameLayout mPreviewContainer;

    protected CameraPreview mCameraPreview;

    protected CameraAsyncTask mCameraAsyncTask;

    protected int mSelectedColor;
    protected int mLastPickedColor;

    protected View mPickedColorPreview;

    protected View mPickedColorPreviewAnimated;

    protected ObjectAnimator mPickedColorProgressAnimator;

    protected float mTranslationDeltaX;
    protected float mTranslationDeltaY;

    protected TextView mColorPreviewText;

    protected View mPointerRectangle;

    protected View mSaveCompletedIcon;

    protected View mSaveButton;

    protected float mSaveCompletedProgress;

    protected ObjectAnimator mSaveCompletedProgressAnimator;

    protected TextView mConfirmSaveMessage;

    protected Interpolator mConfirmSaveMessageInterpolator;

    protected Runnable mHideConfirmSaveMessage;

    protected boolean mIsFlashOn;

    protected String action = null;

    public static final String OI_COLOR_PICKED = "org.openintents.action.PICK_COLOR";
    public static final String OI_COLOR_DATA = "org.openintents.extra.COLOR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_base_activity);

        initProgressAnimator();
        initSaveCompletedProgressAnimator();
        initViews();
        initTranslationDeltas();

        Intent intent = getIntent();
        if (intent != null) {
            action = intent.getAction();
        }
    }

    @SuppressLint("NewApi")
    protected void initTranslationDeltas() {
        ViewTreeObserver vto = mPointerRectangle.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ViewTreeObserver vto = mPointerRectangle.getViewTreeObserver();
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                        vto.removeGlobalOnLayoutListener(this);
                    } else {
                        vto.removeOnGlobalLayoutListener(this);
                    }

                    final Rect pointerRingRect = new Rect();
                    final Rect colorPreviewAnimatedRect = new Rect();
                    mPickedColorPreviewAnimated.getGlobalVisibleRect(pointerRingRect);
                    mPickedColorPreview.getGlobalVisibleRect(colorPreviewAnimatedRect);

                    mTranslationDeltaX = colorPreviewAnimatedRect.left - pointerRingRect.left;
                    mTranslationDeltaY = colorPreviewAnimatedRect.top - pointerRingRect.top;
                }
            });
        }
    }

    protected void initViews() {
        mIsPortrait = getResources().getBoolean(R.bool.is_portrait);
        mPreviewContainer = findViewById(R.id.preview_container);
        mPickedColorPreview = findViewById(R.id.color_preview_bottom_field);
        mPickedColorPreviewAnimated = findViewById(R.id.color_preview_point_animated);
        mColorPreviewText = findViewById(R.id.color_preview_bottom_field_text);
        mPointerRectangle = findViewById(R.id.color_preview_point_rectangle);
        mSaveCompletedIcon = findViewById(R.id.color_preview_bottom_field_save_completed);
        mSaveButton = findViewById(R.id.color_preview_bottom_field_save_button);
        mSaveButton.setOnClickListener(this);
        mConfirmSaveMessage = findViewById(R.id.confirm_save_message);
        mHideConfirmSaveMessage = new Runnable() {
            @Override
            public void run() {
                mConfirmSaveMessage.animate()
                        .translationY(-mConfirmSaveMessage.getMeasuredHeight())
                        .setDuration(DURATION_CONFIRM_SAVE_MESSAGE)
                        .start();
            }
        };
        positionConfirmSaveMessage();
        mConfirmSaveMessageInterpolator = new DecelerateInterpolator();

        mLastPickedColor = ColorItems.getLastPickedColor(this);
        applyPreviewColor(mLastPickedColor);
    }

    protected void positionConfirmSaveMessage() {
        ViewTreeObserver vto = mConfirmSaveMessage.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    ViewTreeObserver viewTreeObserver = mConfirmSaveMessage.getViewTreeObserver();
                    viewTreeObserver.removeOnPreDrawListener(this);
                    mConfirmSaveMessage.setTranslationY(-mConfirmSaveMessage.getMeasuredHeight());
                    return true;
                }
            });
        }
    }

    protected void setPickedColorProgress(float progress) {
        final float fastOppositeProgress = (float) Math.pow(progress, 0.3f);
        final float translationX = mTranslationDeltaX * progress;
        final float translationY = (float) (mTranslationDeltaY * Math.pow(progress, 2f));

        mPickedColorPreviewAnimated.setTranslationX(translationX);
        mPickedColorPreviewAnimated.setTranslationY(translationY);
        mPickedColorPreviewAnimated.setScaleX(fastOppositeProgress);
        mPickedColorPreviewAnimated.setScaleY(fastOppositeProgress);
    }

    public void setSaveCompletedProgress(float progress) {
        mSaveButton.setScaleX(progress);
        mSaveButton.setRotation(45 * (1 - progress));
        mSaveCompletedIcon.setScaleX(1 - progress);
        mSaveCompletedProgress = progress;
    }

    protected void initSaveCompletedProgressAnimator() {
        mSaveCompletedProgressAnimator = ObjectAnimator.ofFloat(this, SAVE_COMPLETED_PROGRESS_PROPERTY_NAME, 1f, 0f);
    }

    private void initProgressAnimator() {
        mPickedColorProgressAnimator = ObjectAnimator.ofFloat(this, PICKED_COLOR_PROGRESS_PROPERTY_NAME, 0f, 1f);
        mPickedColorProgressAnimator.setDuration(400);
        mPickedColorProgressAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animator) {
                mPickedColorPreviewAnimated.setVisibility(View.VISIBLE);
                mPickedColorPreviewAnimated.getBackground().setColorFilter(mSelectedColor, PorterDuff.Mode.SRC_ATOP);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                ColorItems.saveLastPickedColor(CameraBaseActivity.this, mLastPickedColor);
                applyPreviewColor(mLastPickedColor);
                mPickedColorPreviewAnimated.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                mPickedColorPreviewAnimated.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    protected void applyPreviewColor(int previewColor) {
        setSaveCompleted(false);
        mPickedColorPreview.getBackground().setColorFilter(previewColor, PorterDuff.Mode.SRC_ATOP);
        mColorPreviewText.setText(ColorItem.makeHexString(previewColor));
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCameraAsyncTask = new CameraAsyncTask();
        mCameraAsyncTask.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Отмена АсинкТаск камеры
        mCameraAsyncTask.cancel(true);

        //
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }

        if (mCameraPreview != null) {
            mPreviewContainer.removeView(mCameraPreview);
        }
    }

    @Override
    protected void onDestroy() {

        mConfirmSaveMessage.removeCallbacks(mHideConfirmSaveMessage);

        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if (view == mCameraPreview) {
            animatePickedColor(mSelectedColor);
        } else if (view.getId() == R.id.color_preview_bottom_field_save_button) {
            if (OI_COLOR_PICKED.equals(action)) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(OI_COLOR_DATA, mLastPickedColor);
                setResult(RESULT_OK, returnIntent);
                finish();
                return;
            }
            ColorItems.saveColorItem(this, new ColorItem(mLastPickedColor));
            setSaveCompleted(true);
        }
    }

    protected void setSaveCompleted(boolean isSaveCompleted) {
        mSaveButton.setEnabled(!isSaveCompleted);
        mSaveCompletedProgressAnimator.cancel();
        mSaveCompletedProgressAnimator.setFloatValues(mSaveCompletedProgress, isSaveCompleted ? 0f : 1f);
        mSaveCompletedProgressAnimator.start();
    }

    protected void animatePickedColor(int pickedColor) {
        mLastPickedColor = pickedColor;
        if (mPickedColorProgressAnimator.isRunning()) {
            mPickedColorProgressAnimator.cancel();
        }
        mPickedColorProgressAnimator.start();
    }

    @Override
    public void colorSelected(int newColor) {
        mSelectedColor = newColor;
        mPointerRectangle.getBackground().setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
    }

    private class CameraAsyncTask extends AsyncTask<Void, Void, Camera> {

        protected FrameLayout.LayoutParams mPreviewParams;

        @Override
        protected Camera doInBackground(Void... params) {

            Camera camera = getCameraInstance();
            if (camera == null) {
                CameraBaseActivity.this.finish();
            } else {
                Camera.Parameters cameraParametres = camera.getParameters();

                Camera.Size bestSize = Cameras.getBestPreviewSize(
                        cameraParametres.getSupportedPreviewSizes(),
                        mPreviewContainer.getWidth(),
                        mPreviewContainer.getHeight(),
                        mIsPortrait );
                //задаём оптимальное превью
                cameraParametres.setPreviewSize(bestSize.width, bestSize.height);
                camera.setParameters(cameraParametres);

                Cameras.setCameraDisplayOrientation(CameraBaseActivity.this, camera);

                int[] adaptedDimension = Cameras.getPropertionalDimension(
                        bestSize, mPreviewContainer.getWidth(),
                        mPreviewContainer.getHeight(), mIsPortrait );

                mPreviewParams = new FrameLayout.LayoutParams(adaptedDimension[0], adaptedDimension[1]);
                mPreviewParams.gravity = Gravity.CENTER;
            }
            return camera;
        }

        @Override
        protected void onPostExecute(Camera camera) {
            super.onPostExecute(camera);

            if (!isCancelled()) {
                mCamera = camera;
                if (mCamera == null) {
                    CameraBaseActivity.this.finish();
                } else {
                    mCameraPreview = new CameraPreview(CameraBaseActivity.this, mCamera);
                    mCameraPreview.setColorSelectListener(CameraBaseActivity.this);
                    mCameraPreview.setOnClickListener(CameraBaseActivity.this);

                    mPreviewContainer.addView(mCameraPreview, 0, mPreviewParams);
                }
            }
        }

        @Override
        protected void onCancelled(Camera camera) {
            super.onCancelled(camera);
            if (camera != null) {
                camera.release();
            }
        }
    }


}
