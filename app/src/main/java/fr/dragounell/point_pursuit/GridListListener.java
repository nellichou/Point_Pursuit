package fr.dragounell.point_pursuit;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import java.util.List;

/**
 * Listener de la liste des grilles.
 *
 * @author Benjamin Bribant
 */
public class GridListListener implements OnItemClickListener {
    private final Activity activity;
    private final List<String> nomsFichiersGrilles;

    /**
     * Constructeur de la classe. Récupère l'activité en cours la liste des grilles.
     * @param activity L'activité en cours.
     * @param nomsFichiersGrilles La liste des noms des fichiers xml contenant les grilles.
     */
    public GridListListener(Activity activity, List<String> nomsFichiersGrilles){
        this.activity = activity;
        this.nomsFichiersGrilles = nomsFichiersGrilles;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        String selectedName = this.nomsFichiersGrilles.get(position);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("selectedGrid", selectedName);
        this.activity.setResult(Activity.RESULT_OK, resultIntent);
        this.activity.finish();
    }
}
