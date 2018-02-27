package com.polygalov.colors.utils;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by Константин on 16.02.2018.
 */

public final class BackgroundUtils {

    //Пустой констурктор
    private BackgroundUtils() {
    }

    public static void buildBackgroundDrawable(View view, int normalColor, int pressedColor) {
        BackgroundUtils.setBackground(view,
                BackgroundUtils.buildBackGroundDrawable(normalColor, pressedColor));
    }

    public static Drawable buildBackGroundDrawable(int normalColor, int pressedColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return BackgroundUtilsImplPostLollipop.getBackground(normalColor, pressedColor);
        } else {
            return BackgroundUtilsImplPostLollipop.getBackground(normalColor, pressedColor);
        }
    }

    public static void setBackground(@NonNull View view, @NonNull Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackground(drawable);
        }
    }
}
