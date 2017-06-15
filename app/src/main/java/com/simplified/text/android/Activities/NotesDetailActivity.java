package com.simplified.text.android.Activities;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
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
import com.simplified.text.android.db.DBHelper;
import com.simplified.text.android.models.HighlighterModel;
import com.simplified.text.android.models.NotesModel;
import com.simplified.text.android.utils.BlurBuilder;
import com.simplified.text.android.utils.HtmlUtil;
import com.simplified.text.android.utils.SharedPreferenceManager;
import com.simplified.text.android.widgets.ColorPickerDialog;

import java.util.ArrayList;
import java.util.List;


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


    private ActionMode mActionMode;
    private List<HighlighterModel> highlighters = new ArrayList<>();
    private DBHelper dbHelper;

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
        dbHelper = new DBHelper(mActivity, DBHelper.DATABASE_NAME,
                null, DBHelper.DATABASE_VERSION);
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
        if (sharedPreferenceManager.getInt(SharedPreferenceManager.HIGHLIGHTER_COLOR, 0) == 0) {
            sharedPreferenceManager.setInt(SharedPreferenceManager.HIGHLIGHTER_COLOR, closestColorsList.get(0));
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

        tvNote.setCustomSelectionActionModeCallback(new ActionBarCallbacks());

    }

    private void setHeighlightImageColor() {
        ivEditHighlighter.setColorFilter(sharedPreferenceManager.getInt(SharedPreferenceManager.HIGHLIGHTER_COLOR, 0));
    }


    private void setNotes() {

        Spannable heighlightedText = setHighlightText(HtmlUtil.fromHtml(mNoteModel.notes));

        svParentScroll.setVisibility(View.VISIBLE);
        tvNote.setText(heighlightedText);
        tvDate.setText(mNoteModel.date);

       /* if (mNoteModel.isHtml.equalsIgnoreCase("true")) {
//            llwvParent.setVisibility(View.VISIBLE);
            svParentScroll.setVisibility(View.VISIBLE);
            tvNote.setText(HtmlUtil.fromHtml(mNoteModel.notes).toString());
//            wvWebView.loadData(mNoteModel.notes, "text/html; charset=utf-8", "UTF-8");
        } else {
            svParentScroll.setVisibility(View.VISIBLE);
            tvNote.setText(HtmlUtil.fromHtml(mNoteModel.notes));
            tvDate.setText(mNoteModel.date);
        }*/

    }

    private Spannable setHighlightText(Spanned spanned) {


        highlighters.clear();
        dbHelper.getWritableDatabase();
        dbHelper.CreateTable();
        if (dbHelper.getHighlighterList(mNoteModel.notesId) != null && dbHelper.getHighlighterList(mNoteModel.notesId).size() > 0) {
            highlighters.addAll(dbHelper.getHighlighterList(mNoteModel.notesId));
        }
        dbHelper.close();

        Spannable spannable = new SpannableString(spanned);
        if (highlighters.size() > 0) {
            for (HighlighterModel highlighterModel : highlighters) {
                spannable.setSpan(new BackgroundColorSpan(highlighterModel.hColor), highlighterModel.startPoint, highlighterModel.endPoint,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

        }


        return spannable;
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
        dialog.setDefaultColor(sharedPreferenceManager.getInt(SharedPreferenceManager.HIGHLIGHTER_COLOR, 0));

        dialog.setOnDialodButtonListener(new ColorPickerDialog.OnDialogButtonListener() {
            @Override
            public void onDonePressed(ArrayList<Integer> mSelectedColors) {
                sharedPreferenceManager.setInt(SharedPreferenceManager.HIGHLIGHTER_COLOR, mSelectedColors.get(0));
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
            MenuItem menuOpen = menu.findItem(R.id.item_unhighlight);
            if (highlighters.size() == 0 || !checkIfUnlightRequired()) {
                menuOpen.setVisible(false);
            } else {
                menuOpen.setVisible(true);
            }

            return false;
        }

        private boolean checkIfUnlightRequired() {
            int startPoint = tvNote.getSelectionStart();
            /*if(startPoint == 0 )
            {
                startPoint = 1;
            }*/
            int endPoint = tvNote.getSelectionEnd();
//            boolean show = false;
            for (HighlighterModel highlighter : highlighters) {
                if ((startPoint < highlighter.startPoint && highlighter.endPoint < endPoint) || (highlighter.startPoint <= startPoint && startPoint <= highlighter.endPoint) || (highlighter.startPoint <= endPoint && endPoint <= highlighter.endPoint)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int startPoint = tvNote.getSelectionStart();
            int endPoint = tvNote.getSelectionEnd();

            int id = item.getItemId();
            if (id == R.id.item_highlight) {


                /*HighlighterModel highlighter = new HighlighterModel();
                highlighter.noteId = mNoteModel.notesId;
                highlighter.startPoint = tvNote.getSelectionStart();
                highlighter.endPoint = tvNote.getSelectionEnd();
                highlighter.hColor = sharedPreferenceManager.getInt(SharedPreferenceManager.HIGHLIGHTER_COLOR, 0);

                dbHelper.getWritableDatabase();
                dbHelper.CreateTable();
                dbHelper.insertNoteHighlighter(highlighter);
                dbHelper.close();
                setNotes();*/


                setExistingHighlights(startPoint, endPoint);
                HighlighterModel newHighlighter = new HighlighterModel();
                newHighlighter.startPoint = startPoint;
                newHighlighter.endPoint = endPoint;
                newHighlighter.hColor = sharedPreferenceManager.getInt(SharedPreferenceManager.HIGHLIGHTER_COLOR, 0);
                newHighlighter.noteId = mNoteModel.notesId;

                dbHelper.getWritableDatabase();
                dbHelper.CreateTable();
                dbHelper.insertNoteHighlighter(newHighlighter);
                dbHelper.close();
                setNotes();
                Toast.makeText(mActivity, "Highlight Success", Toast.LENGTH_LONG).show();
            } else if (id == R.id.item_unhighlight) {
                setExistingHighlights(startPoint, endPoint);
                setNotes();
                Toast.makeText(mActivity, "Unhighlight Success", Toast.LENGTH_LONG).show();
            }


            return false;
        }

        private void setExistingHighlights(int startPoint, int endPoint) {
            List<HighlighterModel> hToDelete = new ArrayList<>();
            List<HighlighterModel> hToInsert = new ArrayList<>();

            for (HighlighterModel highlighter : highlighters) {

                if (startPoint == highlighter.startPoint && highlighter.endPoint == endPoint) {
                    hToDelete.add(highlighter);
                } else if (startPoint < highlighter.startPoint && highlighter.endPoint < endPoint) {
                    hToDelete.add(highlighter);
                } else if (highlighter.startPoint < startPoint && endPoint < highlighter.endPoint) {
                    hToDelete.add(highlighter);

                    HighlighterModel firstPart = new HighlighterModel();
                    firstPart.startPoint = highlighter.startPoint;
                    firstPart.endPoint = startPoint;
                    firstPart.hColor = highlighter.hColor;
                    firstPart.noteId = highlighter.noteId;
                    hToInsert.add(firstPart);

                    HighlighterModel secondPart = new HighlighterModel();
                    secondPart.startPoint = endPoint;
                    secondPart.endPoint = highlighter.endPoint;
                    secondPart.hColor = highlighter.hColor;
                    secondPart.noteId = highlighter.noteId;
                    hToInsert.add(secondPart);

                } else if (highlighter.startPoint < startPoint && startPoint < highlighter.endPoint) {
                    hToDelete.add(highlighter);

                    HighlighterModel firstPart = new HighlighterModel();
                    firstPart.startPoint = highlighter.startPoint;
                    firstPart.endPoint = startPoint;
                    firstPart.hColor = highlighter.hColor;
                    firstPart.noteId = highlighter.noteId;
                    hToInsert.add(firstPart);

                } else if (highlighter.startPoint < endPoint && endPoint < highlighter.endPoint) {
                    hToDelete.add(highlighter);
                    HighlighterModel secondPart = new HighlighterModel();
                    secondPart.startPoint = endPoint;
                    secondPart.endPoint = highlighter.endPoint;
                    secondPart.hColor = highlighter.hColor;
                    secondPart.noteId = highlighter.noteId;
                    hToInsert.add(secondPart);
                } else if (startPoint == highlighter.startPoint && highlighter.endPoint < endPoint) {
                    hToDelete.add(highlighter);
                } else if (startPoint < highlighter.startPoint && endPoint == highlighter.endPoint) {
                    hToDelete.add(highlighter);
                }

            }


            dbHelper.getWritableDatabase();
            dbHelper.CreateTable();
//            dbHelper.removeNoteHighlighters(mNoteModel.notesId);

            for (HighlighterModel highlighter : hToDelete) {
                dbHelper.removeNoteHighlighter(highlighter.highlighterId);
            }

            for (HighlighterModel highlighter : hToInsert) {
                dbHelper.insertNoteHighlighter(highlighter);
            }

            dbHelper.close();
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    }


}
