package com.polygalov.colors.utils;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;

/**
 * Created by Константин on 16.02.2018.
 */

final class BackgroundUtilsImplPostLollipop {

    public BackgroundUtilsImplPostLollipop() {
    }

    static Drawable getBackground(int normalColor, int pressedColor) {
        return getPressedColorRipleDrawable(normalColor, pressedColor);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static RippleDrawable getPressedColorRipleDrawable(int normalColor, int pressedColor) {
        return new RippleDrawable(
                ColorStateList.valueOf(pressedColor),
                new ColorDrawable(normalColor),
                new ColorDrawable(Color.WHITE)
        );
    }

}
