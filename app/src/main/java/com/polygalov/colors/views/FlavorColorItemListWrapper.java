package com.polygalov.colors.views;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.polygalov.colors.R;
import com.polygalov.colors.data.ColorItem;
import com.polygalov.colors.utils.ClipDatas;
import com.polygalov.colors.wrappers.ColorItemListWrapper;

import java.util.List;

/**
 * Created by Константин on 16.02.2018.
 */

public class FlavorColorItemListWrapper extends ColorItemListWrapper implements ColorItemAdapter.ColorItemAdapterListener {

    private final Context mContext;
    private final ColorItemAdapter mAdapter;
    private final String mClipColorItemLabel;
    private final Application.ActivityLifecycleCallbacks mActivityLifecycleCallbacks;
    private Toast mToast;

    public FlavorColorItemListWrapper(RecyclerView recyclerView, ColorItemListWrapperListener callback) {
        super(recyclerView, callback);
        mContext = recyclerView.getContext();
        mAdapter = new ColorItemAdapter(this);
        mClipColorItemLabel = mContext.getString(R.string.color_clip_color_label_hex);

        mActivityLifecycleCallbacks = createToastWatcher();
        recyclerView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {
                ((Application) mContext.getApplicationContext()).registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
            }

            @Override
            public void onViewDetachedFromWindow(View view) {
                ((Application) mContext.getApplicationContext()).unregisterActivityLifecycleCallbacks(mActivityLifecycleCallbacks);

            }
        });
    }

    @Override
    public void onColorItemClicked(@NonNull ColorItem colorItem, @NonNull View colorPreview) {
        mListener.onColorItemClickerd(colorItem, colorPreview);
    }

    @Override
    public void onColorItemLongClicked(@NonNull ColorItem colorItem) {
        ClipDatas.clipPaintText(mContext, mClipColorItemLabel, colorItem.getHexString());
        showToast(R.string.color_clip_success_copy_message);
    }

    @Override
    public RecyclerView.Adapter installRecyclerView() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        return mAdapter;
    }

    @Override
    public void setItems(List<ColorItem> items) {
        mAdapter.setItems(items);
    }

    @Override
    public void addItems(List<ColorItem> colorJustAdded) {
        super.addItems(colorJustAdded);
        mAdapter.addItems(colorJustAdded);
    }

    private void showToast(@StringRes int resId) {
        hideToast();
        mToast = Toast.makeText(mContext, resId, Toast.LENGTH_SHORT);
        mToast.show();
    }

    private void hideToast() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }

    private Application.ActivityLifecycleCallbacks createToastWatcher() {

        return new Application.ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {
                if (mContext == activity) {
                    hideToast();
                }
            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        };
    }
}
