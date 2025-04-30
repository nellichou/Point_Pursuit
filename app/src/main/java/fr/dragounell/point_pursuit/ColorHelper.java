package fr.dragounell.point_pursuit;

import android.content.Context;

/**
 * Classe servant à aider à la gestion des couleurs en fournissant une interface simple pour obtenir des couleurs
 * Comporte également des méthodes statiques relatives aux couleurs
 *
 * @author Maxence Raymond
 */
public class ColorHelper {

    private int index;
    private final int size;
    private final int[] values;

    /**
     * Constructeur de la classe
     *
     * @param context utilisée pour récupérer les couleurs de colors.xml
     */
    public ColorHelper(Context context) {
        this.index = 0;
        this.values = context.getResources().getIntArray(R.array.colors_array);
        this.size = this.values.length;
    }

    /**
     * Retourne la prochaine couleur
     * se réinitialiser à la première couleur à la fin
     *
     * @return la couleur en hexadécimal
     */
    public int next() {
        int color = this.values[index];
        this.index = (++this.index) % this.size; //important que le ++ soit avant pour que ca soit pris en compte
        return color;
    }

    /**
     * Retourne une version grisée d'une couleur en utilisant les formules de luminance
     *
     * @param value la couleur à transformer en gris
     * @return la couleur grisée
     */
    public static int getGrayVersion(int value) {
        int r = (((value >> 16) & 0xFF));
        int g = (((value >> 8) & 0xFF));
        int b = (value & 0xFF);
        int output = (r+g+b)/3;
        return 0xFF000000 | (output) | ((output) << 8) | ((output) << 16);
    }

    /**
     * Modifie la transparence de la couleur
     *
     * @param color    la couleur à modifier
     * @param newAlpha entre 0 et 255
     * @return la couleur avec la valeur alpha modifiée
     */
    public static int modifyAlpha(int color, int newAlpha) {
        return (color & 0xFFFFFF) | ((newAlpha & 0xFF) << 24);
    }
}
