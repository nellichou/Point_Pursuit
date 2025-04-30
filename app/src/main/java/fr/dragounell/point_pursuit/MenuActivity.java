package fr.dragounell.point_pursuit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

/**
 * Activité du menu du jeu.
 *
 * @author Benjamin Bribant, Maxence Raymond
 */
public class MenuActivity extends Activity {
    private String selectedGrid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button jouer = findViewById(R.id.jouer);
        Button choix = findViewById(R.id.choix);

        choix.setOnClickListener(new OuvrirListeListener(this));
        jouer.setOnClickListener(new JouerClickListener(this));

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        if(getActionBar()!=null) {
            getActionBar().setDisplayShowHomeEnabled(false);
            getActionBar().setIcon(null);
            getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            this.setSelectedGrid(data.getStringExtra("selectedGrid"));
            Toast.makeText(this, "Grille sélectionnée : " + selectedGrid, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("selectedGrid", selectedGrid);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        selectedGrid = savedInstanceState.getString("selectedGrid");
    }

    public String getSelectedGrid() {
        return selectedGrid;
    }

    public void setSelectedGrid(String selectedGrid) {
        this.selectedGrid = selectedGrid;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.preferences, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.settingsbutton) {
            Intent intent = new Intent(this, SettingsActivity.class);
            this.startActivity(intent);
            return true;
        }
        return false;
    }
}