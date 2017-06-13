package com.simplified.text.android.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.simplified.text.android.R;
import com.simplified.text.android.adapters.MeaningRecylerListAdapter;
import com.simplified.text.android.db.DBHelper;
import com.simplified.text.android.models.MeaningModel;

import java.util.ArrayList;
import java.util.List;

public class MeaningFragment extends Fragment {

    private DBHelper dbHelper;
    private List<MeaningModel> meaningList = new ArrayList<>();
    private MeaningRecylerListAdapter meaningAdapter;
    private View mRootView;
    private Activity mActivity;
    private RecyclerView rvMeaningList;


    public static MeaningFragment newInstance(int page, String title) {
        MeaningFragment fragmentFirst = new MeaningFragment();
        return fragmentFirst;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_meaning_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRootView = getView();
        mActivity = getActivity();
        dbHelper = new DBHelper(mActivity, DBHelper.DATABASE_NAME,
                null, DBHelper.DATABASE_VERSION);
        prepareViews();


    }

    private void prepareViews() {
        rvMeaningList = (RecyclerView) mRootView.findViewById(R.id.rv_meaning_list);

        meaningAdapter = new MeaningRecylerListAdapter(mActivity, meaningList);
        rvMeaningList.setAdapter(meaningAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        getMeaningListFromDb();
    }

    private void getMeaningListFromDb() {
        meaningList.clear();
        dbHelper.getWritableDatabase();
        dbHelper.CreateTable();
        if(dbHelper.getWordMeaningList(null)!=null&&dbHelper.getWordMeaningList(null).size()>0)
        {
            meaningList.addAll(dbHelper.getWordMeaningList(null));
        }

        dbHelper.close();
        meaningAdapter.notifyDataSetChanged();


        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            lvMeaningList.setNestedScrollingEnabled(true);
        }*/
    }
}