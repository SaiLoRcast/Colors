package com.polygalov.colors;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.polygalov.colors.data.ColorItems;
import com.polygalov.colors.flavors.MainActivityFlavor;
import com.polygalov.colors.views.ColorItemListPage;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        ColorItemListPage.Listener {

    private static final int PAGE_ID_COLOR_ITEM_LIST = 1;
//    private static final int PAGE_ID_PALETTE_LIST = 2;

    @IntDef({PAGE_ID_COLOR_ITEM_LIST})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PageId {
    }

    private Toast mToast;

    private int mCurrentPageId;

    private FrameLayout mContainer;

    private ColorItemListPage mColorItemListPage;

    private MainActivityFlavor mMainActivityFlavor;

    private Toolbar mToolbar;

    private FloatingActionButton mFab;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.app_name);
        setSupportActionBar(mToolbar);

        mCurrentPageId = PAGE_ID_COLOR_ITEM_LIST;
        mColorItemListPage = new ColorItemListPage(this);
        mColorItemListPage.setListener(this);

        mContainer = findViewById(R.id.items_color);
        mContainer.addView(mColorItemListPage);

        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(this);

        mMainActivityFlavor = new MainActivityFlavor(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ColorItems.getSavedColorItems(this).size() <= 1) {
            animateFab(mFab);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideToast();
    }

    private void hideToast() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }

    private void showToast(@StringRes int resId) {
        hideToast();
        String toastText = getString(resId);
        if (!TextUtils.isEmpty(toastText)) {
            mToast = Toast.makeText(this, resId, Toast.LENGTH_SHORT);
            mToast.show();
        }
    }

    private void animateFab(final FloatingActionButton fab) {
        animateFab(fab, 400);
    }

    private void animateFab(final FloatingActionButton fab, int delay) {
        fab.postDelayed(new Runnable() {
            @Override
            public void run() {

                final long duration = 300;

                final ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(fab, View.SCALE_X, 1f, 1.2f, 1f);
                scaleXAnimator.setDuration(duration);
                scaleXAnimator.setRepeatCount(1);

                final ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(fab, View.SCALE_Y, 1f, 1.2f, 1f);
                scaleYAnimator.setDuration(duration);
                scaleYAnimator.setRepeatCount(1);

                scaleXAnimator.start();
                scaleYAnimator.start();

                final AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(scaleXAnimator).with(scaleYAnimator);
                animatorSet.start();
            }
        }, delay);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMainActivityFlavor.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        final int viewId = view.getId();

        switch (viewId) {
            case R.id.fab:
                if (mCurrentPageId == PAGE_ID_COLOR_ITEM_LIST) {
                    Intent cameraActivity = new Intent(this, CameraActivity.class);
                    startActivity(cameraActivity);
                }
                break;
            default:
                throw new IllegalArgumentException("View clicked unsupported. found: " + view);
        }
    }


    @Override
    public void onEmphasisOnAddColorActionRequested() {

    }
}
