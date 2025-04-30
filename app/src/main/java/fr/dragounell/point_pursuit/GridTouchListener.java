package fr.dragounell.point_pursuit;

import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * Listener du jeu.
 *
 * @author Nell Telechea, Benjamin Bribant
 */
public class GridTouchListener implements OnTouchListener {
    private final LogicGrid grid;
    private boolean isDrawing = false;
    private int lastX = -1, lastY = -1;

    /**
     * Constructeur de la classe, récupère la grille.
     * @param grid La grille de jeu.
     */
    public GridTouchListener(LogicGrid grid) {
        this.grid = grid;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!(v instanceof ViewGrid)) return false;

        int cellSize = ((ViewGrid) v).getCellSize();
        int x = (int) (event.getX() / cellSize);
        int y = (int) (event.getY() / cellSize);

        if (x < 0 || y < 0 || x >= grid.getSize() || y >= grid.getSize()) {
            grid.cancelLine();
            v.invalidate();
            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (grid.isCaseEmpty(x, y)) {
                    return true;
                }

                Case currentCase = grid.getInternalGrid()[x][y];

                // Vérifier si c'est une extrémité
                if (currentCase.isFinal()) {
                    Line existingLine = null;

                    for (Line line : grid.getLines()) {
                        if (line.getColor() == currentCase.getColor()) {
                            existingLine = line;
                            break;
                        }
                    }

                    if (existingLine != null) {
                        boolean removed = grid.removeLine(x, y);
                        if (removed) {
                            v.invalidate();
                        }
                        return true;
                    } else {
                        if (grid.startLine(x, y)) {
                            isDrawing = true;
                            lastX = x;
                            lastY = y;
                            return true;
                        }
                    }
                }

                for (Line line : grid.getLines()) {
                    for (Point p : line.getAllPoints()) {
                        if (p.x == x && p.y == y) {
                            return true;
                        }
                    }
                }

                return true;

            case MotionEvent.ACTION_MOVE:
                if (isDrawing) {
                    if (x != lastX || y != lastY) {
                        Line currentLine = grid.getCurrentLine();
                        if (currentLine != null && currentLine.getAllPoints().contains(new Point(x, y))) {
                            grid.cancelLine();
                            isDrawing = false;
                            v.invalidate();
                            return true;
                        }

                        if (Math.abs(x - lastX) + Math.abs(y - lastY) > 1) {
                            grid.cancelLine();
                            isDrawing = false;
                            v.invalidate();
                            return true;
                        }

                        grid.addPointToLine(x, y);
                        lastX = x;
                        lastY = y;
                        v.invalidate();
                    }
                }
                return true;

            case MotionEvent.ACTION_UP:
                if (grid.isVictory()) {
                    ((ViewGrid) v).victory();
                }
                Case currentCaseEnd = grid.getInternalGrid()[x][y];
                if (currentCaseEnd != null) {
                    if (currentCaseEnd.isFinal()) {
                        isDrawing = false;
                        v.invalidate();
                        return true;
                    } else {
                        isDrawing = false;
                        grid.cancelLine();
                        v.invalidate();
                    }
                } else {
                    isDrawing = false;
                    grid.cancelLine();
                    v.invalidate();
                }
        }

        return false;
    }
}