package com.simplified.text.android.Activities;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ListView;

import com.simplified.text.android.R;
import com.simplified.text.android.Services.CBWatcherService;
import com.simplified.text.android.adapters.MeaningListAdapter;
import com.simplified.text.android.models.MeaningModel;
import com.simplified.text.android.utils.BlurBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private Realm realm;
    private Activity mActivity;
    private List<MeaningModel> meaningList = new ArrayList<>();
    private ListView lvMeaningList;
    private MeaningListAdapter meaningAdapter;
    private RealmResults<MeaningModel> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        mActivity = this;
        setContentView(R.layout.activity_main);
        setWindowBg();
        startService(new Intent(this, CBWatcherService.class));
        realm = Realm.getDefaultInstance();
        prepareViews();


    }

    private void prepareViews() {
        lvMeaningList = (ListView) findViewById(R.id.lv_meaning_list);
        meaningAdapter = new MeaningListAdapter(mActivity, meaningList);
        lvMeaningList.setAdapter(meaningAdapter);
    }

    private void setWindowBg() {

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();

        Drawable drawable = new BitmapDrawable(getResources(), BlurBuilder.blur(MainActivity.this, BlurBuilder.drawableToBitmap(wallpaperDrawable)));
        ((ImageView) findViewById(R.id.bg)).setImageDrawable(drawable);
    }

    @Override
    protected void onStart() {
        super.onStart();
        result = realm.where(MeaningModel.class)
                .findAllAsync();
        result.addChangeListener(callback);
    }

    private OrderedRealmCollectionChangeListener<RealmResults<MeaningModel>> callback = new OrderedRealmCollectionChangeListener<RealmResults<MeaningModel>>() {
        @Override
        public void onChange(RealmResults<MeaningModel> meaningModels, OrderedCollectionChangeSet changeSet) {
//            Toast.makeText(mActivity, "" + meaningModels.size(), Toast.LENGTH_SHORT).show();
            if (meaningModels != null && meaningModels.size() > 0) {
                meaningList.clear();
                meaningList.addAll(meaningModels);
                Collections.reverse(meaningList);
                meaningAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        result.removeAllChangeListeners();
    }
}
