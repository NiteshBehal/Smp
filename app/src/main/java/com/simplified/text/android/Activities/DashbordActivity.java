package com.simplified.text.android.Activities;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.simplified.text.android.R;
import com.simplified.text.android.Services.CBWatcherService;
import com.simplified.text.android.adapters.MyPagerAdapter;
import com.simplified.text.android.interfaces.DashbordActivityEventsListener;
import com.simplified.text.android.utils.BlurBuilder;

/**
 * Created by pbadmin on 8/6/17.
 */

public class DashbordActivity extends AppCompatActivity implements View.OnClickListener {

    private Activity mActivity;
    private NavigationTabStrip mTopNavigationTabStrip;
    private TextView tvEdit, tvClearAll;
    private boolean isInEditMode = false;
    private ViewPager vpPager;
    private MyPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_dashbord);
        mActivity = this;
        startService(new Intent(this, CBWatcherService.class));
        setWindowBg();
        initViews();


    }

    private void initViews() {
        vpPager = (ViewPager) findViewById(R.id.vpPager);
        mTopNavigationTabStrip = (NavigationTabStrip) findViewById(R.id.nts_top);
        tvEdit = (TextView) findViewById(R.id.tv_child_meaning_edit_done);
        tvEdit.setOnClickListener(this);

        setupPager();
    }

    private void setupPager() {
      adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        mTopNavigationTabStrip.setViewPager(vpPager);

        mTopNavigationTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                try {
                    getCurrentListener().pageChanged();
                    getCurrentListener().isEditMode(isInEditMode);
                }
                catch (Exception ex)
                {

                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void setWindowBg() {

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();

        Drawable drawable = new BitmapDrawable(getResources(), BlurBuilder.blur(mActivity, BlurBuilder.drawableToBitmap(wallpaperDrawable)));
//        ((ImageView) findViewById(R.id.bg)).setImageDrawable(drawable);
        findViewById(R.id.bg).setBackground(drawable);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.tv_child_meaning_edit_done:
                isInEditMode = !isInEditMode;
                if (isInEditMode) {
                    tvEdit.setText("Done");
                } else {
                    tvEdit.setText("Edit");
                }
                getCurrentListener().isEditMode(isInEditMode);
                break;
            default:
                break;
        }
    }

    private DashbordActivityEventsListener getCurrentListener()
    {
        return (DashbordActivityEventsListener) adapterViewPager.instantiateItem(vpPager, vpPager.getCurrentItem());
    }

}
