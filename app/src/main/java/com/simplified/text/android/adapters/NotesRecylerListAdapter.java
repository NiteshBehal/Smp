package com.simplified.text.android.adapters;

import android.app.Activity;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.simplified.text.android.R;
import com.simplified.text.android.db.DBHelper;
import com.simplified.text.android.interfaces.DashbordActivityEventsListener;
import com.simplified.text.android.models.NotesModel;
import com.simplified.text.android.utils.AppUtils;
import com.simplified.text.android.utils.HtmlUtil;
import com.simplified.text.android.utils.ResizeWidthAnimation;

import java.util.List;


public class NotesRecylerListAdapter extends RecyclerView.Adapter implements DashbordActivityEventsListener {
    private final List<NotesModel> mNotesList;
    private Activity mActivity;
    private LayoutInflater mInflater;
    private boolean isInEditMode = false;
    private boolean isAnimating = false;
    private int mLastDeletedPosition = -1;
    private NotesModel mLastDeletedNote = null;
    private DBHelper dbHelper;
    private Handler handler;
    private View rootViewForSnakbar;
    private Snackbar snackbar;

    public NotesRecylerListAdapter(Activity activity, List<NotesModel> notesList) {
        mActivity = activity;
        mInflater = mActivity.getLayoutInflater();
        mNotesList = notesList;
        dbHelper = new DBHelper(mActivity, DBHelper.DATABASE_NAME,
                null, DBHelper.DATABASE_VERSION);
    }

