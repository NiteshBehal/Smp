package com.simplified.text.android.adapters;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.simplified.text.android.Activities.MeaningDetailActivity;
import com.simplified.text.android.R;
import com.simplified.text.android.models.MeaningModel;
import com.simplified.text.android.models.Pronunciations;
import com.simplified.text.android.models.Result;
import com.simplified.text.android.utils.AppUtils;
import com.simplified.text.android.utils.ResizeWidthAnimation;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmResults;

public class MeaningListAdapter extends BaseAdapter {
    private final List<MeaningModel> mMeaningList;
    private Activity mActivity;
    private LayoutInflater mInflater;
    private ViewHolder holder;
    private boolean isInEditMode = false;
    private boolean isAnimating = false;
    private int mLastDeletedPosition = -1;
    private MeaningModel mLastDeletedMeaning = null;

    public MeaningListAdapter(Activity activity, List<MeaningModel> meaningList) {
        mActivity = activity;
        mInflater = mActivity.getLayoutInflater();
        mMeaningList = meaningList;
    }

    @Override
    public int getCount() {
        return mMeaningList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final View view;
        if (convertView == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.child_meaning_list, parent, false);

            holder.tvWord = (TextView) view.findViewById(R.id
                    .tv_child_meaning_list_word_text);

            holder.tvPartOfSpeech = (TextView) view.findViewById(R.id
                    .tv_child_meaning_list_part_of_speech_text);
            holder.tvFirstMeaning = (TextView) view.findViewById(R.id
                    .tv_child_meaning_list_word_first_meaning);

            holder.ivDelete = (ImageView) view.findViewById(R.id.iv_child_meaning_list_delete);
            holder.ivNext = (ImageView) view.findViewById(R.id.iv_child_meaning_list_next);


            view.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            view = convertView;
        }

        final MeaningModel meaningModel = mMeaningList.get(position);
        holder.tvWord.setText(meaningModel.word);
        holder.tvPartOfSpeech.setText("");
        holder.tvFirstMeaning.setText("");

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

                holder.tvPartOfSpeech.setText(partOfSpeech);
                holder.tvFirstMeaning.setText(result.meaning);
                break;
            }
        }
        holder.ivDelete.setVisibility(View.VISIBLE);
        holder.ivNext.setVisibility(View.VISIBLE);
        if (isAnimating) {
            if (isInEditMode) {

                ResizeWidthAnimation showAnimation = new ResizeWidthAnimation(holder.ivDelete, (int) AppUtils.pxFromDp(mActivity, 35));
                showAnimation.setDuration(400);
                holder.ivDelete.startAnimation(showAnimation);

                ResizeWidthAnimation hideAnimation = new ResizeWidthAnimation(holder.ivNext, 0);
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
                holder.ivNext.startAnimation(hideAnimation);


            } else {
                ResizeWidthAnimation showAnimation = new ResizeWidthAnimation(holder.ivNext, (int) AppUtils.pxFromDp(mActivity, 35));
                showAnimation.setDuration(400);
                holder.ivNext.startAnimation(showAnimation);

                ResizeWidthAnimation hideAnimation = new ResizeWidthAnimation(holder.ivDelete, 0);
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
                holder.ivDelete.startAnimation(hideAnimation);
            }
        } else {
            if (isInEditMode) {

                ResizeWidthAnimation showAnimation = new ResizeWidthAnimation(holder.ivDelete, (int) AppUtils.pxFromDp(mActivity, 35));
                showAnimation.setDuration(0);
                holder.ivDelete.startAnimation(showAnimation);

                ResizeWidthAnimation hideAnimation = new ResizeWidthAnimation(holder.ivNext, 0);
                hideAnimation.setDuration(0);
                holder.ivNext.startAnimation(hideAnimation);

                holder.ivDelete.setVisibility(View.VISIBLE);
                holder.ivNext.setVisibility(View.GONE);
            } else {

                ResizeWidthAnimation showAnimation = new ResizeWidthAnimation(holder.ivNext, (int) AppUtils.pxFromDp(mActivity, 35));
                showAnimation.setDuration(400);
                holder.ivNext.startAnimation(showAnimation);

                ResizeWidthAnimation hideAnimation = new ResizeWidthAnimation(holder.ivDelete, 0);
                hideAnimation.setDuration(0);
                holder.ivDelete.startAnimation(hideAnimation);

                holder.ivDelete.setVisibility(View.GONE);
                holder.ivNext.setVisibility(View.VISIBLE);
            }
        }

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
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
                                    deleteMeaningFromDb(mLastDeletedMeaning);
                                }
                                mLastDeletedPosition = position;
                                mLastDeletedMeaning = meaningModel;
                                mMeaningList.remove(position);
                                notifyDataSetChanged();
                                showSnackbar(parent.getRootView(), mLastDeletedMeaning.word);
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

        return view;
    }

    Handler handler;

    private void showSnackbar(View rootView, final String word) {
        String message = mActivity.getString(R.string.snackbar_item_deleted,
                word);
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_undo_item_deleted, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mLastDeletedMeaning != null) {
                            mMeaningList.add(mLastDeletedPosition, mLastDeletedMeaning);
                            mLastDeletedPosition = -1;
                            mLastDeletedMeaning = null;
                            notifyDataSetChanged();
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

                deleteMeaningFromDb(mLastDeletedMeaning);


            }
        }, 3000);
    }

    private void deleteMeaningFromDb(final MeaningModel word) {
        if (mLastDeletedMeaning != null) {
            Realm realm = Realm.getDefaultInstance();
            final RealmResults<MeaningModel> results = realm.where(MeaningModel.class).equalTo("word", word.word).findAll();
            realm.beginTransaction();
            results.deleteFirstFromRealm();
            realm.commitTransaction();

            mLastDeletedMeaning = null;
            mLastDeletedPosition = -1;
            /*realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
//                    realm.getSchema().remove("MeaningModel");
                    realm.beginTransaction();
                    results.deleteFirstFromRealm();
                    realm.commitTransaction();

                    mLastDeletedMeaning = null;
                    mLastDeletedPosition = -1;
                }
            });*/
        }

    }

    public void setEditMode(boolean isInEditMode) {
        this.isInEditMode = isInEditMode;
        notifyDataSetChanged();
        isAnimating = true;
    }

    /**
     * Holder class for view holder pattern
     */
    private static class ViewHolder {
        private TextView tvWord, tvPartOfSpeech, tvFirstMeaning;
        private ImageView ivDelete, ivNext;
    }
}
