package com.polygalov.colors.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.polygalov.colors.utils.BackgroundUtils;

/**
 * Created by Константин on 16.02.2018.
 */

public class ColorDotView extends View {

    public ColorDotView(Context context) {
        this(context, null);
    }

    public ColorDotView (Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorDotView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int size = Math.min(width, height);
        int measureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        setMeasuredDimension(measureSpec, measureSpec);
    }


    private void initialize(Context context) {
        if (!isInEditMode()) {
            BackgroundUtils.setBackground(this, new ColorDotDrawable(context));
        }
    }
}
