package com.polygalov.colors.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.polygalov.colors.R;
import com.polygalov.colors.data.ColorItem;
import com.polygalov.colors.utils.ClipDatas;


/**
 * Created by Константин on 18.02.2018.
 */

public class ColorItemDetailView extends LinearLayout implements View.OnClickListener {

    private TextView mHex;

    private TextView mRgb;

    private TextView mHsv;

    private Toast mToast;

    public ColorItemDetailView(Context context) {
        super(context);
        init(context);
    }

    public ColorItemDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ColorItemDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ColorItemDetailView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @Override
    protected void onDetachedFromWindow() {
        hideToast();
        super.onDetachedFromWindow();
    }

    public void setColorItem(ColorItem colorItem) {
        mHex.setText(colorItem.getHexString());
        mRgb.setText(colorItem.getRgbString());
        mHsv.setText(colorItem.getHsvString());
    }


    protected void hideToast() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }

    protected void showToast(int resId) {
        hideToast();
        mToast = Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT);
        mToast.show();
    }

    private void init(Context context) {

        setOrientation(VERTICAL);

        final View view = LayoutInflater.from(context).inflate(R.layout.view_color_item_detail, this);

        mHex = view.findViewById(R.id.view_color_item_detail_hex);
        mRgb = view.findViewById(R.id.view_color_item_detail_rgb);
        mHsv = view.findViewById(R.id.view_color_item_detail_hsv);

        mHex.setOnClickListener(this);
        mRgb.setOnClickListener(this);
        mHsv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        final int viewId = view.getId();

        switch (viewId) {
            case R.id.view_color_item_detail_hex:
                clipColor(R.string.color_clip_color_label_hex, mHex.getText());
                break;
            case R.id.view_color_item_detail_rgb:
                clipColor(R.string.color_clip_color_label_rgb, mRgb.getText());
                break;
            case R.id.view_color_item_detail_hsv:
                clipColor(R.string.color_clip_color_label_hsv, mHsv.getText());
                break;

            default:
                throw new IllegalArgumentException("Unsupported view clicked. Found: " + view);
        }
    }

    private void clipColor(int labelResourceId, CharSequence colorString) {
        final Context context = getContext();

        ClipDatas.clipPaintText(context, context.getString(labelResourceId), colorString);
        showToast(R.string.color_clip_success_copy_message);
    }
}
