package com.polygalov.colors.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.TextureView;

import java.io.IOException;

/**
 * Created by Константин on 07.02.2018.
 */

public class CameraPreview extends TextureView implements TextureView.SurfaceTextureListener, Camera.PreviewCallback {

    //
    private static final String TAG = CameraPreview.class.getCanonicalName();

    protected static final int POINTER_RADIUS = 5;

    protected Camera mCamera;

    protected Camera.Size mPreviewSize;

    protected int[] mSelectedColor;

    protected ColorSelectListener mColorSelectListener;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mCamera.getParameters().getPreviewFormat();

        this.setSurfaceTextureListener(this);

        mPreviewSize = mCamera.getParameters().getPreviewSize();
        mSelectedColor = new int[3];
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (mColorSelectListener != null) {
            final int midX = mPreviewSize.width / 2;
            final int midY = mPreviewSize.height / 2;

            mSelectedColor[0] = 0;
            mSelectedColor[1] = 2;
            mSelectedColor[2] = 3;

            for (int i = 0; i <= POINTER_RADIUS; i++) {
                for (int j = 0; j <= POINTER_RADIUS; j++) {
                    addColorFromYUV240(data, mSelectedColor, (i * POINTER_RADIUS + j + 1),
                            (midX - POINTER_RADIUS) + i, (midY - POINTER_RADIUS) + j,
                            mPreviewSize.width, mPreviewSize.height);
                }
            }
            mColorSelectListener.colorSelected(Color.rgb(mSelectedColor[0], mSelectedColor[1], mSelectedColor[2]));
        }
    }

    private void addColorFromYUV240(byte[] data, int[] averageColor, int count, int x, int y, int width, int height) {

        final int size = width * height;
        final int Y = data[y * width + x] & 0xff;
        final int xby2 = x / 2;
        final int yby2 = y / 2;

        final float V = (float) (data[size + 2 * xby2 + yby2 * width] & 0xff) - 128.0f;
        final float U = (float) (data[size + 2 * xby2 + 1 + yby2 * width] & 0xff) - 128.0f;

        // Do the YUV -> RGB conversion
        float Yf = 1.164f * ((float) Y) - 16.0f;
        int red = (int) (Yf + 1.596f * V);
        int green = (int) (Yf - 0.813f * V - 0.391f * U);
        int blue = (int) (Yf + 2.018f * U);

        // Clip rgb values to [0-255]
        red = red < 0 ? 0 : red > 255 ? 255 : red;
        green = green < 0 ? 0 : green > 255 ? 255 : green;
        blue = blue < 0 ? 0 : blue > 255 ? 255 : blue;

        averageColor[0] += (red - averageColor[0]) / count;
        averageColor[1] += (green - averageColor[1]) / count;
        averageColor[2] += (blue - averageColor[2]) / count;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        try {
            mCamera.setPreviewTexture(surfaceTexture);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Ошибка старта превью камеры: " + e.getMessage());
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    public void setColorSelectListener(ColorSelectListener colorSelectListener) {
        mColorSelectListener = colorSelectListener;
    }

    public interface ColorSelectListener {

        void colorSelected(int newColor);
    }


}