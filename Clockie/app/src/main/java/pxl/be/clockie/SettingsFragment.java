package pxl.be.clockie;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if(preference instanceof NumberPickerPreference){
            preference.setSummary(sharedPreferences.getInt(key, 0)+"");
        } else{
            String ringtoneString = sharedPreferences.getString(key, "");
            Uri ringtoneUri = Uri.parse(ringtoneString);
            Ringtone ringtone = RingtoneManager.getRingtone(getContext(), ringtoneUri);
            String ringtoneName = ringtone.getTitle(getContext());
            preference.setSummary(ringtoneName);
        }
    }

    //    For proper lifecycle management in the activity, register and unregister your SharedPreferences.OnSharedPreferenceChangeListener during the onResume() and onPause() callbacks
    // Maar omdat RingtonePreference met een intent antwoordt, pauzeert dit SettingsFragment waardoor unregister wordt opgeroepen en dus de
    // onSharedPreferenceChanged niet -> daarom onStart en onStop
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
