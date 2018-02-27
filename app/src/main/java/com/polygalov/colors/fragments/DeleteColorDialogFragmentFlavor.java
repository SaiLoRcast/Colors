package com.polygalov.colors.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.polygalov.colors.R;
import com.polygalov.colors.data.ColorItem;

/**
 * Created by Константин on 17.02.2018.
 */

final class DeleteColorDialogFragmentFlavor {

    static AlertDialog createDialog(Context context,
                                    final DeleteColorDialogFragment.Callback callback,
                                    final ColorItem colorItemToDelete) {
        final View view = LayoutInflater.from(context).inflate(R.layout.fragment_dialog_delete_color, null);
        view.findViewById(R.id.fragment_dialog_delete_color_preview).setBackgroundColor(colorItemToDelete.getColor());

        return new AlertDialog.Builder(context)
                .setView(view).setCancelable(true)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        callback.onColorDeleteConfirmed(colorItemToDelete);
                    }
                }).setNegativeButton(android.R.string.cancel, null).create();
    }

    private DeleteColorDialogFragmentFlavor() {

    }
}
