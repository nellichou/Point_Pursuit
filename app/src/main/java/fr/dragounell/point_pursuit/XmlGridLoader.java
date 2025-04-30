package fr.dragounell.point_pursuit;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Classe servant à vérifier, traiter et obtenir les grilles à partir des fichiers xml contenus dans le dossier assets/puzzles
 *
 * @author Maxence Raymond, Nell Telechea
 */
public class XmlGridLoader {
    private static final String ROOT_XML = "puzzle";
    private static final String DUO = "paire";
    private static final String POINT = "point";
    private final Map<String, String> filenamesToNames = new LinkedHashMap<>();
    private final Context context;


    /**
     * Constructeur du chargeur de grilles
     *
     * @param context un contexte pouvant charger colors.xml et le contenu du dossier assets
     */
    public XmlGridLoader(Context context) {
        this.context = context;
        AssetManager manager = context.getAssets();
        try {
            String[] list = manager.list("puzzles");
            if (list == null) {
                Toast.makeText(context, "No puzzles found", Toast.LENGTH_LONG).show();
                Log.d("ERROR", "No puzzles found");
            } else {
                this.populateMap(list, manager);
            }
        } catch (IOException e) {
            Toast.makeText(this.context, "No access to assets folder", Toast.LENGTH_LONG).show();
            Log.e("FATAL_ERROR", "IOException in GridLoader");
        }
    }

