package fr.univtln.group3.anamorphosisandroid;

import android.graphics.Bitmap;
import android.util.Log;

import fr.univtln.group3.anamorphosisandroid.Utility.Utils;

/**
 * Effectue une anamorphosme simple, ie :
 *  - gauche vers droite
 *  - dorite vers gauche
 *  - haut vers bas
 *  - bas vers haut
 */
public class AlgoClassic {
    Utils.Direction direction;


    int numRangeeDePixels;
    int largeur;
    int hauteur;
    int nbBitmap;

    int tailleObturateur;
    float resteObturateur;
    float compteurResteObturateur;
    int bitmapTraitees;

    Bitmap bitmapResult;


    private static final String TAG = "ExtractMpegFrames";
    private static final boolean VERBOSE = false;

    /**
     * Initialise les variables
     * @param bitmapResult
     * @param direction
     * @param largeur
     * @param hauteur
     * @param nbBitmap
     */
    public AlgoClassic(Bitmap bitmapResult, Utils.Direction direction, int largeur, int hauteur, int nbBitmap){
        this.direction = direction;
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.nbBitmap = nbBitmap;
        this.bitmapResult = bitmapResult;


        switch (direction){
            case DOWN:
                numRangeeDePixels = 0;
                tailleObturateur = hauteur / nbBitmap;
                resteObturateur = (float) nbBitmap / ((float) hauteur % (float) nbBitmap);
                break;
            case RIGHT:
                numRangeeDePixels = 0;
                tailleObturateur = largeur / nbBitmap;
                resteObturateur = (float) nbBitmap / ((float) largeur % (float) nbBitmap);
                break;
            case UP:
                numRangeeDePixels = hauteur - 1;
                tailleObturateur = hauteur / nbBitmap;
                resteObturateur = (float) nbBitmap / ((float) hauteur % (float) nbBitmap);
                break;
            case LEFT:
                numRangeeDePixels = largeur - 1;
                tailleObturateur = largeur / nbBitmap;
                resteObturateur = (float) nbBitmap / ((float) largeur % (float) nbBitmap);
                break;
        }
        compteurResteObturateur = 0;
        bitmapTraitees = 0;


    }

    /**
     * Extraie une partie de la bitmap courante et la copie sur la bitmap finale
     * @param bitmapCurrent
     */
    public void extractAndCopy(Bitmap bitmapCurrent){
        int oneMore = 0;
        if (bitmapTraitees > compteurResteObturateur){
            oneMore = 1;
            compteurResteObturateur += resteObturateur;
        }
        switch (direction){
            case DOWN:
                hautBas(bitmapCurrent, oneMore);
                break;
            case RIGHT:
                gaucheDroite(bitmapCurrent, oneMore);
                break;
            case UP:
                basHaut(bitmapCurrent, oneMore);
                break;
            case LEFT:
                droiteGauche(bitmapCurrent, oneMore);
                break;
        }


        bitmapTraitees ++;
    }

    /**
     * Algorithme haut vers bas
     * @param bitmapCurrent
     * @param oneMore
     */
    public void hautBas(Bitmap bitmapCurrent, int oneMore) {
        if (bitmapTraitees < nbBitmap) {
            if (VERBOSE) Log.d(TAG, "debut haut bas");
            for (int i = numRangeeDePixels; i < numRangeeDePixels + tailleObturateur + oneMore; i++) {
                if (i<hauteur) {
                    for (int j = 0; j < largeur; j++) { // pour chaque element dans la ligne
                        bitmapResult.setPixel(j, i, bitmapCurrent.getPixel(j, i)); // i, j sont inversés
                    }
                }
            }
            numRangeeDePixels += tailleObturateur + oneMore;
            if (VERBOSE) Log.d(TAG, "fin haut bas");
        } else {
            Log.d(TAG, "Erreur, toutes les Bitmaps on été traitées");
        }
    }

    /**
     * Algorithme gauche vers droite
     * @param bitmapCurrent
     * @param oneMore
     */
    public void gaucheDroite(Bitmap bitmapCurrent, int oneMore) {
        if (bitmapTraitees < nbBitmap) {
            if (VERBOSE) Log.d(TAG, "debut gauche droite");
            for (int j = numRangeeDePixels; j < numRangeeDePixels + tailleObturateur + oneMore; j++) {
                if (j<largeur) {
                    for (int i = 0; i < hauteur; i++) { // pour chaque element dans la ligne
                        bitmapResult.setPixel(j, i, bitmapCurrent.getPixel(j, i)); // i, j sont inversés
                    }
                }
            }
            numRangeeDePixels += tailleObturateur + oneMore;
            if (VERBOSE) Log.d(TAG, "fin gauche droite");
        } else {
            Log.d(TAG, "Erreur, toutes les Bitmaps on été traitées");
            System.out.println("Erreur toutes bitmap traitee");
        }
    }

