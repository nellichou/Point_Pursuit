package fr.dragounell.point_pursuit;

/**
 * Classe wrapper autour de l'algorithme de vérification de victoire
 *
 * @author Maxence Raymond
 */
public class GridVictoryChecker {

    /**
     * Méthode statique de vérification
     * Vérifie que toutes les cases contiennent une ligne et que le nombre de lignes valides équivaut le nombre de paires
     *
     * @param logicGrid la grille à tester
     * @return true si la partie est victorieuse
     */
    public static boolean isFinished(LogicGrid logicGrid) {
        boolean output = logicGrid.getPointNumber() / 2 == logicGrid.getLines().size();
        return output && GridVictoryChecker.isGridComplete(logicGrid);
    }

    /**
     * Méthode permettant de vérifier que des grilles occupent toutes les lignes
     *
     * @param logicGrid la grille à vérifier
     * @return si toutes les cases sont occupées
     */
    private static boolean isGridComplete(LogicGrid logicGrid) {
        for (Case[] cases : logicGrid.getInternalGrid()) {
            for (Case toCheck : cases) {
                if (toCheck == null) {
                    return false;
                }
            }
        }
        return true;
    }
}
