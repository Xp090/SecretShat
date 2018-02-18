package me.xp090.secretshat;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.google.gson.Gson;

import me.xp090.secretshat.Util.PasscodeUtil;

import static me.xp090.secretshat.Util.SharedPreferencesUtil.SecOptions;
import static me.xp090.secretshat.Util.SharedPreferencesUtil.SecurityPreferences;


public class SettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        PrefFragment fragment = new PrefFragment();
        Bundle bundle = getIntent().getExtras();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings_container, fragment).commit();
    }


    public static class PrefFragment extends PreferenceFragmentCompat {
        public static final String SEC_PREF = "sec_pref";
        public static final int REQ_PASSCODE = 6454;
        SharedPreferences.Editor mPrefEditor;
        ListPreference method = (ListPreference) findPreference("sec_method");
        ListPreference pattern = (ListPreference) findPreference("sec_pattern_size");
        ListPreference pin = (ListPreference) findPreference("sec_pin_length");
        Preference setup = findPreference("sec_setup");

        public PrefFragment() {

        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.pref, rootKey);
            mPrefEditor = SecurityPreferences.edit();
            method = (ListPreference) findPreference("sec_method");
            pattern = (ListPreference) findPreference("sec_pattern_size");
            pin = (ListPreference) findPreference("sec_pin_length");
            setup = findPreference("sec_setup");
            method.setValue(SecOptions.SecurityMethod);
            pattern.setValue(String.valueOf(SecOptions.PatternSize));
            pin.setValue(String.valueOf(SecOptions.PinLength));

            prepareListeners();
            onSecurityMethod(method.getValue());
        }

        private void onSecurityMethod(String value) {
            switch (value) {
                case ("none"):
                    pattern.setEnabled(false);
                    pin.setEnabled(false);
                    setup.setEnabled(false);
                    SecOptions.SecurityMethod = value;
                    SecOptions.PassCode = null;
                    mPrefEditor.putString("Security", new Gson().toJson(SecOptions)).apply();
                    break;
                case ("pattern"):
                    pattern.setEnabled(true);
                    pin.setEnabled(false);
                    setup.setEnabled(true);

                    break;
                case ("pin"):
                    pattern.setEnabled(false);
                    pin.setEnabled(true);
                    setup.setEnabled(true);
                    break;
            }
        }

        private void prepareListeners() {
            method.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    onSecurityMethod(newValue.toString());
                    return true;
                }
            });

            setup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    SecOptions.SecurityMethod = method.getValue();
                    SecOptions.PatternSize = Integer.parseInt(pattern.getValue());
                    SecOptions.PinLength = Integer.parseInt(pin.getValue());
                    Intent intent = new Intent(getActivity(), SecurityActivity.class);
                    intent.putExtra(PasscodeUtil.SECURITY_MODE, PasscodeUtil.MODE_RECORDING);
                    startActivityForResult(intent, REQ_PASSCODE);
                    return false;
                }
            });

        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQ_PASSCODE && resultCode == RESULT_OK) {
                mPrefEditor.putString("Security", new Gson().toJson(SecOptions)).apply();
            }

        }
    }
}



