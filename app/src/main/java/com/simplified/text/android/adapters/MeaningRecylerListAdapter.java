package com.simplified.text.android.adapters;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.simplified.text.android.Activities.MeaningDetailActivity;
import com.simplified.text.android.R;
import com.simplified.text.android.db.DBHelper;
import com.simplified.text.android.models.MeaningModel;
import com.simplified.text.android.models.Pronunciations;
import com.simplified.text.android.models.Result;
import com.simplified.text.android.utils.AppUtils;
import com.simplified.text.android.utils.ResizeWidthAnimation;

import java.util.List;


public class MeaningRecylerListAdapter extends RecyclerView.Adapter {
    private final List<MeaningModel> mMeaningList;
    private Activity mActivity;
    private LayoutInflater mInflater;
//    private RecyclerView.ViewHolder holder;
    private boolean isInEditMode = false;
    private boolean isAnimating = false;
    private int mLastDeletedPosition = -1;
    private MeaningModel mLastDeletedMeaning = null;
    private DBHelper dbHelper;

    public MeaningRecylerListAdapter(Activity activity, List<MeaningModel> meaningList) {
        mActivity = activity;
        mInflater = mActivity.getLayoutInflater();
        mMeaningList = meaningList;
        dbHelper = new DBHelper(mActivity, DBHelper.DATABASE_NAME,
                null, DBHelper.DATABASE_VERSION);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.child_meaning_list, parent, false);
        return new ViewHolderListItem(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolderListItem listHolder = (ViewHolderListItem) holder;
        final MeaningModel meaningModel = mMeaningList.get(position);
        listHolder.tvWord.setText(meaningModel.word);
        listHolder.tvPartOfSpeech.setText("");
        listHolder.tvFirstMeaning.setText("");

        final View view = holder.itemView;
        final int itemPosition = position;
        if (meaningModel.results != null) {
            for (Result result : meaningModel.results) {
                if (TextUtils.isEmpty(result.meaning)) {
                    continue;
                } else {
                    String partOfSpeech = "";
                    if (result.pronunciations != null && result.pronunciations.size() > 0) {
                        for (Pronunciations pronunciations : result.pronunciations) {
                            if (!TextUtils.isEmpty(pronunciations.ipa)) {
                                partOfSpeech = "|" + pronunciations.ipa + "| ";
                                if (!TextUtils.isEmpty(pronunciations.url)) {
//                                remoteViews.setImageViewResource(R.id.iv_play, R.drawable.mic);
//                                remoteViews.setOnClickPendingIntent(R.id.iv_play,setClickIntent(pronunciations.url));

                                }
                                break;
                            }
                        }
                    }
                    if (!TextUtils.isEmpty(result.part_of_speech)) {
                        partOfSpeech = partOfSpeech + result.part_of_speech;
                    }

                    listHolder.tvPartOfSpeech.setText(partOfSpeech);
                    listHolder.tvFirstMeaning.setText(result.meaning);
                    break;
                }
            }
        }
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
//                if(mLastDeletedPosition!=position) {


                ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredHeight(), 0);
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        int val = (Integer) valueAnimator.getAnimatedValue();
                        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                        layoutParams.height = val;
                        view.setLayoutParams(layoutParams);
                        if (val == 0) {
                            try {
                                if (mLastDeletedMeaning != null) {
//                                    deleteMeaningFromDb(mLastDeletedMeaning);
                                }
                                mLastDeletedPosition = itemPosition;
                                mLastDeletedMeaning = meaningModel;
                                mMeaningList.remove(itemPosition);
                                notifyDataSetChanged();
//                                showSnackbar(parent.getRootView(), mLastDeletedMeaning.word);
                            } catch (Exception ex) {
//                                    ex.printStackTrace();
                            }
                        }
                    }
                });
                anim.setDuration(200);
                anim.start();

//                }
            }


        });


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isInEditMode) {
                    MeaningDetailActivity.open(mActivity, meaningModel.word);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mMeaningList.size();
    }


    private class ViewHolderListItem extends RecyclerView.ViewHolder {
        private TextView tvWord, tvPartOfSpeech, tvFirstMeaning;
        private ImageView ivDelete, ivNext;
        public ViewHolderListItem(View itemView) {
            super(itemView);
            tvWord = (TextView) itemView.findViewById(R.id
                    .tv_child_meaning_list_word_text);

            tvPartOfSpeech = (TextView) itemView.findViewById(R.id
                    .tv_child_meaning_list_part_of_speech_text);
            tvFirstMeaning = (TextView) itemView.findViewById(R.id
                    .tv_child_meaning_list_word_first_meaning);

            ivDelete = (ImageView) itemView.findViewById(R.id.iv_child_meaning_list_delete);
            ivNext = (ImageView) itemView.findViewById(R.id.iv_child_meaning_list_next);

        }
    }
}
