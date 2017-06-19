package com.simplified.text.android.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.simplified.text.android.R;
import com.simplified.text.android.adapters.NotesRecylerListAdapter;
import com.simplified.text.android.db.DBHelper;
import com.simplified.text.android.interfaces.AnimateFabListener;
import com.simplified.text.android.interfaces.DashbordActivityEventsListener;
import com.simplified.text.android.models.NotesModel;

import java.util.ArrayList;
import java.util.List;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_FLING;
import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;

public class NotesFragment extends Fragment implements DashbordActivityEventsListener {

    private DBHelper dbHelper;
    private List<NotesModel> notesList = new ArrayList<>();
    private NotesRecylerListAdapter notesAdapter;
    private View mRootView;
    private Activity mActivity;
    private RecyclerView rvNotesList;


    public static NotesFragment newInstance() {
        NotesFragment fragmentFirst = new NotesFragment();
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
        rvNotesList = (RecyclerView) mRootView.findViewById(R.id.rv_meaning_list);

        notesAdapter = new NotesRecylerListAdapter(mActivity, notesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        rvNotesList.setLayoutManager(mLayoutManager);
        rvNotesList.setItemAnimator(new DefaultItemAnimator());
        rvNotesList.setAdapter(notesAdapter);

        rvNotesList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == SCROLL_STATE_TOUCH_SCROLL || newState == SCROLL_STATE_FLING) {
                    ((AnimateFabListener)mActivity).hideFab(true);

                } else {

                    ((AnimateFabListener)mActivity).hideFab(false);

                }

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getNotesListFromDb("");
    }

    private void getNotesListFromDb(String searchKey) {
        notesList.clear();
        dbHelper.getWritableDatabase();
        dbHelper.CreateTable();
        if(dbHelper.getNotesList(searchKey)!=null&&dbHelper.getNotesList(searchKey).size()>0)
        {
            notesList.addAll(dbHelper.getNotesList(searchKey));
        }

        dbHelper.close();
        notesAdapter.notifyDataSetChanged();

    }

    @Override
    public void isEditMode(boolean isEditable) {
        notesAdapter.isEditMode(isEditable);
    }

    @Override
    public void pageChanged() {
//        getNotesListFromDb();
        notesAdapter.pageChanged();
    }

    @Override
    public void performSearch(String searchKey) {
        getNotesListFromDb(searchKey);
    }
}