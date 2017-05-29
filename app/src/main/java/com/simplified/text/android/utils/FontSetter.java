package com.simplified.text.android.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Singleton Class used to hold fonts for entire app.
 */
public class FontSetter {

    static Context mContext;
    private static FontSetter instance;

    private static Typeface tfNexaBold;
    private static Typeface tfNexaLight;

    public static Typeface getTfNexaBold() {
        return tfNexaBold;
    }


    public static Typeface getTfNexaLight() {
        return tfNexaLight;
    }


    private FontSetter() {
        /**
         * Private constructor
         */
    }

    /**
     * Static method to initialise fonts and return this class instance.
     *
     * @param con context
     */
    public static FontSetter getFontSetter(Context con) {
        mContext = con;
        if (instance == null) {
            instance = new FontSetter();
            initializeFonts();
        }
        return instance;

    }
    /**
     * Static method to initialise fonts
     */
    private static void initializeFonts() {
        tfNexaBold = instance.setfont("Nexa Bold.otf");
        tfNexaLight = instance.setfont("Nexa Light.otf");
    }

    /**
     * Static method to initialise fonts
     */
    private Typeface setfont(String fontname) {
        return Typeface.createFromAsset(mContext.getAssets(),
//				"fonts/"+
                fontname);

    }

}
