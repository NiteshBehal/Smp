package com.simplified.text.android.Activities;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.simplified.text.android.R;
import com.simplified.text.android.models.NotesModel;
import com.simplified.text.android.utils.BlurBuilder;
import com.simplified.text.android.utils.HtmlUtil;


public class NotesDetailActivity extends AppCompatActivity {

    public static final String NOTE_KEY = "NOTE_KEY";
    private Activity mActivity;
    private TextView tvTitle;
    private NotesModel mNoteModel;

    private ScrollView svParentScroll;
    private WebView wvWebView;
    private TextView tvNote, tvDate;
    private LinearLayout llwvParent;


//    private ActionMode mActionMode;

    public static void open(Activity activity, NotesModel note) {
        Intent intent = new Intent(activity, NotesDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(NOTE_KEY, note);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        mActivity = this;
        setContentView(R.layout.activity_notes_details_activity);
        setWindowBg();
        prepareViews();
        mNoteModel = (NotesModel) getIntent().getSerializableExtra(NOTE_KEY);
        setNotes();

    }

    private void prepareViews() {
        tvTitle = (TextView) findViewById(R.id.tv_title);

        wvWebView = (WebView) findViewById(R.id.wv_notes_details_webview);
        svParentScroll = (ScrollView) findViewById(R.id.sv_notes_details_scroll);
        llwvParent = (LinearLayout)findViewById(R.id.ll_notes_details_webview);

        tvNote = (TextView) findViewById(R.id
                .tv_child_note_list_note);
        tvDate = (TextView) findViewById(R.id
                .tv_child_note_list_date);

        WebSettings settings = wvWebView.getSettings();
        settings.setJavaScriptEnabled(true);



    }


    private void setNotes() {
        if (mNoteModel.isHtml.equalsIgnoreCase("true")) {
//            llwvParent.setVisibility(View.VISIBLE);
            svParentScroll.setVisibility(View.VISIBLE);
            tvNote.setText(HtmlUtil.fromHtml(mNoteModel.notes).toString());
//            wvWebView.loadData(mNoteModel.notes, "text/html; charset=utf-8", "UTF-8");
        } else {
            svParentScroll.setVisibility(View.VISIBLE);
            tvNote.setText(HtmlUtil.fromHtml(mNoteModel.notes));
            tvDate.setText(mNoteModel.date);
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


    class ActionBarCallbacks implements ActionMode.Callback {

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
            if (id == R.id.item_search) {
//                tv.setText("");
                Toast.makeText(mActivity, "option deleted", Toast.LENGTH_LONG).show();
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    }


}
