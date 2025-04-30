package fr.dragounell.point_pursuit;

import android.graphics.Rect;
import android.graphics.RectF;

import java.io.Serializable;

/**
 * Représentation logique d'une case
 *
 * @author Maxence Raymond
 */
public class Case implements Serializable {
    private final int x;
    private final int y;
    private final int color;
    private boolean isFinal = false;

    /**
     * Constructeur de base
     *
     * @param x     coordonnée x
     * @param y     coordonnée y
     * @param color la couleur à utiliser
     */
    public Case(int x, int y, int color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    /**
     * Constructeur avec possibilité de définir l'état final
     *
     * @param x       coordonnée x
     * @param y       coordonnée y
     * @param color   la couleur à utiliser
     * @param isFinal etat final
     */
    public Case(int x, int y, int color, boolean isFinal) {
        this(x, y, color);
        this.isFinal = isFinal;
    }

    /**
     * Renvoie la couleur de la case
     * @return la couleur sous format android
     */
    public int getColor() {
        return this.color;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    /**
     * Méthode pour les composants graphiques
     *
     * @param gridSize la taille qu'occupe à l'écran la case
     * @return Un rectangle occupé par la case en position absolue par rapport au composant
     */
    public Rect getRect(int gridSize) {
        return new Rect(gridSize * this.x, gridSize * this.y, gridSize * (this.x + 1), gridSize * (this.y + 1));
    }


    /**
     * Méthode pour les composants graphiques occupant les 3/4 de la taille avec des bords arrondis
     *
     * @param gridSize la taille qu'occupe à l'écran la case
     * @return Un rectangle arrondi en position absolue par rapport au composant
     */
    public RectF getRectF(int gridSize) {
        float part = gridSize * 0.2f;
        return new RectF(gridSize * this.x + 2 + part, gridSize * this.y + part, gridSize * (this.x + 1) - part, gridSize * (this.y + 1) - part);
    }
}
