package com.polygalov.colors.flavors;

import android.view.Menu;
import android.view.MenuItem;

import com.polygalov.colors.MainActivity;

/**
 * Created by Константин on 19.02.2018.
 */

public class MainActivityFlavor {

    private MainActivity mMainActivity;

    public MainActivityFlavor(MainActivity mainActivity) {
        mMainActivity = mainActivity;
    }

    public boolean onCreateOptionsMenu(final Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }
}
