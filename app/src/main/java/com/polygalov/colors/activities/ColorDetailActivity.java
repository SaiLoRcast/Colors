package com.polygalov.colors.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import com.polygalov.colors.R;
import com.polygalov.colors.data.ColorItem;
import com.polygalov.colors.data.ColorItems;
import com.polygalov.colors.data.Palette;
import com.polygalov.colors.data.Palettes;
import com.polygalov.colors.fragments.DeleteColorDialogFragment;
import com.polygalov.colors.fragments.EditTextDialogFragment;
import com.polygalov.colors.views.ColorItemDetailView;

import java.util.List;

/**
 * Created by Константин on 17.02.2018.
 */

public class ColorDetailActivity extends AppCompatActivity implements DeleteColorDialogFragment.Callback,
        EditTextDialogFragment.CallBack {

    //Ключ для передачи элемента цвета в качестве дополнительного
    private static final String EXTRA_COLOR_ITEM = "ColorDetailActivity.Extras.EXTRA_COLOR_ITEM";

    //Ключ для передачи глобального видимого прямоугольника щелчка по щелчку цвета нажата
    private static final String EXTRA_START_BOUNDS = "ColorDetailActivity.Extras.EXTRA_START_BOUNDS";

    //Ключ для передачи необязательной палитры, связанной с отображаемым элементом цвета.
    //!!!может быть ненужным
    private static final String EXTRA_PALETTE = "ColorDetailActivity.Extras.EXTRA_PALETTE";

    //Качество изображения сжато перед совместным использованием.
    private static final int SHARED_IMAGE_QUALITY = 100;

    //
    private static final int SHARED_IMAGE_SIZE = 150;

    //
    private static final String SHARED_DIRECTORY = "colors";

    //
    private static final String SHARED_IMAGE_FILE = "shared_colors.jpg";

    //
    private static final String FILE_PROVIDER_AUTHORITY = ".fileprovidor";

    //
    private static final int REQUEST_CODE_EDIT_COLOR_ITEM_NAME = 15;

    public static void startWithColorItem(Context context, ColorItem colorItem,
                                          View colorPreviewClicked) {
        startWithColorItem(context, colorItem, colorPreviewClicked, null);
    }

    public static void startWithColorItem(Context context, ColorItem colorItem,
                                          View colorPreviewClicked, Palette palette) {

        final boolean isActivity = context instanceof Activity;
        final Rect startBounds = new Rect();
        colorPreviewClicked.getGlobalVisibleRect(startBounds);

        final Intent intent = new Intent(context, ColorDetailActivity.class);
        intent.putExtra(EXTRA_COLOR_ITEM, colorItem);
        intent.putExtra(EXTRA_START_BOUNDS, startBounds);
        intent.putExtra(EXTRA_PALETTE, palette);

        if (!isActivity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);

        if (isActivity) {
            ((Activity) context).overridePendingTransition(0, 0);
        }
    }

    //{@link android.view.View} для отображения перепрограммирования цвета во время анимации начала с позиции щелчка в нужное место.
    private View mTranslatedPreview;

    private View mScaledPreview;

    private ColorItemDetailView mColorItemDetailView;

    private ColorItem mColorItem;

    private Palette mPalette;

    private int shadowInset;

    private View mShadow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Intent intent = getIntent();
        if (!intent.hasExtra(EXTRA_COLOR_ITEM) || !intent.hasExtra(EXTRA_START_BOUNDS) || !intent.hasExtra(EXTRA_PALETTE)) {
            throw new IllegalStateException("Missing extras. Please use startWithColorItem");
        }

        final Rect startBounds = intent.getParcelableExtra(EXTRA_START_BOUNDS);
        if (savedInstanceState == null) {
            mColorItem = intent.getParcelableExtra(EXTRA_COLOR_ITEM);
            mPalette = intent.getParcelableExtra(EXTRA_PALETTE);
        } else {
            mColorItem = savedInstanceState.getParcelable(EXTRA_COLOR_ITEM);
            mPalette = savedInstanceState.getParcelable(EXTRA_PALETTE);
        }

        //Задайте заголовок активности с именем цвета, если не null
        if (!TextUtils.isEmpty(mColorItem.getName())) {
            setTitle(mColorItem.getName());
        } else {
            setTitle(mColorItem.getHexString());
        }

        shadowInset = getResources().getDimensionPixelSize(R.dimen.row_color_item_preview_size_shadow_size);

        //Создайте прямоугольник, который будет использоваться для получения границ остановки
        final Rect stopBounds = new Rect();

        //Ищем вьюхи
        mTranslatedPreview = findViewById(R.id.activity_color_detail_preview_translating);
        mScaledPreview = findViewById(R.id.activity_chooser_view_scaling);
        mColorItemDetailView = findViewById(R.id.activity_color_detail_color_item_detail_view);
        mColorItemDetailView.setColorItem(mColorItem);
        mShadow = findViewById(R.id.activity_color_detail_list_view_shadow);

        //Отображение данных элемента изображения
        mTranslatedPreview.getBackground().setColorFilter(mColorItem.getColor(), PorterDuff.Mode.MULTIPLY);
        mScaledPreview.getBackground().setColorFilter(mColorItem.getColor(), PorterDuff.Mode.MULTIPLY);

        final View previewContainer = findViewById(R.id.activity_color_detail_preview_container);
        final ViewTreeObserver viewTreeObserver = previewContainer.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {

                    previewContainer.getViewTreeObserver().removeOnPreDrawListener(this);

                    mTranslatedPreview.getGlobalVisibleRect(stopBounds);
                    final float scale = startBounds.width() / (float) stopBounds.width();
                    mTranslatedPreview.setScaleX(scale);
                    mTranslatedPreview.setScaleY(scale);

                    //вычислить границы снова, чтобы включить масштаб
                    mTranslatedPreview.getGlobalVisibleRect(stopBounds);
                    final int deltaY = startBounds.top - stopBounds.top;
                    final int deltaX = startBounds.left - stopBounds.left;

                    float scaleRotationX = (startBounds.width() - 2 * shadowInset) / (float) stopBounds.width();
                    float scaleRotationY = (startBounds.height() - 2 * shadowInset) / (float) stopBounds.height();
                    mScaledPreview.setScaleX(scaleRotationX);
                    mScaledPreview.setScaleY(scaleRotationY);

                    final AnimatorSet translationAnimatorSet = new AnimatorSet();
                    translationAnimatorSet
                            .play(ObjectAnimator.ofFloat(mTranslatedPreview, View.TRANSLATION_X, deltaX, 0))
                            .with(ObjectAnimator.ofFloat(mTranslatedPreview, View.TRANSLATION_Y, deltaY, -2 * shadowInset));
                    translationAnimatorSet.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            mScaledPreview.setVisibility(View.VISIBLE);
                            mColorItemDetailView.setVisibility(View.VISIBLE);
                            mShadow.setVisibility(View.VISIBLE);

                            final float maxContainerSize = (float) Math.sqrt(Math.pow(previewContainer.getWidth(), 2) + Math.pow(previewContainer.getHeight(), 2));
                            final float maxSize = Math.max(mScaledPreview.getWidth(), mScaledPreview.getHeight());
                            final float scaleRatio = maxContainerSize / maxSize;
                            final AnimatorSet scaleAnimatorSet = new AnimatorSet();
                            scaleAnimatorSet.play(ObjectAnimator.ofFloat(mScaledPreview, View.SCALE_X,
                                    mScaledPreview.getScaleX(), scaleRatio))
                                    .with(ObjectAnimator.ofFloat(mScaledPreview, View.SCALE_Y,
                                            mScaledPreview.getScaleY(), scaleRatio))
                                    .with(ObjectAnimator.ofFloat(mColorItemDetailView, View.ALPHA, 0f, 1f));
                            scaleAnimatorSet.start();
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });
                    translationAnimatorSet.start();

                    return true;
                }
            });
        }
        ColoDetailActivityFlavor.onCreate(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_COLOR_ITEM, mColorItem);
        outState.putParcelable(EXTRA_PALETTE, mPalette);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_color_detail, menu);
        if (mPalette != null) {
            menu.removeItem(R.id.menu_color_detail_action_delete);
        }

        ColoDetailActivityFlavor.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_color_detail_action_delete) {
            DeleteColorDialogFragment.newInstance(mColorItem).show(getSupportFragmentManager(), null);
            return true;
        } else if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.menu_color_detail_action_edit) {
            EditTextDialogFragment.newInstance(REQUEST_CODE_EDIT_COLOR_ITEM_NAME,
                    R.string.activity_color_detail_edit_text_dialog_fragment_title,
                    R.string.activity_color_detail_edit_text_dialog_fragment_positive_button,
                    android.R.string.cancel,
                    mColorItem.getHexString(),
                    mColorItem.getName(), true).show(getSupportFragmentManager(), null);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onColorDeleteConfirmed(@NonNull ColorItem colorItemToDelete) {
        if (ColorItems.deleteColorItem(this, colorItemToDelete)) {
            finish();
        }
    }

    @Override
    public void onEditTextDialogFragmentPositiveButtonClick(int requestCode, String text) {
        if (requestCode == REQUEST_CODE_EDIT_COLOR_ITEM_NAME) {
            if (TextUtils.isEmpty(text)) {
                setTitle(mColorItem.getHexString());
            } else {
                setTitle(text);
            }
        }

        mColorItem.setName(text);
        if (mPalette == null) {
            ColorItems.saveColorItem(this, mColorItem);
        } else {
            final List<ColorItem> colorItems = mPalette.getColors();
            for (ColorItem candidate : colorItems) {
                if (candidate.getId() == mColorItem.getId()) {
                    candidate.setName(text);
                    break;
                }
            }
            Palettes.saveColorPalette(this, mPalette);
        }
    }

    @Override
    public void onEditTextDialogFragmentNegativeButtonClick(int requestCode) {
        //Пусто
    }
}
