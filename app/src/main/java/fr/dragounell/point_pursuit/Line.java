package fr.dragounell.point_pursuit;

import android.graphics.Point;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant une ligne
 *
 * @author Maxence Raymond, Nell Telechea
 */
public class Line implements Serializable {

    private final int color;
    private final Case firstCase;
    private Point initial;
    private Point lastPoint;
    private final List<Point> intermediates = new ArrayList<>();

    /**
     * Constructeur de ligne
     *
     * @param caseStart Case finale de départ
     */
    public Line(Case caseStart) {
        this.initial = new Point(caseStart.getX(), caseStart.getY());
        this.color = caseStart.getColor();
        this.firstCase = caseStart;
        this.lastPoint = this.initial;
        this.intermediates.add(this.initial);
    }

    /**
     * Renvoie la couleur de la ligne
     *
     * @return la valeur en int
     */
    public int getColor() {
        return this.color;
    }

    /**
     * Ajoute un point à la ligne
     *
     * @param x cordonnée adjacente au dernier point
     * @param y cordonnée adjacente au dernier point
     * @return si l'ajout s'est bien passé
     */
    public boolean addPoint(int x, int y) {
        if (!isAdjacent(lastPoint.x, lastPoint.y, x, y)) { //verifie que l'adjacence du point n'est pas en diagonale ou autre
            return false;
        }
        Point p = new Point(x, y);
        this.lastPoint = p;
        this.intermediates.add(p);
        return true;
    }

    /**
     * Vérifie si deux points sont adjacents.
     *
     * @param x1 Numéro de la colonne du premier point.
     * @param y1 Numéro de la ligne du premier point.
     * @param x2 Numéro de la colonne du deuxième point.
     * @param y2 Numéro de la ligne du deuxième point.
     * @return true si les points sont adjacents, false sinon.
     */
    private boolean isAdjacent(int x1, int y1, int x2, int y2) {
        return (Math.abs(x1 - x2) == 1 && y1 == y2) || (Math.abs(y1 - y2) == 1 && x1 == x2);
    }

    /**
     * Retire un point de la ligne
     * Retire de manière impartielle un point de la ligne peu importe sa position ou s'il existe
     *
     * @param x cordonnée x du point à retirer
     * @param y coordonnée y du point à retirer
     * @deprecated
     */
    public void removePoint(int x, int y) {
        if (this.initial.equals(new Point(x, y))) {
            this.initial = null;
        } else {
            this.intermediates.remove(new Point(x, y));
        }
    }

    /**
     * Retourne tout les points
     * @return une liste de points
     */
    public List<Point> getAllPoints() {
        return this.intermediates;
    }

    /**
     * Renvoie la case de départ
     * @return une instance de Case finale
     */
    public Case getFirstCase() {
        return this.firstCase;
    }
}
