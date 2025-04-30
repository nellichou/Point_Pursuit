package fr.dragounell.point_pursuit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

/**
 * Composant s'occupant du rendu de la grille
 *
 * @author Maxence Raymond
 */
public class ViewGrid extends View {
    private LogicGrid grid = null;
    private boolean grayscale = false;
    private int gridSize;
    private GridTouchListener gtl;
    private final SegmentCalculator sc = new SegmentCalculator();
    private final Paint defaultPaint;
    private final Paint changingPaint;

    /**
     * Constructeur de la classe.
     *
     * @param context utilisé pour l'ascendance
     * @param attrs   utilisé pour l'ascendance
     */
    public ViewGrid(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.defaultPaint = new Paint();
        this.defaultPaint.setColor(Color.BLACK);
        this.defaultPaint.setStrokeWidth(0.5f);
        this.defaultPaint.setStyle(Paint.Style.STROKE);
        this.changingPaint = new Paint();
        this.changingPaint.setStrokeWidth(5);
    }

    /**
     * Méthode qui instancie la grille de jeu.
     *
     * @param grid La grille.
     */
    public void setGrid(LogicGrid grid) {
        this.grid = grid;
        this.gridSize = Math.min(this.getWidth(), this.getHeight()) / this.grid.getSize();
        this.gtl = new GridTouchListener(this.grid);
        setOnTouchListener(this.gtl);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (this.grid != null) {
            this.gridSize = Math.min(w, h) / this.grid.getSize();
            invalidate();
        }
    }

    /**
     * Méthode à appeler une fois que la partie est gagnée afin de désactiver le toucher et afficher un message de victoire
     */
    public void victory() {
        TextView t = ((LinearLayout) getParent()).findViewById(R.id.TextUnder);
        t.setText(R.string.victoire);
        this.setOnTouchListener(null);
    }

    /**
     * Renvoie l'accès à la taille d'une case
     *
     * @return la valeur demandée
     */
    public int getCellSize() {
        return this.gridSize;
    }

    /**
     * Méthode pour activer le mode achromate
     *
     * @param value si le mode est actif ou non
     * @return l'ancien état
     */
    public boolean setGrayScale(boolean value) {
        boolean actual = this.grayscale;
        this.grayscale = value;
        this.invalidate();
        return actual;
    }

    /**
     * Essaye de prendre autant de place que possible en tant que carré juqsu'à 3000pixels
     *
     * @param widthMeasureSpec  valeur passée par android
     * @param heightMeasureSpec valeur passée par android
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int value = Math.min(Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec)), 3000);
        setMeasuredDimension(value, value);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (this.grid == null) {
            canvas.drawText("Grid not loaded yet", 0, 0, this.defaultPaint);
            return;
        }
        this.drawGrid(canvas);
        for (Case[] lines : this.grid.getInternalGrid()) {
            for (Case sortie : lines) {
                if (sortie != null) {
                    this.changingPaint.setColor(this.grayscale ? ColorHelper.getGrayVersion(sortie.getColor()) : sortie.getColor()); //handle grayscale mode
                    this.changingPaint.setColor(ColorHelper.modifyAlpha(this.changingPaint.getColor(), 127));
                    canvas.drawRect(sortie.getRect(this.gridSize), changingPaint);
                    if (sortie.isFinal()) {
                        this.changingPaint.setColor(ColorHelper.modifyAlpha(this.changingPaint.getColor(), 255));
                        canvas.drawRoundRect(sortie.getRectF(this.gridSize), 20, 20, this.changingPaint);
                    }
                }
            }
        }
        this.drawLines(canvas);
    }

    /**
     * Méthode qui dessine les lignes.
     *
     * @param canvas le canvas à utiliser
     */
    private void drawLines(@NonNull Canvas canvas) {
        for (Line l : this.grid.getLines()) {
            sc.allocateFor(l, gridSize, grayscale);
            canvas.drawPath(sc.getPath(), sc.getPaint());
        }
        if (this.grid.getCurrentLine() != null) {
            sc.allocateFor(this.grid.getCurrentLine(), gridSize, grayscale);
            canvas.drawPath(sc.getPath(), sc.getPaint());
        }
    }

    /**
     * Méthode qui dessine la grille.
     *
     * @param canvas le canvas à utiliser
     */
    private void drawGrid(@NonNull Canvas canvas) {
        if (this.grayscale) {
            return;
        }
        int size = this.grid.getSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                canvas.drawRect(i * this.gridSize, j * this.gridSize, (i + 1) * this.gridSize, (j + 1) * this.gridSize, this.defaultPaint);
            }
        }
    }
}