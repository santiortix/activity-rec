package com.startapps.activityrec.preference;

import java.util.Set;

import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceManager;

public class PreferenceCompat
{
	// private static final String TAG = "PreferenceCompat";

    private static final PreferenceCompatImpl IMPL;

    static {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 11/* Build.VERSION_CODES.HONEYCOMB */) {
            IMPL = new PreferenceCompatImplHoneycomb();
        } else {
            IMPL = new PreferenceCompatImplGB();
        }
    }

    private PreferenceCompat() {
        // hide constructor
    }

    /**
     * Attempts to get a persisted set of Strings from the
     * {@link android.content.SharedPreferences}.
     * <p>
     * This will check if this Preference is persistent, get the
     * SharedPreferences from the {@link PreferenceManager}, and get the value.
     * 
     * @param pref Preference to attempts to get a persisted set of Strings.
     * @param defaultReturnValue The default value to return if either the
     *            Preference is not persistent or the Preference is not in the
     *            shared preferences.
     * @return The value from the SharedPreferences or the default return value.
     */
    public static boolean persistStringSet(Preference pref, Set<String> values) {
        return IMPL.persistStringSet(pref, values);
    }

    /**
     * Attempts to get a persisted set of Strings from the
     * {@link android.content.SharedPreferences}.
     * <p>
     * This will check if this Preference is persistent, get the
     * SharedPreferences from the {@link PreferenceManager}, and get the value.
     * 
     * @param pref Preference to attempts to get a persisted set of Strings.
     * @param defaultReturnValue The default value to return if either the
     *            Preference is not persistent or the Preference is not in the
     *            shared preferences.
     * @return The value from the SharedPreferences or the default return value.
     */
    public static Set<String> getPersistedStringSet(
            Preference pref, Set<String> defaultReturnValue) {
        return IMPL.getPersistedStringSet(pref, defaultReturnValue);
    }
}
