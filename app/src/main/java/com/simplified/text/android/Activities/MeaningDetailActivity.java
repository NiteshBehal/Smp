package com.simplified.text.android.Activities;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.simplified.text.android.R;
import com.simplified.text.android.models.MeaningModel;
import com.simplified.text.android.models.Pronunciations;
import com.simplified.text.android.models.Result;
import com.simplified.text.android.utils.AppUtils;
import com.simplified.text.android.utils.BlurBuilder;



public class MeaningDetailActivity extends AppCompatActivity {

    public static final String WORD_KEY = "WORD_KEY";
    private Activity mActivity;
    private TextView tvTitle;
    private MeaningModel mMeaningModel;
    private LinearLayout llScrollChild;
    private ActionMode mActionMode;

    public static void open(Activity activity, String word) {
        Intent intent = new Intent(activity, MeaningDetailActivity.class);
        intent.putExtra(WORD_KEY, word);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        mActivity = this;
        setContentView(R.layout.activity_meaning_details_activity);
        setWindowBg();
        prepareViews();
        getMeaningFromDb(getIntent().getStringExtra(WORD_KEY));
    }
    private void prepareViews() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        llScrollChild = (LinearLayout) findViewById(R.id.ll_meaing_container);
    }

    private void getMeaningFromDb(String word) {
        /*RealmResults<MeaningModel> result = Realm.getDefaultInstance().where(MeaningModel.class).equalTo("word", word)
                .findAllAsync();
        result.addChangeListener(callback);*/
       /* if (result != null && result.size() > 0) {

        }*/
    }

   /* private OrderedRealmCollectionChangeListener<RealmResults<MeaningModel>> callback = new OrderedRealmCollectionChangeListener<RealmResults<MeaningModel>>() {
        @Override
        public void onChange(RealmResults<MeaningModel> meaningModels, OrderedCollectionChangeSet changeSet) {
            if (meaningModels != null && meaningModels.size() > 0) {
                mMeaningModel = meaningModels.get(0);
                setMeanings();
            }
        }
    };*/



    private void setMeanings() {

        tvTitle.setText(AppUtils.toTitleCase(mMeaningModel.word));

        if (mMeaningModel.results != null && mMeaningModel.results.size() > 0) {
            LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (final Result meaning : mMeaningModel.results) {
                View meaningParent = vi.inflate(R.layout.child_meaning_detail_page,null);

                llScrollChild.addView(meaningParent);

                if (!TextUtils.isEmpty(meaning.part_of_speech)) {
                    TextView tvPos = (TextView) meaningParent.findViewById(R.id.tv_detail_part_of_speech);
                    tvPos.setVisibility(View.VISIBLE);
                    tvPos.setText(meaning.part_of_speech);
                }
                TextView tvMeaning = (TextView) meaningParent.findViewById(R.id.tv_detail_meaning);

                /*tvMeaning.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        mActionMode = mActivity.startActionMode(new ActionBarCallbacks());
                        return true;
                    }
                });*/

                tvMeaning.setCustomSelectionActionModeCallback(new ActionBarCallbacks());

                tvMeaning.setText(meaning.meaning);
                if (meaning.example != null && !TextUtils.isEmpty(meaning.example.example)) {
                    LinearLayout llExampleParent = (LinearLayout) meaningParent.findViewById(R.id.ll_detail_example_parent);
                    llExampleParent.setVisibility(View.VISIBLE);
                    TextView tvExample = (TextView) llExampleParent.findViewById(R.id.tv_detail_example_text);
                    tvExample.setText(meaning.example.example);
                    if (!TextUtils.isEmpty(meaning.example.url)) {
                        ImageView ivExampleMic = (ImageView) llExampleParent.findViewById(R.id.iv_detail_example_mic);
                        ivExampleMic.setVisibility(View.VISIBLE);
                        llExampleParent.findViewById(R.id.ll_detail_example_subparent).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                sendAudioBroadcast(meaning.example.url);
                            }
                        });

                    }
                }


                if(meaning.pronunciations!=null&&meaning.pronunciations.size()>0)
                {
                    LinearLayout llPronunciationParent = (LinearLayout) meaningParent.findViewById(R.id.ll_detail_pronunciation_parent);
                    for(final Pronunciations pronunciation : meaning.pronunciations)
                    {
                        if(!TextUtils.isEmpty(pronunciation.lang)&&!TextUtils.isEmpty(pronunciation.ipa))
                        {
                            View pronunciationParent = vi.inflate(R.layout.child_pronunciation_layout, null);
                            llPronunciationParent.addView(pronunciationParent);
                            TextView tvLang = ((TextView)pronunciationParent.findViewById(R.id.tv_detail_pronunciation_accent));
                                    tvLang.setText(pronunciation.lang);
                            TextView tvIpa = ((TextView)pronunciationParent.findViewById(R.id.tv_detail_pronunciation_ipa));
                            tvIpa.setText("|"+pronunciation.ipa+"|");
                            if(!TextUtils.isEmpty(pronunciation.url))
                            {
                                ImageView ivMic = (ImageView) pronunciationParent.findViewById(R.id.iv_detail_pronunciation_mic);
                                ivMic.setVisibility(View.VISIBLE);
                                pronunciationParent.findViewById(R.id.ll_pronunciatin_accent_subparent).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        sendAudioBroadcast(pronunciation.url);
                                    }
                                });
                            }

                        }
                    }
                }


            }
        }

    }

    private void sendAudioBroadcast(String url) {
        Intent audioBroadcastIntent = new Intent("mic_click");
        audioBroadcastIntent.putExtra("mp3Url", url);
        sendBroadcast(audioBroadcastIntent);
    }

    private void setWindowBg() {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();

        Drawable drawable = new BitmapDrawable(getResources(), BlurBuilder.blur(mActivity, BlurBuilder.drawableToBitmap(wallpaperDrawable)));
        findViewById(R.id.ll_parent).setBackground(drawable);
    }



    class ActionBarCallbacks implements ActionMode.Callback
    {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.contextual_menu, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            int id = item.getItemId();
            if(id == R.id.item_search)
            {
//                tv.setText("");
                Toast.makeText(mActivity,"option deleted",Toast.LENGTH_LONG).show();
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    }



}
