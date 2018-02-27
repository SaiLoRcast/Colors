package com.polygalov.colors.fragments;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.polygalov.colors.R;
import com.polygalov.colors.utils.Views;

/**
 * Created by Константин on 17.02.2018.
 */

public class EditTextDialogFragment extends DialogFragment {

    //Ключ для передачи кода запроса. Код запроса будет передан в методах обратных вызовов.
    private static final String ARG_REQUEST_CODE = "EditTextDialogFragment.Args.ARG_REQUEST_CODE";

    //Ключ для передачи идентификатора ресурса заголовка.
    private static final String ARG_TITLE_RESOURCE_ID = "EditTextDialogFragment.Args.ARG_TITLE_RESOURCE_ID";

    //Ключ для передачи идентификатора ресурса положительной кнопки.
    private static final String ARG_POSITIVE_BUTTON_RESOURCE_ID = "EditTextDialogFragment.Args.ARG_POSITIVE_BUTTON_RESOURCE_ID";

    //Ключ для передачи идентификатора ресурса отрицательной кнопки.
    private static final String ARG_NEGATIVE_BUTTON_RESOURCE_ID = "EditTextDialogFragment.Args.ARG_NEGATIVE_BUTTON_RESOURCE_ID";

    //Ключ для передачи идентификатора ресурса текста подсказки.
    private static final String ARG_EDIT_TEXT_HINT = "EditTextDialogFragment.Args.ARG_EDIT_TEXT_HINT";

    //Ключ для передачи исходного текстового значения.
    private static final String ARG_EDIT_TEXT_INITIAL_TEXT = "EditTextDialogFragment.Args.ARG_EDIT_TEXT_INITIAL_TEXT";

    //Ключ для пустых строк.
    private static final String ARG_ALLOW_EMPTY_STRING = "EditTextDialogFragment.Args.ARG_ALLOW_EMPTY_STRING";

    public static EditTextDialogFragment newInstance(int requestCode,
                                                     @StringRes int titleResourceId,
                                                     @StringRes int positiveButtonResourceId,
                                                     @StringRes int negativeButtonResourceId,
                                                     String editTextHint,
                                                     String editTextInitialText) {
        return newInstance(requestCode, titleResourceId, positiveButtonResourceId,
                negativeButtonResourceId, editTextHint, editTextInitialText, false);
    }

    public static EditTextDialogFragment newInstance(int requestCode,
                                                     @StringRes int titleResourceId,
                                                     @StringRes int positiveButtonResourceId,
                                                     @StringRes int negativeButtonResourceId,
                                                     String editTextHint,
                                                     String editTextInitialText,
                                                     boolean allowEmptyString) {


        final EditTextDialogFragment instance = new EditTextDialogFragment();
        final Bundle args = new Bundle();

        args.putInt(ARG_REQUEST_CODE, requestCode);
        args.putInt(ARG_TITLE_RESOURCE_ID, titleResourceId);
        args.putInt(ARG_POSITIVE_BUTTON_RESOURCE_ID, positiveButtonResourceId);
        args.putInt(ARG_NEGATIVE_BUTTON_RESOURCE_ID, negativeButtonResourceId);
        args.putString(ARG_EDIT_TEXT_HINT, editTextHint);
        args.putString(ARG_EDIT_TEXT_INITIAL_TEXT, editTextInitialText);
        args.putBoolean(ARG_ALLOW_EMPTY_STRING, allowEmptyString);
        instance.setArguments(args);
        return instance;
    }

    private CallBack mCallback;

    private EditText mEditText;

    private ObjectAnimator mNopeAnimator;

    private int mRequestCode;

    private boolean mAllowEmptyString;

