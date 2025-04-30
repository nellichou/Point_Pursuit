package fr.dragounell.point_pursuit;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Classe adaptant la liste selon la disponibilité des puzzles
 *
 * @author Benjamin Bribant, Maxence Raymond
 */
public class GridValidityAdapter extends ArrayAdapter<String> {
    private final XmlGridLoader loader;
    private final List<String> nomsFichiers;

    /**
     * Constructeur de l'adapteur
     *
     * @param context a besoin d'accéder aux ressources et aux assets
     */
    public GridValidityAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
        this.loader = new XmlGridLoader(context);
        Map<String, String> grilles = loader.getNames();
        this.nomsFichiers = new ArrayList<>(grilles.keySet());
        this.addAll(new ArrayList<>(grilles.values()));
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView textView = view.findViewById(android.R.id.text1);
        textView.setText(getItem(position));

        if (!loader.isValid(nomsFichiers.get(position))) {
            textView.setTextColor(getContext().getResources().getColor(android.R.color.darker_gray));
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            view.setOnClickListener(null);
        } else {
            textView.setTextColor(getContext().getResources().getColor(android.R.color.black));
            textView.setPaintFlags(textView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }

        return view;
    }

    @Override
    public boolean isEnabled(int position) {
        return loader.isValid(nomsFichiers.get(position));
    }
}
