package com.polygalov.colors.views;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.polygalov.colors.activities.ColorDetailActivity;
import com.polygalov.colors.R;
import com.polygalov.colors.data.ColorItem;
import com.polygalov.colors.data.ColorItems;
import com.polygalov.colors.wrappers.ColorItemListWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Константин on 15.02.2018.
 */

public class ColorItemListPage extends FrameLayout implements ColorItemListWrapper.ColorItemListWrapperListener {

    private ColorItemListWrapper mColorItemListWrapper;

    private ColorItems.OnColorItemChangeListener mOnColorItemChangeListener;

    private OnClickListener internilaListener;

    private Listener mListener;

    private List<ColorItem> currentColors;

    private ArrayList<ColorItem> colorJustAdded;

    private Application.ActivityLifecycleCallbacks activityLifecycleCallbacks;

    private boolean isHoldingActivityPaused;

    public ColorItemListPage(Context context) {
        super(context);
        init(context);
    }

    public ColorItemListPage(Context context, AttributeSet  attrs) {
        super(context, attrs);
        init(context);
    }

    public ColorItemListPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ColorItemListPage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ColorItems.registerListener(getContext(), mOnColorItemChangeListener);
        ((Application) getContext().getApplicationContext()).registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    @Override
    protected void onDetachedFromWindow() {
        ColorItems.unregisterListener(getContext(), mOnColorItemChangeListener);
        ((Application) getContext().getApplicationContext()).unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
        super.onDetachedFromWindow();
    }



    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    private void init(Context context) {

        initLifeCycleListener();

        initInternalListener();

        final View view = LayoutInflater.from(context).inflate(R.layout.color_item_list_page, this, true);
        final View emptyView = view.findViewById(R.id.view_color_item_list_page_empty_view);
        emptyView.setOnClickListener(internilaListener);
        final RecyclerView recyclerView = view.findViewById(R.id.view_color_item_list_page_list_view);
        mColorItemListWrapper = new FlavorColorItemListWrapper(recyclerView, this);

        final RecyclerView.Adapter adapter = mColorItemListWrapper.installRecyclerView();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                emptyView.setVisibility(adapter.getItemCount() == 0 ? VISIBLE : GONE);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                emptyView.setVisibility(adapter.getItemCount() == 0 ? VISIBLE : GONE);
            }
        });

        colorJustAdded = new ArrayList<>();
        currentColors = ColorItems.getSavedColorItems(context);
        mColorItemListWrapper.setItems(currentColors);
        mOnColorItemChangeListener = new ColorItems.OnColorItemChangeListener() {
            @Override
            public void onColorItemChanged(List<ColorItem> colorItems) {
                if (!isHoldingActivityPaused) {
                    mColorItemListWrapper.setItems(colorItems);
                } else {
                    if (colorItems.size() < currentColors.size()) {
                        mColorItemListWrapper.setItems(colorItems);
                    } else {
                        colorJustAdded.clear();
                        for (int i = 0; i < colorItems.size() - currentColors.size(); i++) {
                            colorJustAdded.add(colorItems.get(i));
                        }
                    }
                }
            }
        };
    }

    private void initLifeCycleListener() {
        isHoldingActivityPaused = false;
        activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                if (getContext() == activity) {
                    onHoldingActivityResumed();
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
                if (getContext() == activity) {
                    onHoldingActivityPaused();
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

    private void onHoldingActivityResumed() {
        isHoldingActivityPaused = false;
        if (colorJustAdded.size() > 0) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    mColorItemListWrapper.addItems(colorJustAdded);
                    currentColors.addAll(colorJustAdded);
                    colorJustAdded.clear();
                }
            }, 500);
        }
    }

    private void onHoldingActivityPaused() {
        isHoldingActivityPaused = true;
    }

    private void initInternalListener() {
        internilaListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onEmphasisOnAddColorActionRequested();
                }
            }
        };
    }

    public void onColorItemClickerd(@NonNull ColorItem colorItem, @NonNull View colorPreview) {
        ColorDetailActivity.startWithColorItem(getContext(), colorItem, colorPreview);
    }

    public interface Listener {

        void onEmphasisOnAddColorActionRequested();
    }
}
