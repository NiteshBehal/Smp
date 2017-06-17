package com.simplified.text.android.Activities;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.simplified.text.android.R;
import com.simplified.text.android.Services.CBWatcherService;
import com.simplified.text.android.adapters.MyPagerAdapter;
import com.simplified.text.android.interfaces.DashbordActivityEventsListener;
import com.simplified.text.android.utils.AppUtils;
import com.simplified.text.android.utils.BlurBuilder;
import com.simplified.text.android.utils.ResizeWidthAnimation;
import com.simplified.text.android.widgets.NonSwipeableViewPager;
import com.simplified.text.android.widgets.jjSearchView.JJSearchView;
import com.simplified.text.android.widgets.jjSearchView.anim.controller.JJChangeArrowController;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by pbadmin on 8/6/17.
 */

public class DashbordActivity extends AppCompatActivity implements View.OnClickListener {

    private Activity mActivity;
    private NavigationTabStrip mTopNavigationTabStrip;
    private TextView tvEdit, tvClearAll;
    private boolean isInEditMode = false;
    private boolean isInSearchMode = false;
    private NonSwipeableViewPager vpPager;
    private MyPagerAdapter adapterViewPager;
    private JJSearchView mJJSearchView;
    private LinearLayout llSearchView, llTitleView;
    private EditText etSearch;
    private Timer timer;
    private LinearLayout llTopBar;
    private AppBarLayout.LayoutParams topParams;
    private int mActionBarSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_dashbord);
        mActivity = this;
        getActionbarHightDim();
        startService(new Intent(this, CBWatcherService.class));
        setWindowBg();
        initViews();


    }

    private void getActionbarHightDim() {
        TypedArray styledAttributes = getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        mActionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
    }

    private void initViews() {
        vpPager = (NonSwipeableViewPager) findViewById(R.id.vpPager);
        mTopNavigationTabStrip = (NavigationTabStrip) findViewById(R.id.nts_top);
        tvEdit = (TextView) findViewById(R.id.tv_child_meaning_edit_done);
        mJJSearchView = (JJSearchView) findViewById(R.id.jjsv);
        llSearchView = (LinearLayout) findViewById(R.id.ll_search_view);
        llTitleView = (LinearLayout) findViewById(R.id.ll_edit_title_view);
        etSearch = (EditText) findViewById(R.id.et_search_view);
        llTopBar = (LinearLayout) findViewById(R.id.ll_top_scroll_bar);
        topParams = (AppBarLayout.LayoutParams) llTopBar.getLayoutParams();

        etSearch.addTextChangedListener(new TextChangeListener());
        mJJSearchView.setController(new JJChangeArrowController());
        tvEdit.setOnClickListener(this);
        mJJSearchView.setOnClickListener(this);


        setupPager();

    }


    private class TextChangeListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (timer != null)
                timer.cancel();
        }

        @Override
        public void afterTextChanged(final Editable searchKey) {

            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timer.cancel();
                            getCurrentListener(0).performSearch(searchKey.toString().trim());
                            getCurrentListener(1).performSearch(searchKey.toString().trim());
                        }
                    });
                }

            }, 400);

        }
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
                    getCurrentListener(0).pageChanged();
                    getCurrentListener(1).pageChanged();
//                    getCurrentListener(0).isEditMode(isInEditMode);
//                    getCurrentListener(1).isEditMode(isInEditMode);
                } catch (Exception ex) {

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
                getCurrentListener(0).isEditMode(isInEditMode);
                getCurrentListener(1).isEditMode(isInEditMode);
//                getCurrentListener(vpPager.getCurrentItem()).isEditMode(isInEditMode);
                break;

            case R.id.jjsv:

                if (isInEditMode) {
                    onClick(tvEdit);
                }

                isInSearchMode = !isInSearchMode;
                if (isInSearchMode) {
                    vpPager.setPagingEnabled(false);
                    topParams.setScrollFlags(0);
                    mJJSearchView.startAnim();
                } else {
                    vpPager.setPagingEnabled(true);
                    topParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                            | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                    mJJSearchView.resetAnim();
                    if (etSearch.getText().toString().length() > 0) {
                        etSearch.setText("");
                    }
                    AppUtils.controlKeyboard(mActivity, false);
                }

                showSearchView(isInSearchMode);
                animateTabs(isInSearchMode);
                break;

            default:
                break;
        }
    }

    private void animateTabs(boolean isInSearchMode) {
        ValueAnimator anim;

        if(isInSearchMode)
        {
            anim = ValueAnimator.ofInt(mActionBarSize, 0);
        }else
        {
            anim = ValueAnimator.ofInt( 0,mActionBarSize);
        }
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = mTopNavigationTabStrip.getLayoutParams();
                layoutParams.height = val;
                mTopNavigationTabStrip.setLayoutParams(layoutParams);

            }
        });
        anim.setDuration(400);
        anim.start();
    }

    private void showSearchView(final boolean isInSearchMode) {
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_400);
        Animation fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out_400);

        fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isInSearchMode) {
                    llTitleView.setVisibility(View.GONE);
                    etSearch.requestFocus();
                    AppUtils.controlKeyboard(mActivity, true);
                } else {
                    llSearchView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        if (isInSearchMode) {
            llSearchView.setVisibility(View.VISIBLE);
            llSearchView.startAnimation(fadeInAnimation);
            llTitleView.startAnimation(fadeOutAnimation);
        } else {
            llTitleView.setVisibility(View.VISIBLE);
            llSearchView.startAnimation(fadeOutAnimation);
            llTitleView.startAnimation(fadeInAnimation);
        }


    }

    private DashbordActivityEventsListener getCurrentListener(int position) {
        return (DashbordActivityEventsListener) adapterViewPager.instantiateItem(vpPager, position);
    }

    @Override
    public void onBackPressed() {
        if (isInEditMode) {
            onClick(tvEdit);
        } else if (isInSearchMode) {
            onClick(mJJSearchView);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isInSearchMode) {
            getCurrentListener(0).performSearch(AppUtils.getTrimmedText(etSearch));
            getCurrentListener(1).performSearch(AppUtils.getTrimmedText(etSearch));
           /* String string = AppUtils.getTrimmedText(etSearch);
            etSearch.setText("");
            etSearch.setText(string);*/
        }
    }
}