    private void populateMap(String[] list, AssetManager manager) {
        for (String fileName : list) {
            try {
                XmlPullParser parser = XmlGridLoader.openParser("puzzles/" + fileName, manager);
                this.filenamesToNames.put(fileName, this.retrieveFilename(fileName, parser));
            } catch (XmlPullParserException | IOException e) {
                Log.d("ParserError", "Error opening parser for" + fileName);
                this.filenamesToNames.put(fileName, fileName.substring(0, fileName.lastIndexOf(".")));
                Toast.makeText(this.context, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String retrieveFilename(String fileName, XmlPullParser parser) {
        String gridName = null;
        try {
            int event = parser.getEventType();
            while (event != XmlPullParser.END_TAG) {
                if (parser.getName() != null && parser.getName().equals(XmlGridLoader.ROOT_XML)) {
                    gridName = parser.getAttributeValue(null, "nom");
                    break;
                }
                event = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            Log.d("ParserError", "Error retrieving puzzle name of " + fileName);
        }
        return gridName != null ? gridName : fileName.substring(0, fileName.lastIndexOf(".")); //renvoie le nom du fichier sans l'extension si le nom n'est pas disponible et le nom sinon
    }

    private XmlPullParser openParser(String filename) throws IOException, XmlPullParserException {
        return XmlGridLoader.openParser("puzzles/" + filename, this.context.getAssets());
    }

    private static XmlPullParser openParser(String filename, AssetManager manager) throws IOException, XmlPullParserException {
        InputStream inputStream = manager.open(filename);
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(inputStream, "UTF-8");
        return parser;
    }

    /**
     * Une map contenant en clé les noms de fichier des grilles et en valeur les noms des grilles
     *
     * @return les noms et les noms de fichier
     */
    public Map<String, String> getNames() {
        return this.filenamesToNames;
    }

    /**
     * Retourne une instance de LogicGrid
     * La validité de la grille devrait être vérifié au préalable avec XmlGridLoader.isValid(String filename)
     *
     * @param filename le nom du fichier contenant une grille valide
     * @return null si une erreur est rencontrée
     */
    public LogicGrid getGrid(String filename) {
        LogicGrid inWorkGrid = null;
        try {
            XmlPullParser parser = this.openParser(filename);
            int event = parser.getEventType();

            if (event == XmlPullParser.START_DOCUMENT) {
                event = parser.next();
            }

            while (event != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();
                if (tagName != null) {
                    if (tagName.equals(ROOT_XML) && event == XmlPullParser.START_TAG) {
                        String sizeStr = parser.getAttributeValue(null, "size");
                        String nom = parser.getAttributeValue(null, "nom");

                        if (sizeStr == null) {
                            Log.e("XmlGridLoader", "size is missing in the file");
                            return null;
                        }

                        int size = Integer.parseInt(sizeStr);
                        inWorkGrid = new LogicGrid(size, nom, this.context);
                    } else if (tagName.equals(XmlGridLoader.DUO) && event == XmlPullParser.START_TAG) {
                        assert inWorkGrid != null;
                        XmlGridLoader.handlePair(parser, inWorkGrid);
                    }
                }
                event = parser.next();
            }
        } catch (XmlPullParserException | IOException | NumberFormatException e) {
            Log.e("ParserError", "Error encountered while retrieving " + filename, e);
            return null;
        }
        return inWorkGrid;
    }

    private static void handlePair(XmlPullParser parser, LogicGrid inWorkGrid) throws IOException, XmlPullParserException {
        parser.next(); // get out of the pair tag
        while (parser.getName() == null || parser.getEventType() != XmlPullParser.START_TAG || !parser.getName().equals(XmlGridLoader.POINT)) {
            parser.next();
        }
        int x1 = Integer.parseInt(parser.getAttributeValue(null, "colonne"));
        int y1 = Integer.parseInt(parser.getAttributeValue(null, "ligne"));
        parser.next();
        parser.next();
        while (parser.getName() == null || parser.getEventType() != XmlPullParser.START_TAG || !parser.getName().equals(XmlGridLoader.POINT)) {
            parser.next();
        }
        int x2 = Integer.parseInt(parser.getAttributeValue(null, "colonne"));
        int y2 = Integer.parseInt(parser.getAttributeValue(null, "ligne"));
        inWorkGrid.setNewPair(x1, y1, x2, y2);
    }

    /**
     * Vérifie si la grille est valide
     *
     * @param filename le nom du fichier contenant la grille à tester
     * @return false si une erreur est rencontrée ou la grille est invalide, true sinon
     */
    public boolean isValid(String filename) {
        /* Checks to do :
        - 2 paires
        - paires contenant 2 points
        - points valides
        - taille
        - paires fermantes ?
        - présence tag puzzle avant paire
        - présence end tag puzzle
         */
        int size = -1;
        boolean haspair = false, checkpair = false, hasStarted = false;
        try {
            XmlPullParser parser = this.openParser(filename);
            String name = parser.getName();
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                if (name != null) {
                    if (name.equals(ROOT_XML) && event == XmlPullParser.START_TAG) { //vérification du tag puzzle
                        size = Integer.parseInt(parser.getAttributeValue(null, "size"));
                        if (size > 14 || size < 5) {
                            return false; // taille invalide
                        }
                        if (hasStarted) {
                            return false; //vérification de l'absence de doublon du tag puzzle
                        }
                        hasStarted = true;
                    } else if (name.equals(XmlGridLoader.DUO) && event == XmlPullParser.START_TAG) {
                        if (!hasStarted) { //pas de tag puzzle
                            return false;
                        }
                        haspair = true; //vérification de la présence d'une paire au moins
                        checkpair = !checkpair; //vérification de l'intégrité de la paire
                        if (XmlGridLoader.pairVerifier(parser, parser.getEventType(), size)) { //vérification de l'intégrité du point
                            return false;
                        }
                    } else if (name.equals(XmlGridLoader.DUO) && event == XmlPullParser.END_TAG) {
                        if (!haspair || !checkpair) {
                            return false;
                        }
                        checkpair = false;
                    }
                }
                parser.next();
                event = parser.getEventType();
                name = parser.getName();
            }
            return haspair;
        } catch (IOException e) {
            Toast.makeText(this.context, "Error while opening " + filename, Toast.LENGTH_SHORT).show();
            Log.d("FATAL_ERROR", "IOException in GridLoader while opening " + filename);
        } catch (XmlPullParserException | NumberFormatException ignored) {
        }
        return false;
    }

    /**
     * Renvoie true si la paire n'est pas valide
     *
     * @throws IOException            générée par le parser et doit remonter jusqu'à isvalid
     * @throws XmlPullParserException générée par le parser et doit remonter jusqu'à isvalid
     */
    private static boolean pairVerifier(XmlPullParser parser, int event, int size) throws IOException, XmlPullParserException {
        int pointCount = 0;
        String output = parser.getName();
        while (event != XmlPullParser.END_TAG || !(output != null && output.equals(XmlGridLoader.DUO))) {
            if (output != null) {
                if (output.equals(XmlGridLoader.POINT) && event == XmlPullParser.START_TAG) {
                    boolean returnvalue = XmlGridLoader.pointVerifier(parser, size);
                    pointCount++;
                    if (returnvalue) {
                        return true;
                    }
                }
            }
            parser.next();
            event = parser.getEventType();
            output = parser.getName();
        }
        return pointCount != 2;
    }

    /**
     * Renvoie true si le point n'est pas valide
     *
     * @throws IOException            générée par le parser et doit remonter jusqu'à isvalid
     * @throws XmlPullParserException générée par le parser et doit remonter jusqu'à isvalid
     */
    private static boolean pointVerifier(XmlPullParser parser, int size) throws IOException, XmlPullParserException {
        while (parser.getName() == null || !parser.getName().equals(XmlGridLoader.POINT)) {
            if (parser.getEventType() == XmlPullParser.END_DOCUMENT) {
                return true;
            }
            parser.next();
        }
        int column = Integer.parseInt(parser.getAttributeValue(null, "colonne"));
        int line = Integer.parseInt(parser.getAttributeValue(null, "ligne"));
        return line >= size || line < 0 || column < 0 || column >= size;
    }
}