package com.simplified.text.android.Activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Switch;

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
    private Switch swNotiSearch;

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
        swNotiSearch = (Switch) findViewById(R.id.sw_enable_notification_search);
        swNotiSearch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    generateStickyNotification();
                } else {
                    removeStickyNotification();
                }
            }
        });

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


    private void removeStickyNotification() {
        NotificationManager notifManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancel(55);
    }

    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private int notification_id;
    private RemoteViews remoteViews, remoteViewsSmall;
    private Context mContext;

    private void generateStickyNotification() {
        mContext = this;
        notification_id = 55;
        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(mContext);

//        remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.big_notification_layout);
        remoteViewsSmall = new RemoteViews(mContext.getPackageName(), R.layout.notificaatin_search_layout);

//        setupNotificationData(meaning);
//        builder.setOngoing(true);
//        builder.setAutoCancel(false);

        Intent notification_intent = new Intent(mContext, SearchPopup.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, notification_intent, 0);

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .setAutoCancel(false)
                .setCustomBigContentView(remoteViewsSmall)
                .setCustomContentView(remoteViewsSmall)
                .setContentIntent(pendingIntent);

        notificationManager.notify(notification_id, builder.build());
    }

}
