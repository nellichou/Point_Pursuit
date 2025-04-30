package fr.dragounell.point_pursuit;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Listener du bouton pour ouvrir l'activité de la liste des grilles.
 *
 * @author Benjamin Bribant
 */
public class OuvrirListeListener implements OnClickListener {
    private Activity activity;

    /**
     * Constructeur de la classe, récupère l'activité en cours.
     * @param activity L'activité en cours.
     */
    public OuvrirListeListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(activity, GridListActivity.class);
        activity.startActivityForResult(intent, 1);
    }
}
