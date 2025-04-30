package fr.dragounell.point_pursuit;

import android.content.SharedPreferences;

import androidx.annotation.Nullable;

/**
 * Observateur pour les paramètres.
 *
 * @author Maxence Raymond
 */
public class SettingsObserver implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String s) {
        boolean achromate = sharedPreferences.getBoolean(s,false);
    }
}
