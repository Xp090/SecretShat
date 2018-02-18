package me.xp090.secretshat.Util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import me.xp090.secretshat.DataModels.SecurityOptions;

import static android.content.Context.MODE_PRIVATE;
import static me.xp090.secretshat.SettingsActivity.PrefFragment.SEC_PREF;

/**
 * Created by Xp090 on 31/12/2017.
 */

public class SharedPreferencesUtil {
    public static SharedPreferences SecurityPreferences;
    public static SecurityOptions SecOptions;

    public static boolean initSecurityOptions(Context context) {
        SecurityPreferences = context.getSharedPreferences(SEC_PREF, MODE_PRIVATE);
        String securityJson = SecurityPreferences.getString("Security", "");
        if (securityJson == "") {
            SecOptions = new SecurityOptions();
        } else {
            SecOptions = new Gson().fromJson(securityJson, SecurityOptions.class);

        }
        return !SecOptions.SecurityMethod.equals("none");
    }

    public static void restSecurityOptions() {
        SecurityPreferences.edit().clear().apply();
        SecOptions = null;
    }
}
