package com.polygalov.colors.wrappers;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.polygalov.colors.data.ColorItem;

import java.util.List;

/**
 * Created by Константин on 15.02.2018.
 */

public abstract class ColorItemListWrapper {

    protected final RecyclerView mRecyclerView;
    protected final ColorItemListWrapperListener mListener;

    protected ColorItemListWrapper(RecyclerView recyclerView, ColorItemListWrapperListener callback) {
        mRecyclerView = recyclerView;
        mListener = callback;
    }

    public abstract RecyclerView.Adapter installRecyclerView();

    public abstract void setItems(List<ColorItem> items);

    public void addItems(List<ColorItem> colorJustAdded) {
        mRecyclerView.scrollToPosition(0);
    }


    public interface ColorItemListWrapperListener {

        void onColorItemClickerd(@NonNull ColorItem colorItem, @NonNull View colorPreview);
    }
}