    @Override
    public int getItemCount() {
        return mNotesList.size();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.child_notes_list, parent, false);
        rootViewForSnakbar = parent;
        return new ViewHolderListItem(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolderListItem listHolder = (ViewHolderListItem) holder;
        final NotesModel noteModel = mNotesList.get(position);
        listHolder.tvNote.setText(HtmlUtil.fromHtml(noteModel.notes).toString());
        listHolder.tvDate.setText(noteModel.date);

        final View view = holder.itemView;
        final int itemPosition = position;

        listHolder.ivDelete.setVisibility(View.VISIBLE);
        listHolder.ivNext.setVisibility(View.VISIBLE);
        if (isAnimating) {
            if (isInEditMode) {

                ResizeWidthAnimation showAnimation = new ResizeWidthAnimation(listHolder.ivDelete, (int) AppUtils.pxFromDp(mActivity, 35));
                showAnimation.setDuration(400);
                listHolder.ivDelete.startAnimation(showAnimation);

                ResizeWidthAnimation hideAnimation = new ResizeWidthAnimation(listHolder.ivNext, 0);
                hideAnimation.setDuration(400);
                hideAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        isAnimating = false;
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                listHolder.ivNext.startAnimation(hideAnimation);


            } else {
                ResizeWidthAnimation showAnimation = new ResizeWidthAnimation(listHolder.ivNext, (int) AppUtils.pxFromDp(mActivity, 35));
                showAnimation.setDuration(400);
                listHolder.ivNext.startAnimation(showAnimation);

                ResizeWidthAnimation hideAnimation = new ResizeWidthAnimation(listHolder.ivDelete, 0);
                hideAnimation.setDuration(400);
                hideAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        isAnimating = false;
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                listHolder.ivDelete.startAnimation(hideAnimation);
            }
        } else {
            if (isInEditMode) {

                ResizeWidthAnimation showAnimation = new ResizeWidthAnimation(listHolder.ivDelete, (int) AppUtils.pxFromDp(mActivity, 35));
                showAnimation.setDuration(0);
                listHolder.ivDelete.startAnimation(showAnimation);

                ResizeWidthAnimation hideAnimation = new ResizeWidthAnimation(listHolder.ivNext, 0);
                hideAnimation.setDuration(0);
                listHolder.ivNext.startAnimation(hideAnimation);

                listHolder.ivDelete.setVisibility(View.VISIBLE);
                listHolder.ivNext.setVisibility(View.GONE);
            } else {

                ResizeWidthAnimation showAnimation = new ResizeWidthAnimation(listHolder.ivNext, (int) AppUtils.pxFromDp(mActivity, 35));
                showAnimation.setDuration(400);
                listHolder.ivNext.startAnimation(showAnimation);

                ResizeWidthAnimation hideAnimation = new ResizeWidthAnimation(listHolder.ivDelete, 0);
                hideAnimation.setDuration(0);
                listHolder.ivDelete.startAnimation(hideAnimation);

                listHolder.ivDelete.setVisibility(View.GONE);
                listHolder.ivNext.setVisibility(View.VISIBLE);
            }
        }

        listHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View nview) {
                nview.setOnClickListener(null);
                try {
                    if (mLastDeletedNote != null) {
                        deleteNoteFromDb(mLastDeletedNote);
                    }
                    mLastDeletedPosition = itemPosition;
                    mLastDeletedNote = noteModel;
                    mNotesList.remove(mLastDeletedNote);
                    notifyItemRemoved(mLastDeletedPosition);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            notifyDataSetChanged();
                        }
                    }, 500);
                    showSnackbar(rootViewForSnakbar, mLastDeletedNote.notes);
                } catch (Exception ex) {
                }
            }
        });


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isInEditMode) {
//                    MeaningDetailActivity.open(mActivity, noteModel);
                }
            }
        });

    }


    private class ViewHolderListItem extends RecyclerView.ViewHolder {
        private TextView tvNote, tvDate;
        private ImageView ivDelete, ivNext;

        public ViewHolderListItem(View itemView) {
            super(itemView);
            tvNote = (TextView) itemView.findViewById(R.id
                    .tv_child_note_list_note);

            tvDate = (TextView) itemView.findViewById(R.id
                    .tv_child_note_list_date);

            ivDelete = (ImageView) itemView.findViewById(R.id.iv_child_note_list_delete);
            ivNext = (ImageView) itemView.findViewById(R.id.iv_child_note_list_next);

        }
    }


    private void deleteNoteFromDb(NotesModel note) {

        if (mLastDeletedNote != null) {
            dbHelper.getWritableDatabase();
            dbHelper.CreateTable();
            dbHelper.removeNote(note.notesId);
            dbHelper.close();

            mLastDeletedNote = null;
            mLastDeletedPosition = -1;
        }
    }

    final
    private void showSnackbar(View rootView, String note) {

        String stringToShow = HtmlUtil.fromHtml(note).toString();
        if (stringToShow.length() > 0) {
            stringToShow = stringToShow.substring(0, 30) + "...";
        }
        String message = mActivity.getString(R.string.snackbar_item_deleted,
                stringToShow);
        snackbar = Snackbar.make(rootView, message, 2900);
        snackbar.setAction(R.string.snackbar_undo_item_deleted, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLastDeletedNote != null) {
                    mNotesList.add(mLastDeletedPosition, mLastDeletedNote);
                    if (mLastDeletedPosition == 0 || mLastDeletedPosition == getItemCount() - 1) {
                        ((RecyclerView) rootViewForSnakbar).scrollToPosition(mLastDeletedPosition);
                    }
                    notifyItemInserted(mLastDeletedPosition);
                    mLastDeletedPosition = -1;
                    mLastDeletedNote = null;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            notifyDataSetChanged();

                        }
                    }, 500);
                }
            }
        }).show();

        try {
            handler.removeCallbacksAndMessages(null);
        } catch (Exception ex) {
        }

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                deleteNoteFromDb(mLastDeletedNote);


            }
        }, 2900);
    }


    @Override
    public void isEditMode(boolean isEditable) {
        this.isInEditMode = isEditable;
        notifyDataSetChanged();
        isAnimating = true;

    }

    @Override
    public void pageChanged() {
        try {
            if(mLastDeletedNote!=null) {
                deleteNoteFromDb(mLastDeletedNote);
            }
            snackbar.dismiss();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
