package fr.dragounell.point_pursuit;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Classe qui gère toute la logique de la grille de jeu.
 *
 * @author Maxence Raymond
 */
public class LogicGrid {
    private final String name;
    private final int size;
    private Case[][] internalGrid; //utilisée pour des raisons de performance sur téléphone ancien
    private final ColorHelper colorHelper;
    private Line inConstructionline;
    private int pointNumber = 0;
    private final List<Line> appliedLines = new ArrayList<>();

    /**
     * Constructeur de la grille
     * XmlGridLoader devrait être utilisé plutôt
     *
     * @param size taille de la grille
     * @param name nom de la grille
     */
    public LogicGrid(int size, String name, Context context) {
        this.size = size;
        this.name = name;
        this.internalGrid = new Case[size][size];
        this.colorHelper = new ColorHelper(context);
    }

    /**
     * Méthode utilisée lors de la création de la grille.
     *
     * @param x1 Numéro de la colonne du premier point de la paire.
     * @param y1 Numéro de la ligne du premier point de la paire.
     * @param x2 Numéro de la colonne du deuxième point de la paire.
     * @param y2 Numéro de la ligne du deuxième point de la paire.
     */
    public void setNewPair(int x1, int y1, int x2, int y2) {
        if (x1 < 0 || y1 < 0 || x2 < 0 || y2 < 0 || x1 > size || y1 > size || x2 > size || y2 > size) {
            throw new IllegalArgumentException("Invalid positions");
        }
        int color = this.colorHelper.next();
        this.internalGrid[x1][y1] = new Case(x1, y1, color, true);
        this.internalGrid[x2][y2] = new Case(x2, y2, color, true);
        this.pointNumber += 2;
    }

    /**
     * Getter du nombre de points
     *
     * @return le nombre de points faisant partie d'une paire
     */
    public int getPointNumber() {
        return pointNumber;
    }

    /**
     * Méthode exposant le stockage interne des cases de la grille
     *
     * @return Un tableau à double entrée
     */
    public Case[][] getInternalGrid() { //there is probably a better way to do this
        return this.internalGrid;
    }

    /**
     * Renvoie la taille de la grille.
     *
     * @return Taille de la grille.
     */
    public int getSize() {
        return this.size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LogicGrid)) return false;
        LogicGrid logicGrid = (LogicGrid) o;
        return size == logicGrid.size && name.equals(logicGrid.name) && Objects.deepEquals(internalGrid, logicGrid.internalGrid);
    }

    /**
     * Vérfie l'état de victoire de la grille
     *
     * @return l'état de victoire
     */
    public boolean isVictory() {
        return GridVictoryChecker.isFinished(this);
    }

    /**
     * Commence l'ajout d'une ligne
     *
     * @param x La coordonnée x du point de départ
     * @param y La coordonnée y du point de départ
     * @return true si cela s'est bien passé, false si l'opération est invalide
     */
    public boolean startLine(int x, int y) {
        this.cancelLine();
        if (this.internalGrid[x][y] == null) {
            Log.d("LineCheck", "Line start failed");
            return false;
        }
        this.inConstructionline = new Line(this.internalGrid[x][y]);
        Log.d("LineCheck", "Line started");
        return true;
    }

    /**
     * Ajout un Point à la ligne en cours de construction, la ligne doit être commencée par startline
     *
     * @param x la coordonnée x du point
     * @param y la coordonnée y du point
     * @return si l'ajout s'est bien déroulé, false sinon
     */
    public boolean addPointToLine(int x, int y) {
        if (this.inConstructionline == null) {
            return false;
        }
        if (this.internalGrid[x][y] != null) {
            if (this.inConstructionline.getFirstCase().getColor() == this.internalGrid[x][y].getColor() && this.internalGrid[x][y].isFinal()) {
                this.inConstructionline.addPoint(x, y);
                this.applyLine();
                Log.d("LineCheck", "Line completed");
                return true;
            }
            this.cancelLine();
            Log.d("LineCheck", "Line aborted when adding a point");
            return false;
        }
        this.inConstructionline.addPoint(x, y);
        Log.d("LineCheck", "Point added");
        return true;
    }

    /**
     * Réinitialise la ligne en cours d'ajout
     */
    public void cancelLine() {
        Log.d("LineCheck", "Line cancelled");
        this.inConstructionline = null;
    }

    /**
     * Finalise la ligne en cours de construction et l'applique à la grille
     */
    private void applyLine() {
        this.appliedLines.add(this.inConstructionline);
        for (Point p : this.inConstructionline.getAllPoints()) {
            if (this.internalGrid[p.x][p.y] == null) {
                this.internalGrid[p.x][p.y] = new Case(p.x, p.y, this.inConstructionline.getColor());
            }
        }
        this.cancelLine();
    }

    /**
     * Renvoie le nom de la grille
     *
     * @return le nom sous forme de string
     */
    public String getName() {
        return this.name;
    }

    public boolean removeLine(int x, int y) {
        if (!this.internalGrid[x][y].isFinal() || this.appliedLines.isEmpty()) {
            return false;
        }
        for (Line l : this.appliedLines) {
            if (l.getColor() == this.internalGrid[x][y].getColor()) {
                for (Point p : l.getAllPoints()) {
                    if (!this.internalGrid[p.x][p.y].isFinal()) {
                        this.internalGrid[p.x][p.y] = null;
                    }
                }
                this.appliedLines.remove(l);
                return true;
            }
        }
        return false;
    }

    /**
     * Permet de vérifier si une case est vide.
     *
     * @param x Numéro de la colonne de la case.
     * @param y Numéro de la ligne de la case.
     * @return True si elle est vide, false sinon.
     */
    public boolean isCaseEmpty(int x, int y) {
        return this.internalGrid[x][y] == null;
    }

    /**
     * Renvoie la liste des lignes créées.
     *
     * @return La liste des lignes.
     */
    public List<Line> getLines() {
        return this.appliedLines;
    }

    /**
     * Renvoie la ligne en construction.
     *
     * @return La ligne.
     */
    public Line getCurrentLine() {
        return this.inConstructionline;
    }

    /**
     * Package private method for bundle
     */
    void restoreLines(List<Line> lines) {
        this.appliedLines.clear();
        this.appliedLines.addAll(lines);
        for (Line l : this.appliedLines) {
            this.applyRestoredLine(l);
        }
    }

    private void applyRestoredLine(Line l) {
        List<Point> lp = l.getAllPoints();
        int color = l.getColor();
        boolean endpoint;
        for (int i = 0; i < lp.size(); i++) {
            Point p = lp.get(i);
            endpoint = i == 0 || i == (lp.size() - 1);
            if (endpoint) {
                this.internalGrid[p.x][p.y] = new Case(p.x, p.y, color, true);
            } else {
                this.internalGrid[p.x][p.y] = new Case(p.x, p.y, color);
            }
        }
    }

    /**
     * Package private method for bundle
     */
    Serializable getSerializableLines() {
        return (Serializable) this.appliedLines;
    }
}
