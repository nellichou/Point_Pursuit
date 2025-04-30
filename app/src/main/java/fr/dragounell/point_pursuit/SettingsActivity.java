package fr.dragounell.point_pursuit;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Activité des paramètres.
 *
 * @author Nell Telechea
 */
public class SettingsActivity extends PreferenceActivity {
    public static final String KEY_MODE_ACHROMATE= "mode_achromate";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.preferences);
        getActionBar().hide();

        SettingsObserver obs = new SettingsObserver();
        this.getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(obs);
    }
}
