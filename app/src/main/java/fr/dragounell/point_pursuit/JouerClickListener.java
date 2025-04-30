package fr.dragounell.point_pursuit;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

/**
 * Listener du bouton pour lancer le jeu.
 *
 * @author Benjamin Bribant
 */
public class JouerClickListener implements OnClickListener {
    private MenuActivity activity;

    /**
     * Constructeur de la classe. Récupère l'activité en cours.
     * @param activity L'activité en cours.
     */
    public JouerClickListener(MenuActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        String selectedGrid = activity.getSelectedGrid();
        if (selectedGrid != null) {
            Intent intent = new Intent(activity, GameActivity.class);
            intent.putExtra("filename", selectedGrid);
            activity.startActivity(intent);
        } else {
            Toast.makeText(activity, "Veuillez choisir une grille d'abord", Toast.LENGTH_SHORT).show();
        }
    }
}
