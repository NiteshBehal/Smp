package com.simplified.text.android.adapters;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.simplified.text.android.Activities.MeaningDetailActivity;
import com.simplified.text.android.R;
import com.simplified.text.android.models.MeaningModel;
import com.simplified.text.android.models.Pronunciations;
import com.simplified.text.android.models.Result;

import java.util.List;

/**
 * Created by mohitjain on 4/9/17.
 */
public class MeaningListAdapter extends BaseAdapter {
    private final List<MeaningModel> mMeaningList;
    private Activity mActivity;
    private LayoutInflater mInflater;
    private ViewHolder holder;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.child_meaning_list, parent, false);

            holder.tvWord = (TextView) view.findViewById(R.id
                    .tv_child_meaning_list_word_text);

            holder.tvPartOfSpeech = (TextView) view.findViewById(R.id
                    .tv_child_meaning_list_part_of_speech_text);
            holder.tvFirstMeaning = (TextView) view.findViewById(R.id
                    .tv_child_meaning_list_word_first_meaning);


            view.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            view = convertView;
        }

        final MeaningModel meaningModel = mMeaningList.get(position);
        holder.tvWord.setText(meaningModel.word);
        holder.tvPartOfSpeech.setText("");
        holder.tvFirstMeaning.setText("");

       /* for (Result result : meaningModel.results) {
            if (TextUtils.isEmpty(result.part_of_speech) || TextUtils.isEmpty(result.meaning)) {
                continue;
            } else {
                String partOfSpeech = "";
                if (result.pronunciations != null && result.pronunciations.size() > 0) {
                    for (Pronunciations pronunciations : result.pronunciations) {
                        if (!TextUtils.isEmpty(pronunciations.ipa)) {
                            partOfSpeech = "|" + pronunciations.ipa + "| ";
                            break;
                        }
                    }
                }
                partOfSpeech = partOfSpeech + result.part_of_speech;

                holder.tvPartOfSpeech.setText(partOfSpeech);
                holder.tvFirstMeaning.setText(result.meaning);
                break;
            }
        }*/


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


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MeaningDetailActivity.open(mActivity, meaningModel.word);
            }
        });

        return view;
    }

    /**
     * Holder class for view holder pattern
     */
    private static class ViewHolder {
        private TextView tvWord, tvPartOfSpeech, tvFirstMeaning;
    }
}
