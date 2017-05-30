package com.simplified.text.android.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.simplified.text.android.R;


/**
 * Created by nitesh.behal on 8/28/2015.
 */
public class AppUtils {
    private static boolean showLog = false;

    private AppUtils() {
        // Empty Constructor
    }

    public static String toTitleCase(String input) {
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }

            titleCase.append(c);
        }

        return titleCase.toString();
    }

    /**
     * Method to get trimmed text of a view
     *
     * @param edittext edittext view to get trimmed text
     */
    public static String getTrimmedText(EditText edittext) {
        return edittext.getText().toString().trim();
    }

    /**
     * Method to show or hide soft keyboard.
     *
     * @param activity activity to get current focused view
     * @param show     boolean to identify weather to hide or show the soft keyboard.
     */
    public static void controlKeyboard(Activity activity, boolean show) {
        try {
            View view = activity.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (show) {
                    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Method to convert dp to px
     *
     * @param context context
     * @param dp      value
     */
    public static float pxFromDp(final Context context, final float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }


    /**
     * Method to show error dialogue.
     *
     * @param context current activity context.
     * @param msg     message to display
     */
    @SuppressLint("NewApi")
    public static void showError(Context context, String msg) {


        final Dialog dialog = new Dialog(context, R.style.NewDialog);
        dialog.setContentView(R.layout.dialog_error_layout);

        TextView tvDilogMessage = (TextView) dialog.findViewById(R.id
                .tv_error_dialog_message);

        tvDilogMessage.setText(msg);
        dialog.findViewById(R.id.tv_error_dialog_yes)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }

                });
        dialog.findViewById(R.id.div).setVisibility(View.GONE);
        dialog.findViewById(R.id.tv_error_dialog_no).setVisibility(View.GONE);
        dialog.show();


    }

    public static void setupStatusbarColor(Activity activity, String color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }

}
