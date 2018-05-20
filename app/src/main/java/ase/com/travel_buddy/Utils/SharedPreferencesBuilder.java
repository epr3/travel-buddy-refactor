package ase.com.travel_buddy.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferencesBuilder {
    static SharedPreferences getSharedPreferences (Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setSharedPreference (Context ctx, String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getSharedPreference (Context ctx, String key) {
        return getSharedPreferences(ctx).getString(key, "");
    }

}
