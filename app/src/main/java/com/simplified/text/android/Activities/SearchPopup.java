package com.simplified.text.android.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.simplified.text.android.R;
import com.simplified.text.android.widgets.OwlView;


/**
 * Created by pbadmin on 30/5/17.
 */

public class SearchPopup extends AppCompatActivity {


    private OwlView owlView;
    private boolean isOpen = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_popup);
        getSupportActionBar().hide();

        owlView = (OwlView) findViewById(R.id.owl_view);
        animateOwl();

    }

    private void animateOwl() {
        try {
            if (isOpen) {
                owlView.open();
            } else {
                owlView.close();
            }
            isOpen = !isOpen;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    animateOwl();
                }
            }, 3000);

        } catch (Exception ex) {

        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
