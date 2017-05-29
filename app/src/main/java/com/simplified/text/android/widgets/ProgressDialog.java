package com.simplified.text.android.widgets;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.simplified.text.android.R;


public class ProgressDialog extends Dialog {
    /**
     * Custom progress dialogue
     */

    public ProgressDialog(Context context) {
        super(context, R.style.NewDialog);
    }

    public static ProgressDialog show(Context context, CharSequence title, CharSequence message) {
        return show(context, title, message, false);
    }

    public static ProgressDialog show(Context context, CharSequence title, CharSequence message, boolean indeterminate) {
        return show(context, title, message, indeterminate, false, null);
    }

    public static ProgressDialog show(Context context, CharSequence title, CharSequence message, boolean indeterminate, boolean cancelable) {
        return show(context, title, message, indeterminate, cancelable, null);
    }

    public static ProgressDialog show(Context context, CharSequence title, CharSequence message, boolean indeterminate, boolean cancelable, OnCancelListener cancelListener) {

        ProgressDialog dialog = new ProgressDialog(context);

        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        /* The next line will add the ProgressBar to the dialog. */
        LinearLayout ll = new LinearLayout(context);
        ll.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setGravity(Gravity.CENTER);
        ll.addView(new TextView(context), new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        ll.addView(new ProgressBar(context), new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        dialog.addContentView(ll, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        ((TextView) ll.getChildAt(0)).setBackgroundColor(Color.TRANSPARENT);
        ((TextView) ll.getChildAt(0)).setTextColor(Color.WHITE);
        ((TextView) ll.getChildAt(0)).setTextSize(20);
        if (message != null) {
            ((TextView) ll.getChildAt(0)).setText(message);
        }
        dialog.show();
        return dialog;
    }


}