package com.polygalov.colors.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.polygalov.colors.data.ColorItem;

/**
 * Created by Константин on 17.02.2018.
 */

public final class DeleteColorDialogFragment extends android.support.v4.app.DialogFragment {

    private static final String ARG_COLOR_ITEM = "DeleteColorDialog.Args.ARG_COLOR_ITEM";

    private Callback mCallback;

    public static DeleteColorDialogFragment newInstance(ColorItem colorItemToDelete) {
        final DeleteColorDialogFragment instance = new DeleteColorDialogFragment();
        final Bundle arguments = new Bundle();
        arguments.putParcelable(ARG_COLOR_ITEM, colorItemToDelete);
        instance.setArguments(arguments);
        return instance;
    }

    public DeleteColorDialogFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof Callback) {
            mCallback = (Callback) activity;
        } else {
            throw new IllegalStateException("Activity must implements DeleteColorDialog#Callback");
        }
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle arguments = getArguments();

        if (!arguments.containsKey(ARG_COLOR_ITEM)) {
            throw new IllegalStateException("Missing args. Please use the newInstance() method.");
        }

        final ColorItem colorItemToDelete = arguments.getParcelable(ARG_COLOR_ITEM);
        final Context context = getActivity();

        return DeleteColorDialogFragmentFlavor.createDialog(context, mCallback, colorItemToDelete);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public interface Callback {

        void onColorDeleteConfirmed(@NonNull ColorItem colorItemToDelete);
    }

}