    /**
     * Algorithme bas vers haut
     * @param bitmapCurrent
     * @param oneMore
     */
    public void basHaut(Bitmap bitmapCurrent, int oneMore) {
        if (bitmapTraitees < nbBitmap) {
            if (VERBOSE) Log.d(TAG, "debut bas haut");
            for (int i = numRangeeDePixels; i > numRangeeDePixels - tailleObturateur - oneMore ; i--) {
                if (i>=0) {
                    for (int j = 0; j < largeur; j++) { // pour chaque element dans la ligne
                        bitmapResult.setPixel(j, i, bitmapCurrent.getPixel(j, i)); // i, j sont inversés
                    }
                }
            }
            numRangeeDePixels = numRangeeDePixels - tailleObturateur - oneMore;
            if (VERBOSE) Log.d(TAG, "fin bas haut");
        } else {
            Log.d(TAG, "Erreur, toutes les Bitmaps on été traitées");
        }
    }

    /**
     * Algorithme droite vers gauche
     * @param bitmapCurrent
     * @param oneMore
     */
    public void droiteGauche(Bitmap bitmapCurrent, int oneMore) {
        if (bitmapTraitees < nbBitmap) {
            if (VERBOSE) Log.d(TAG, "debut gauche droite");
            for (int j = numRangeeDePixels; j > numRangeeDePixels - tailleObturateur - oneMore; j--) {
                if (j>=0) {
                    for (int i = 0; i < hauteur; i++) { // pour chaque element dans la ligne
                        bitmapResult.setPixel(j, i, bitmapCurrent.getPixel(j, i)); // i, j sont inversés
                    }
                }
            }
            numRangeeDePixels = numRangeeDePixels - tailleObturateur - oneMore;
            if (VERBOSE) Log.d(TAG, "fin gauche droite");
        } else {
            Log.d(TAG, "Erreur, toutes les Bitmaps on été traitées");
        }
    }


    /**
     * S'il reste une bitmap a traiter, on la traite ici
     * Cette fonction est lancé après la boucle principale dans l'asyncTask
     * @param bitmapCurrent
     */
    public void combler(Bitmap bitmapCurrent){
        switch (direction){
            case DOWN:
                if (numRangeeDePixels < hauteur - 1)
                comblerHautBas(bitmapCurrent);
                break;
            case RIGHT:
                if (numRangeeDePixels < largeur - 1)
                comblerGaucheDroite(bitmapCurrent);
                break;
            case UP:
                if (numRangeeDePixels > 0)
                comblerBasHaut(bitmapCurrent);
                break;
            case LEFT:
                if (numRangeeDePixels > 0)
                comblerDroiteGauche(bitmapCurrent);
                break;
        }
    }

    /**
     * Combler pour haut vers bas
     * @param bitmapCurrent
     */
    public void comblerHautBas(Bitmap bitmapCurrent){
        for (int i = numRangeeDePixels; i < hauteur; i++) {
            for (int j = 0; j < largeur; j++) {
                bitmapResult.setPixel(j, i, bitmapCurrent.getPixel(j, i));
            }
        }
    }

    /**
     * Combler pour gauche vers droite
     * @param bitmapCurrent
     */
    public void comblerGaucheDroite(Bitmap bitmapCurrent){
        for (int j = numRangeeDePixels; j < largeur; j++) {
            for (int i = 0; i < hauteur; i++) {
                bitmapResult.setPixel(j, i, bitmapCurrent.getPixel(j, i));
            }
        }
    }

    /**
     * Combler pour bas vers haut
     * @param bitmapCurrent
     */
    public void comblerBasHaut(Bitmap bitmapCurrent){
        for (int i = numRangeeDePixels; i >= 0; i--) {
            for (int j = 0; j < largeur; j++) {
                bitmapResult.setPixel(j, i, bitmapCurrent.getPixel(j, i));
            }
        }
    }

    /**
     * Combler pour  droite vers gauche
     * @param bitmapCurrent
     */
    public void comblerDroiteGauche(Bitmap bitmapCurrent) {
        for (int j = numRangeeDePixels; j >= 0; j--) {
            for (int i = 0; i < hauteur; i++) {
                bitmapResult.setPixel(j, i, bitmapCurrent.getPixel(j, i));
            }
        }
    }

}
