package com.simplified.text.android.Activities;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.simplified.text.android.R;
import com.simplified.text.android.models.NotesModel;
import com.simplified.text.android.utils.BlurBuilder;
import com.simplified.text.android.utils.HtmlUtil;
import com.simplified.text.android.utils.SharedPreferenceManager;
import com.simplified.text.android.widgets.ColorPickerDialog;

import java.util.ArrayList;


public class NotesDetailActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String NOTE_KEY = "NOTE_KEY";
    private Activity mActivity;
    private TextView tvTitle;
    private NotesModel mNoteModel;

    private ScrollView svParentScroll;
    private WebView wvWebView;
    private TextView tvNote, tvDate;
    private LinearLayout llwvParent;
    private ImageView ivEditHighlighter;
    private ArrayList<Integer> closestColorsList = new ArrayList<>();
    private SharedPreferenceManager sharedPreferenceManager;


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
        setContentView(R.layout.activity_notes_details_activity);
        mActivity = this;
        sharedPreferenceManager = new SharedPreferenceManager(mActivity, null);
        setWindowBg();

        setHighlighterColors();
        prepareViews();
        mNoteModel = (NotesModel) getIntent().getSerializableExtra(NOTE_KEY);
        setNotes();

    }

    private void setHighlighterColors() {
        closestColorsList.clear();
        closestColorsList.add(Color.parseColor("#CCe67e22"));
        closestColorsList.add(Color.parseColor("#F1C40F"));
        closestColorsList.add(Color.parseColor("#CC2ecc71"));
        closestColorsList.add(Color.parseColor("#990DD5FC"));
        closestColorsList.add(Color.parseColor("#99FF0099"));
        closestColorsList.add(Color.parseColor("#996E0DD0"));
        if(sharedPreferenceManager.getInt(SharedPreferenceManager.HIGHLIGHTER_COLOR,0)==0)
        {
            sharedPreferenceManager.setInt(SharedPreferenceManager.HIGHLIGHTER_COLOR,closestColorsList.get(0));
        }
    }

    private void prepareViews() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        ivEditHighlighter = (ImageView) findViewById(R.id.iv_edit_highlighter_color);

        wvWebView = (WebView) findViewById(R.id.wv_notes_details_webview);
        svParentScroll = (ScrollView) findViewById(R.id.sv_notes_details_scroll);
        llwvParent = (LinearLayout) findViewById(R.id.ll_notes_details_webview);

        tvNote = (TextView) findViewById(R.id
                .tv_child_note_list_note);
        tvDate = (TextView) findViewById(R.id
                .tv_child_note_list_date);

        WebSettings settings = wvWebView.getSettings();
        settings.setJavaScriptEnabled(true);

        ivEditHighlighter.setOnClickListener(this);
        setHeighlightImageColor();

    }

    private void setHeighlightImageColor() {
        ivEditHighlighter.setColorFilter(sharedPreferenceManager.getInt(SharedPreferenceManager.HIGHLIGHTER_COLOR,0));
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


    private void setWindowBg() {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();

        Drawable drawable = new BitmapDrawable(getResources(), BlurBuilder.blur(mActivity, BlurBuilder.drawableToBitmap(wallpaperDrawable)));
        findViewById(R.id.ll_parent).setBackground(drawable);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_edit_highlighter_color:
                openColorPiker();
                break;
            default:
                break;
        }
    }

    private void openColorPiker() {
        ColorPickerDialog dialog = ColorPickerDialog.newInstance(
                ColorPickerDialog.SELECTION_SINGLE,
                closestColorsList,
                3,
                ColorPickerDialog.SIZE_SMALL);
        dialog.setDefaultColor(sharedPreferenceManager.getInt(SharedPreferenceManager.HIGHLIGHTER_COLOR,0));

        dialog.setOnDialodButtonListener(new ColorPickerDialog.OnDialogButtonListener() {
            @Override
            public void onDonePressed(ArrayList<Integer> mSelectedColors) {
                sharedPreferenceManager.setInt(SharedPreferenceManager.HIGHLIGHTER_COLOR,mSelectedColors.get(0));
                setHeighlightImageColor();
            }

            @Override
            public void onDismiss() {

            }
        });

        dialog.show(getFragmentManager(), "some_tag");
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
