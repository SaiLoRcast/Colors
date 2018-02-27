package com.polygalov.colors.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.polygalov.colors.R;

/**
 * Created by Константин on 16.02.2018.
 */

class ColorDotDrawable extends GradientDrawable {

    private final Paint mPaint;
    private final float shadowSize;

    public ColorDotDrawable(Context context) {
        super(Orientation.TOP_BOTTOM, new int[]{
                ContextCompat.getColor(context, R.color.black_shadow),
                Color.TRANSPARENT
        });
        Resources resources = context.getResources();

        shadowSize = resources.getDimensionPixelSize(R.dimen.row_color_item_preview_size_shadow_size);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);

        setGradientType(RADIAL_GRADIENT);
        setGradientCenter(0.5f, 0.5f);
    }

    @Override
    protected void onBoundsChange(Rect r) {
        super.onBoundsChange(r);
        setGradientRadius(r.width() / 2f);
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        //рисуем тень
        super.draw(canvas);
        //
        canvas.drawCircle(bounds.centerX(), bounds.centerY(), bounds.width() / 2f - shadowSize * 2, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
