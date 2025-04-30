package fr.dragounell.point_pursuit;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import java.util.List;

/**
 * Classe permettant de dessiner les lignes sur la grille de jeu.
 *
 * @author Maxence Raymond
 */
public class SegmentCalculator {
    private Line line;
    private int gridsize = -1; //la taille d'une case est forcément supérieure ou égale à 0
    private final Paint paint;
    private final Path path;

    /**
     * Constructeur de la classe
     * Il faut appeler allocateFor avant toute utilisation, la classe étant concue pour être réutilisable.
     */
    public SegmentCalculator() {
        this.path = new Path();
        this.paint = new Paint();
        this.paint.setStyle(Paint.Style.STROKE);
    }

    private void getInitialPaint(int color, int gridsize) {
        if (gridsize != this.gridsize) {
            //optimization needed for later when using patterns
            this.paint.setStrokeWidth(gridsize / 4f);
        }

        this.paint.setColor(color);
        //TODO: prettify it with a linear gradient or maybe pathdashpatheffect for alternating between normal color and a lighter variant

    }

    private void createValues() {
        List<Point> lp = this.line.getAllPoints();
        this.path.reset();
        this.path.incReserve(lp.size());
        int half = this.gridsize >> 1;

        this.path.moveTo(lp.get(0).x * this.gridsize + half, lp.get(0).y * this.gridsize + half);
        for (int i = 1; i < lp.size(); i++) {
            // Nouveau stop à la ligne sur le centre de la case
            this.path.lineTo(lp.get(i).x * this.gridsize + half, lp.get(i).y * this.gridsize + half);
        }
    }

    /**
     * Renvoie un composant Path suivant les points de la ligne
     *
     * @return le composant à passer à canvas
     */
    public Path getPath() {
        if (this.line == null) {
            throw new IllegalStateException("initialization needed");
        }
        return this.path;
    }

    /**
     * Renvoie un composant Paint stylisé de la couleur de la ligne (si complètement fini)
     *
     * @return le composant Paint à utiliser pour dessiner
     */
    public Paint getPaint() {
        if (this.line == null) {
            throw new IllegalStateException("initialization needed");
        }
        return this.paint;
    }

    /**
     * Méthode de préparation de la classe
     *
     * @param l    la ligne à dessiner
     * @param size la taille d'une case
     */
    public void allocateFor(Line l, int size, boolean grayscale) {
        this.line = l;
        int color = l.getColor();
        this.getInitialPaint(grayscale ? ColorHelper.getGrayVersion(color) : color, size); //cree le composant Paint pour le dessin de la ligne suivant si le mode achromate est actif ou non
        this.gridsize = size;
        this.createValues();
    }
}