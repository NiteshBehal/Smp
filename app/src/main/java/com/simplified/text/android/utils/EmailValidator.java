package com.simplified.text.android.utils;

import java.util.regex.Pattern;

/**
 * Singleton Class to validate Email pattern.
 */
public class EmailValidator {
    /**
     * Static instance
     */
    static EmailValidator instance = null;

    /**
     * private Constructor
     */
    private EmailValidator() {

    }

    /**
     * Static method to return the instance of this class.
     */

    public static EmailValidator getEmailValidator() {
        if (instance == null) {
            instance = new EmailValidator();
        }
        return instance;
    }

    /**
     * RegX Pattern to match input email with.
     */
    private static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(

            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$");

    /**
     * Method to return weather input email is valid or not.
     */
    public boolean validateEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

}