    //Пустой конструктор
    public EditTextDialogFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof DeleteColorDialogFragment.Callback) {
            mCallback = (CallBack) activity;
        } else {
            throw new IllegalStateException("Activity must implements EditTextDialogFragment#Callback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle args = getArguments();

        ensureSaneArgs(args);

        mRequestCode = args.getInt(ARG_REQUEST_CODE);
        mAllowEmptyString = args.getBoolean(ARG_ALLOW_EMPTY_STRING);

        final int titleResourceId = args.getInt(ARG_TITLE_RESOURCE_ID);
        final int positiveButtonResourceId = args.getInt(ARG_POSITIVE_BUTTON_RESOURCE_ID);
        final int negativeButtonResourceId = args.getInt(ARG_NEGATIVE_BUTTON_RESOURCE_ID);
        final String editTextHint = args.getString(ARG_EDIT_TEXT_HINT);
        final String editTextInitialText = args.getString(ARG_EDIT_TEXT_INITIAL_TEXT);

        final Context context = getActivity();
        final View view = LayoutInflater.from(context).inflate(R.layout.fragment_dialog_edit_text, null);

        mEditText = view.findViewById(R.id.fragment_dialog_edit_text);
        mEditText.setHint(editTextHint);
        mEditText.setText(editTextInitialText);
        mNopeAnimator = Views.nopeAnimator(mEditText, mEditText.getPaddingLeft());

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view)
                .setTitle(titleResourceId)
                .setCancelable(true)
                .setPositiveButton(positiveButtonResourceId, null)
                .setNegativeButton(negativeButtonResourceId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        handleNegativeClick();
                    }
                });

        //Слушатель для кнопки ОК
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                final Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handlePositiveClick();
                    }
                });
            }
        });

        //если пользователь нажал IME_ACTION_DONE будем считать нажал ОК
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    handlePositiveClick();
                    return true;
                }
                return false;
            }
        });

        return dialog;
    }

    private void handlePositiveClick() {
        final String text = mEditText.getText().toString();
        if (TextUtils.isEmpty(text) && !mAllowEmptyString) {
            if (mNopeAnimator.isRunning()) {
                mNopeAnimator.cancel();
            }
            mNopeAnimator.start();
        } else {
            mCallback.onEditTextDialogFragmentPositiveButtonClick(mRequestCode, text);
            getDialog().dismiss();
        }
    }

    private void handleNegativeClick() {
        mCallback.onEditTextDialogFragmentNegativeButtonClick(mRequestCode);
    }

    private void ensureSaneArgs(Bundle args) {
        if (args == null) {
            throw new IllegalArgumentException("Args can't be null");
        }

        if (!args.containsKey(ARG_REQUEST_CODE)) {
            throw new IllegalArgumentException("Отсутствует код запроса. Используйте EditTextDialogFragment # newInstance()");
        }

        if (!args.containsKey(ARG_TITLE_RESOURCE_ID)) {
            throw new IllegalArgumentException("Отсутствует идентификатор ресурса заголовка. Используйте EditTextDialogFragment # newInstance()");
        }

        if (!args.containsKey(ARG_POSITIVE_BUTTON_RESOURCE_ID)) {
            throw new IllegalArgumentException("Отсутствует идентификатор ресурса положительной кнопки. Используйте EditTextDialogFragment # newInstance()");
        }

        if (!args.containsKey(ARG_NEGATIVE_BUTTON_RESOURCE_ID)) {
            throw new IllegalArgumentException("Отсутствует идентификатор ресурса отрицательной кнопки. Используйте EditTextDialogFragment # newInstance()");
        }

        if (!args.containsKey(ARG_EDIT_TEXT_HINT)) {
            throw new IllegalArgumentException("Отсутствует идентификатор ресурса ссылки для редактирования текста. Используйте EditTextDialogFragment # newInstance ()");
        }


        if (!args.containsKey(ARG_EDIT_TEXT_INITIAL_TEXT)) {
            throw new IllegalArgumentException("Отсутствует исходный текст исходного текста. Используйте EditTextDialogFragment # newInstance()");
        }

        if (!args.containsKey(ARG_ALLOW_EMPTY_STRING)) {
            throw new IllegalArgumentException("Missing edit text initial text. Please use EditTextDialogFragment#newInstance()");
        }
    }

    public interface CallBack {

        void onEditTextDialogFragmentPositiveButtonClick(int requestCode, String value);

        void onEditTextDialogFragmentNegativeButtonClick(int requestCode);

    }
}
