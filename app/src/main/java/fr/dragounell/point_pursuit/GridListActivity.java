package fr.dragounell.point_pursuit;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Activité de la liste des grilles.
 *
 * @author Benjamin Bribant
 */
public class GridListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        getActionBar().hide();

        ListView listeGrilles = findViewById(R.id.grilles);

        XmlGridLoader loader = new XmlGridLoader(this);
        Map<String, String> grilles = loader.getNames();
        List<String> nomsFichiers = new ArrayList<>(grilles.keySet());

        listeGrilles.setAdapter(new GridValidityAdapter(this));

        listeGrilles.setOnItemClickListener(new GridListListener(this, nomsFichiers));
    }

}
