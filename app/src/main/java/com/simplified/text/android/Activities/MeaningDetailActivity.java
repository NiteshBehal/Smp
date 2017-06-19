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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.simplified.text.android.R;
import com.simplified.text.android.core.CustomHttpRequest;
import com.simplified.text.android.core.HttpResponseListener;
import com.simplified.text.android.core.URLConstants;
import com.simplified.text.android.db.DBHelper;
import com.simplified.text.android.models.MeaningModel;
import com.simplified.text.android.models.Pronunciations;
import com.simplified.text.android.models.Result;
import com.simplified.text.android.utils.AppUtils;
import com.simplified.text.android.utils.BlurBuilder;
import com.simplified.text.android.utils.CheckNetworkStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MeaningDetailActivity extends AppCompatActivity implements View.OnClickListener, HttpResponseListener {

    public static final String WORD_KEY = "NOTE_KEY";
    private Activity mActivity;
    private TextView tvTitle;
    private MeaningModel mMeaningModel;
    private LinearLayout llScrollChild;
    private ImageView ivBack;
    private ImageView ivSearch;
    private EditText etSearch;
    private DBHelper dbHelper;
    private ProgressBar pbLoading;
    private TextView tvErrorMessage;

    public static void open(Activity activity, MeaningModel word) {
        Intent intent = new Intent(activity, MeaningDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(WORD_KEY, word);
        intent.putExtras(bundle);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in_400, R.anim.fade_out_400);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        mActivity = this;
        dbHelper = new DBHelper(mActivity, DBHelper.DATABASE_NAME,
                null, DBHelper.DATABASE_VERSION);
        setContentView(R.layout.activity_meaning_details_activity);
        setWindowBg();
        prepareViews();
        mMeaningModel = (MeaningModel) getIntent().getSerializableExtra(WORD_KEY);
        if (mMeaningModel != null) {
            setMeanings();
        } else {
            tvTitle.setVisibility(View.GONE);
            findViewById(R.id.ll_search_view).setVisibility(View.VISIBLE);
            ivSearch.setVisibility(View.VISIBLE);
            etSearch.requestFocus();
            AppUtils.controlKeyboard(mActivity, true);
        }

    }

    private void prepareViews() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        llScrollChild = (LinearLayout) findViewById(R.id.ll_meaing_container);
        ivBack = (ImageView) findViewById(R.id.iv_back);

        ivSearch = (ImageView) findViewById(R.id.iv_search_meaning);
        etSearch = (EditText) findViewById(R.id.et_search_view);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading_spinner);
        tvErrorMessage = (TextView) findViewById(R.id.tv_error_dialog_message);


        ivBack.setOnClickListener(this);
        ivSearch.setOnClickListener(this);
    }


    private void setMeanings() {

        tvTitle.setText(AppUtils.toTitleCase(mMeaningModel.word));


        if (mMeaningModel.results != null && mMeaningModel.results.size() > 0) {
            LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (final Result meaning : mMeaningModel.results) {
                View meaningParent = vi.inflate(R.layout.child_meaning_detail_page, null);

                llScrollChild.addView(meaningParent);

                if (!TextUtils.isEmpty(meaning.part_of_speech)) {
                    TextView tvPos = (TextView) meaningParent.findViewById(R.id.tv_detail_part_of_speech);
                    tvPos.setVisibility(View.VISIBLE);
                    tvPos.setText(meaning.part_of_speech);
                }
                TextView tvMeaning = (TextView) meaningParent.findViewById(R.id.tv_detail_meaning);

//                tvMeaning.setCustomSelectionActionModeCallback(new ActionBarCallbacks());

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


                if (meaning.pronunciations != null && meaning.pronunciations.size() > 0) {
                    LinearLayout llPronunciationParent = (LinearLayout) meaningParent.findViewById(R.id.ll_detail_pronunciation_parent);
                    for (final Pronunciations pronunciation : meaning.pronunciations) {
                        if (!TextUtils.isEmpty(pronunciation.lang) && !TextUtils.isEmpty(pronunciation.ipa)) {
                            View pronunciationParent = vi.inflate(R.layout.child_pronunciation_layout, null);
                            llPronunciationParent.addView(pronunciationParent);
                            TextView tvLang = ((TextView) pronunciationParent.findViewById(R.id.tv_detail_pronunciation_accent));
                            tvLang.setText(pronunciation.lang);
                            TextView tvIpa = ((TextView) pronunciationParent.findViewById(R.id.tv_detail_pronunciation_ipa));
                            tvIpa.setText("|" + pronunciation.ipa + "|");
                            if (!TextUtils.isEmpty(pronunciation.url)) {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_search_meaning:
                getMeaning();
                tvErrorMessage.setText("");
                tvErrorMessage.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private void getMeaning() {

        String string = AppUtils.getTrimmedText(etSearch);
        if (string.length() == 0) {
            showError("Please enter some word to search");

        } else if (!wordExistsInDb(string)) {
            if (CheckNetworkStatus.isOnline(mActivity)) {
                try {
                    llScrollChild.removeAllViews();
                    getMeaning(string);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                showError("Please check your internet connectivity");
            }
        }
    }

    private void showError(String message) {
        if (llScrollChild.getChildCount()==0) {
            tvErrorMessage.setText(message);
            tvErrorMessage.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void getMeaning(String string) throws JSONException {
        JSONObject loginBody = new JSONObject();
        loginBody.put("meaning", string);

        CustomHttpRequest request = new CustomHttpRequest(Request.Method.POST, URLConstants.GET_MEANING_URL, URLConstants.GET_MEANING_ID, null, loginBody.toString(), MeaningModel.class, this);
        Volley.newRequestQueue(mActivity).add(request);
        pbLoading.setVisibility(View.VISIBLE);
    }

    private boolean wordExistsInDb(String word) {
        AppUtils.controlKeyboard(mActivity, false);
        ArrayList<MeaningModel> meaningModels = new ArrayList<>();
        dbHelper.getWritableDatabase();
        dbHelper.CreateTable();
        if (dbHelper.getWordMeaningList(word).size() > 0) {
            meaningModels.addAll(dbHelper.getWordMeaningList(word));
        }
        dbHelper.close();


        if (meaningModels.size() > 0) {
            mMeaningModel = meaningModels.get(0);
            try {
                llScrollChild.removeAllViews();
            } catch (Exception ex) {

            }
            setMeanings();
//            generateNotification(meaningModels.get(0));
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in_400, R.anim.fade_out_400);
    }

    @Override
    public void onResponseSuccess(Object response, int requestID) {
        pbLoading.setVisibility(View.GONE);
        if (response != null) {
            if (requestID == URLConstants.GET_MEANING_ID) {
                MeaningModel meaningModel = (MeaningModel) response;
                if (!meaningModel.error_code) {
                    mMeaningModel = meaningModel;
                    setMeanings();
                    saveDataToDb(meaningModel);
                } else {
                    showError("Unable to find meaning for "+AppUtils.getTrimmedText(etSearch));
                }
            }
        }

    }

    private void saveDataToDb(MeaningModel meaningModel) {
        dbHelper.getWritableDatabase();
        dbHelper.CreateTable();
        dbHelper.insertWordMeaning(meaningModel);
        dbHelper.close();

    }

    @Override
    public void onResponseError(VolleyError error, int requestID) {
        pbLoading.setVisibility(View.GONE);
        showError("Something went wrong, please try again");
    }

    /* class ActionBarCallbacks implements ActionMode.Callback
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
    }*/


}
