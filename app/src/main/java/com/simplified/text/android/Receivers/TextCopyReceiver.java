package com.simplified.text.android.Receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.simplified.text.android.Activities.MainActivity;
import com.simplified.text.android.R;
import com.simplified.text.android.core.CustomHttpRequest;
import com.simplified.text.android.core.HttpResponseListener;
import com.simplified.text.android.core.URLConstants;
import com.simplified.text.android.db.DBHelper;
import com.simplified.text.android.models.MeaningModel;
import com.simplified.text.android.models.NotesModel;
import com.simplified.text.android.models.Pronunciations;
import com.simplified.text.android.models.Result;
import com.simplified.text.android.utils.CheckNetworkStatus;
import com.simplified.text.android.utils.HtmlUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class TextCopyReceiver extends BroadcastReceiver implements HttpResponseListener {
    public static final String CUSTOM_INTENT = "simplified.textcopied";
    public static final String TEXT_COPIED_KEY = "TEXT_COPIED_KEY";
    public static final String TEXT_IS_HTML_KEY = "TEXT_IS_HTML_KEY";

    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private int notification_id;
    private RemoteViews remoteViews, remoteViewsSmall;
    private Context mContext;

    private DBHelper dbHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        dbHelper = new DBHelper(mContext, DBHelper.DATABASE_NAME,
                null, DBHelper.DATABASE_VERSION);

        if (intent.getAction().equals(CUSTOM_INTENT)) {
            String origionalString = intent.getStringExtra(TEXT_COPIED_KEY).trim().toLowerCase();
            String isHtml = intent.getStringExtra(TEXT_IS_HTML_KEY).trim().toLowerCase();
            String string = "";
            if (isHtml.equalsIgnoreCase("true")) {
                string = HtmlUtil.fromHtml(intent.getStringExtra(TEXT_COPIED_KEY).trim().toLowerCase()).toString();
            } else {
                string = origionalString;
            }
//            Log.d(">>>>>", string);

            if (!TextUtils.isEmpty(string)) {
                if (HtmlUtil.isValidURL(string)) {
                    Toast.makeText(mContext, "Is Url", Toast.LENGTH_SHORT).show();
                } else if (!string.contains(" ")) {
                    if (!wordExistsInRealm(string)) {
                        if (CheckNetworkStatus.isOnline(context)) {
                            try {
                                getMeaning(string);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
//                    Toast.makeText(mContext, "Is string", Toast.LENGTH_SHORT).show();

//                    getPackageName();

                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yy hh:mm aa");
                    String formattedDate = df.format(c.getTime());

                    NotesModel notesModel = new NotesModel();
                    notesModel.notes = origionalString;
                    notesModel.isHtml = isHtml;
                    notesModel.date = formattedDate;

                    dbHelper.getWritableDatabase();
                    dbHelper.CreateTable();
                    dbHelper.insertNotes(notesModel);
                    dbHelper.close();

                    Toast.makeText(mContext, "Note Saved Successfully", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }


    private boolean wordExistsInRealm(String word) {

        ArrayList<MeaningModel> meaningModels = new ArrayList<>();
        dbHelper.getWritableDatabase();
        dbHelper.CreateTable();
        if (dbHelper.getWordMeaningList(word).size() > 0) {
            meaningModels.addAll(dbHelper.getWordMeaningList(word));
        }
        dbHelper.close();


        if (meaningModels.size() > 0) {
            generateNotification(meaningModels.get(0));
            return true;
        }
        return false;
    }

    private void getMeaning(String string) throws JSONException {
        JSONObject loginBody = new JSONObject();
        loginBody.put("meaning", string);

        CustomHttpRequest request = new CustomHttpRequest(Request.Method.POST, URLConstants.GET_MEANING_URL, URLConstants.GET_MEANING_ID, null, loginBody.toString(), MeaningModel.class, this);
        Volley.newRequestQueue(mContext).add(request);
    }

    @Override
    public void onResponseSuccess(Object response, int requestID) {
        if (response != null) {
            if (requestID == URLConstants.GET_MEANING_ID) {
                MeaningModel meaningModel = (MeaningModel) response;
                if (!meaningModel.error_code) {
                    generateNotification(meaningModel);
                    saveDataToDb(meaningModel);
                } else {
                    Toast.makeText(mContext, "Unable to find meaning.", Toast.LENGTH_SHORT).show();
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

    }


    private void generateNotification(MeaningModel meaning) {
        notification_id = (int) System.currentTimeMillis();
        notificationManager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(mContext);

        remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.big_notification_layout);
        remoteViewsSmall = new RemoteViews(mContext.getPackageName(), R.layout.small_notification_layout);

        setupNotificationData(meaning);


        Intent notification_intent = new Intent(mContext, MainActivity.class);

       /* TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(MeaningDetailActivity.class);
        stackBuilder.addNextIntent(notification_intent);*/
//        notification_intent.putExtra(MeaningDetailActivity.WORD_KEY, meaning.word);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, notification_intent, 0);

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setCustomBigContentView(remoteViews)
                .setCustomContentView(remoteViewsSmall)
                .setContentIntent(pendingIntent);

        notificationManager.notify(notification_id, builder.build());
    }

    private void setupNotificationData(MeaningModel meaning) {
        remoteViews.setTextViewText(R.id.tv_notification_word_text, meaning.word);
        remoteViewsSmall.setTextViewText(R.id.tv_notification_word_text, meaning.word);


        for (Result result : meaning.results) {
//            if ((TextUtils.isEmpty(result.part_of_speech) && (result.pronunciations == null || result.pronunciations.size() == 0)) || TextUtils.isEmpty(result.meaning)) {
            if (TextUtils.isEmpty(result.meaning)) {
                continue;
            } else {
                String partOfSpeech = "";
                if (result.pronunciations != null && result.pronunciations.size() > 0) {
                    for (Pronunciations pronunciations : result.pronunciations) {
                        if (!TextUtils.isEmpty(pronunciations.ipa)) {
                            partOfSpeech = "|" + pronunciations.ipa + "| ";
                            if (!TextUtils.isEmpty(pronunciations.url)) {
                                remoteViews.setImageViewResource(R.id.iv_play, R.drawable.mic);
                                remoteViews.setOnClickPendingIntent(R.id.notification_mic_parent, setClickIntent(pronunciations.url));
                            }
                            break;
                        }
                    }
                }
                if (!TextUtils.isEmpty(result.part_of_speech)) {
                    partOfSpeech = partOfSpeech + result.part_of_speech;
                }

                remoteViews.setTextViewText(R.id.tv_notification_word_first_meaning, result.meaning);
                remoteViewsSmall.setTextViewText(R.id.tv_notification_word_first_meaning, result.meaning);
                remoteViews.setTextViewText(R.id.tv_notification_part_of_speech_text, partOfSpeech);
                break;
            }
        }

    }

    private PendingIntent setClickIntent(String url) {
        Intent button_intent = new Intent("mic_click");
        button_intent.putExtra("mp3Url", url);
        PendingIntent button_pending_event = PendingIntent.getBroadcast(mContext, notification_id,
                button_intent, 0);
        return button_pending_event;
    }


}


//        Intent button_intent = new Intent("button_click");
//        button_intent.putExtra("id",notification_id);
//        PendingIntent button_pending_event = PendingIntent.getBroadcast(context,notification_id,
//                button_intent,0);

//        remoteViews.setOnClickPendingIntent(R.id.button,button_pending_event);


//            remoteViews.setImageViewResource(R.id.notif_icon,R.mipmap.ic_launcher);
//            remoteViews.setTextViewText(R.id.notif_title,"TEXT");
//            remoteViews.setProgressBar(R.id.progressBar,100,40,true);