package fr.dragounell.point_pursuit;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;


/**
 * Activité du jeu
 *
 * @author Maxence Raymond
 */
public class GameActivity extends Activity {
    private LogicGrid grid;
    private ViewGrid view;
    private String filename;
    private boolean grayscale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        this.filename = getIntent().getStringExtra("filename");
        if (this.filename == null) {
            throw new IllegalStateException("Must have an intent with desired grid filename");
        }
        XmlGridLoader loader = new XmlGridLoader(this);
        this.grid = loader.getGrid(this.filename);
        this.view = this.findViewById(R.id.Game);
        this.view.setGrid(this.grid);
        ((TextView) this.findViewById(R.id.GameName)).setText(loader.getNames().get(filename));

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        this.grayscale = sp.getBoolean("grayscale", false);
        this.view.setGrayScale(this.grayscale);
        if (getActionBar() != null) {
            getActionBar().hide();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("IsSet", true);
        outState.putString("gridName", this.filename);
        outState.putSerializable("lines", this.grid.getSerializableLines());
        outState.putBoolean("Win", this.grid.isVictory());
        outState.putBoolean("grayscale", this.grayscale);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey("IsSet")) {
            XmlGridLoader loader = new XmlGridLoader(this.getApplicationContext());
            String gridname = savedInstanceState.getString("gridName");
            this.grid = loader.getGrid(gridname);
            this.view.setGrid(this.grid);
            this.view.setGrayScale(savedInstanceState.getBoolean("grayscale", false));
            List<Line> lines = (List<Line>) savedInstanceState.getSerializable("lines");
            this.grid.restoreLines(lines);
            if (savedInstanceState.getBoolean("Win")) {
                this.view.victory();
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }
}