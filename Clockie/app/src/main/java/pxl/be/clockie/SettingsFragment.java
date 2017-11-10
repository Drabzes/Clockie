package pxl.be.clockie;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        for(int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            Preference preference = getPreferenceScreen().getPreference(i);
            if(preference instanceof NumberPickerPreference) {
                String key = preference.getKey();
                int value = sharedPreferences.getInt(key, 0) + 60;
                preference.setSummary(value+"");
            } else{
                String key = preference.getKey();
                String ringtoneString = sharedPreferences.getString(key, "");
                Uri ringtoneUri = Uri.parse(ringtoneString);
                Ringtone ringtone = RingtoneManager.getRingtone(getContext(), ringtoneUri);
                String ringtoneName = ringtone.getTitle(getContext());
                preference.setSummary(ringtoneName);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if(preference instanceof NumberPickerPreference){
            preference.setSummary(sharedPreferences.getInt(key, 0) +"");
        } else{
            String ringtoneString = sharedPreferences.getString(key, "");
            Uri ringtoneUri = Uri.parse(ringtoneString);
            Ringtone ringtone = RingtoneManager.getRingtone(getContext(), ringtoneUri);
            String ringtoneName = ringtone.getTitle(getContext());
            preference.setSummary(ringtoneName);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

}
