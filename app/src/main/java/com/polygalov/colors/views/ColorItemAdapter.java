package com.polygalov.colors.views;

import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.polygalov.colors.R;
import com.polygalov.colors.data.ColorItem;
import com.polygalov.colors.utils.BackgroundUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Константин on 16.02.2018.
 */

class ColorItemAdapter extends RecyclerView.Adapter<ColorItemAdapter.ColorItemHolder> {

    private final List<ColorItem> mItems;
    private final ColorItemAdapterListener mListener;

    ColorItemAdapter(ColorItemAdapterListener listener) {
        this.mListener = listener;
        this.mItems = new ArrayList<>();
    }

    @Override
    public ColorItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_color_item, parent, false);

        return new ColorItemHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(ColorItemHolder holder, int position) {
        final ColorItem colorItem = mItems.get(position);
        holder.bind(colorItem);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    void setItems(List<ColorItem> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    void addItems(List<ColorItem> colorJustAdded) {
        for (int i = colorJustAdded.size()-1; i>=0;i--) {
            mItems.add(0, colorJustAdded.get(i));
        }
        notifyItemRangeInserted(0,colorJustAdded.size());
    }

    interface ColorItemAdapterListener {

        //Вызывается, когда {@link ColorItem} был только что нажат.
        void onColorItemClicked(@NonNull ColorItem colorItem, @NonNull View colorPreview);

        //
        void onColorItemLongClicked(@NonNull ColorItem colorItem);
    }


    public static class ColorItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        //{@link View}, чтобы показать предварительный просмотр цвета.
        private final View mColorPreview;

        //{@link TextView}, чтобы отобразить шестнадцатеричный код элемента цвета.
        private final TextView mColorText;

        //
        private final View mUnderlayingView;

        private final ColorItemAdapterListener mListener;

        private ColorItem mColorItem;

        public ColorItemHolder(View view, ColorItemAdapterListener listener) {
            super(view);
            mListener = listener;
            mUnderlayingView = view;
            mColorPreview = view.findViewById(R.id.row_color_item_preview);
            mColorText = view.findViewById(R.id.row_color_item_text);
            BackgroundUtils.setBackground(mColorPreview, new ColorDotDrawable(view.getContext()));
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        public void bind(ColorItem colorItem) {
            mColorItem = colorItem;
            mColorPreview.getBackground().setColorFilter(colorItem.getColor(), PorterDuff.Mode.MULTIPLY);
            if (!TextUtils.isEmpty(colorItem.getName())) {
                mColorText.setText(colorItem.getName());
            } else {
                mColorText.setText(colorItem.getHexString());
            }
        }

        @Override
        public void onClick(View view) {
            if (view != mUnderlayingView) {
                throw new IllegalArgumentException("Unsupported view clicked. Found: " + view);
            }

            if (mColorItem != null) {
                mListener.onColorItemClicked(mColorItem, mColorPreview);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (view != mUnderlayingView || mColorItem == null) {
                return false;
            }
            mListener.onColorItemLongClicked(mColorItem);
            return true;
        }
    }
}
