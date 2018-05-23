package fr.univtln.group3.anamorphosisandroid;

import android.graphics.Bitmap;
import android.util.Log;

public class PixelsExtractor {
    Direction direction;


    int nombreRangeesDePixelsCopiees;
    int largeur;
    int hauteur;
    int nbBitmap;

    int tailleObturateur;
    float resteObturateur;
    float compteurResteObturateur;
    int bitmapTraitees;

//    Bitmap bitmapResult;

    private static final String TAG = "ExtractMpegFrames";
    private static final boolean VERBOSE = false;

    public PixelsExtractor(Direction direction, int largeur, int hauteur, int nbBitmap){
        nombreRangeesDePixelsCopiees = 0;
        this.direction = direction;
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.nbBitmap = nbBitmap;
//        this.bitmapResult = bitmapResult;

        System.out.println("largeur: " + largeur);
        System.out.println("hauteur: " + hauteur);
        System.out.println("nbBitmap: "+ nbBitmap);

        switch (direction){
            case HAUT_BAS:
                tailleObturateur = hauteur / nbBitmap;
                resteObturateur = (float) nbBitmap / ((float) hauteur % (float) nbBitmap);
                break;
            case GAUCHE_DROITE:
                tailleObturateur = largeur / nbBitmap;
                resteObturateur = (float) nbBitmap / ((float) largeur % (float) nbBitmap);
                break;
            case BAS_HAUT:
                tailleObturateur = hauteur / nbBitmap;
                resteObturateur = (float) nbBitmap / ((float) hauteur % (float) nbBitmap);
                break;
            case DROITE_GAUCHE:
                tailleObturateur = largeur / nbBitmap;
                resteObturateur = (float) nbBitmap / ((float) largeur % (float) nbBitmap);
                break;
        }
        compteurResteObturateur = 0;
        bitmapTraitees = 0;

        System.out.println("taille: " + tailleObturateur);
        System.out.println("reste: " + resteObturateur);

    }


    public enum Direction{
        HAUT_BAS,
        GAUCHE_DROITE,
        BAS_HAUT,
        DROITE_GAUCHE
    }

    public void extractAndCopy(Bitmap bitmapResult, Bitmap bitmapCurrent){
        int oneMore = 0;
        if (bitmapTraitees > compteurResteObturateur){
            oneMore = 1;
            compteurResteObturateur += resteObturateur;
        }
        switch (direction){
            case HAUT_BAS:
                hautBas(bitmapResult, bitmapCurrent, oneMore);
                break;
        }

        bitmapTraitees ++;
    }

    public void hautBas(Bitmap bitmapResult, Bitmap bitmapCurrent, int oneMore) {
        if (bitmapTraitees < nbBitmap) {
            if (VERBOSE) Log.d(TAG, "debut haut bas");
            for (int i = nombreRangeesDePixelsCopiees; i < nombreRangeesDePixelsCopiees + tailleObturateur + oneMore; i++) {
                if (i<hauteur) {
                    for (int j = 0; j < largeur; j++) { // pour chaque element dans la ligne
                        bitmapResult.setPixel(j, i, bitmapCurrent.getPixel(j, i));
                    }
                }
            }
            nombreRangeesDePixelsCopiees += tailleObturateur + oneMore;
            System.out.println("nombreRangeesDePixelsCopiees: " + nombreRangeesDePixelsCopiees);
            if (VERBOSE) Log.d(TAG, "fin haut bas");
        } else {
            Log.d(TAG, "Erreur, toutes les Bitmaps on été traitées");
        }
    }

}
