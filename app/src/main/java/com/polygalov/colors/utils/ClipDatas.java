package com.polygalov.colors.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * Created by Константин on 17.02.2018.
 */

public final class ClipDatas {

    public static void clipPaintText(Context context, String label, CharSequence text) {
        final ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        final ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
    }

    private ClipDatas() {

    }
}
