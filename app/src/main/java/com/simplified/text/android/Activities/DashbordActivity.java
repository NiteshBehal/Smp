package com.simplified.text.android.Activities;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.simplified.text.android.R;
import com.simplified.text.android.Services.CBWatcherService;
import com.simplified.text.android.adapters.MyPagerAdapter;
import com.simplified.text.android.utils.BlurBuilder;

/**
 * Created by pbadmin on 8/6/17.
 */

public class DashbordActivity extends AppCompatActivity {

    private Activity mActivity;
    private NavigationTabStrip mTopNavigationTabStrip;
    private TextView tvEdit, tvClearAll;
    private boolean isInEditMode = false;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_dashbord);
        mActivity = this;
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        startService(new Intent(this, CBWatcherService.class));
        setWindowBg();
        initViews();


    }

    private void initViews() {
        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        mTopNavigationTabStrip = (NavigationTabStrip) findViewById(R.id.nts_top);

        MyPagerAdapter adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        mTopNavigationTabStrip.setViewPager(vpPager);
    }


    private void setWindowBg() {

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();

        Drawable drawable = new BitmapDrawable(getResources(), BlurBuilder.blur(mActivity, BlurBuilder.drawableToBitmap(wallpaperDrawable)));
//        ((ImageView) findViewById(R.id.bg)).setImageDrawable(drawable);
        findViewById(R.id.bg).setBackground(drawable);
    }
}
