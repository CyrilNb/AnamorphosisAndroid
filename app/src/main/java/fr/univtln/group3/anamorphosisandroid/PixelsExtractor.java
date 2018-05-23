package fr.univtln.group3.anamorphosisandroid;

import android.graphics.Bitmap;

public class PixelsExtractor {
    Direction direction;


    int rangeesPixelsCopies;
    int largeur;
    int hauteur;
    int nbBitmap;

    int tailleObturateur;
    float resteObturateur;
    float compteurResteObturateur;
    int bitmapTraitees;

    public PixelsExtractor(Direction direction, int largeur, int hauteur, int nbBitmap){
        rangeesPixelsCopies = 0;
        this.direction = direction;
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.nbBitmap = nbBitmap;

        switch (direction){
            case HAUT_BAS:
                tailleObturateur = hauteur / nbBitmap;
                resteObturateur = (float) nbBitmap / ((float) nbBitmap - ((float) hauteur % (float) nbBitmap));
            case GAUCHE_DROITE:
                tailleObturateur = largeur / nbBitmap;
                resteObturateur = (float) nbBitmap / ((float) nbBitmap - ((float) largeur % (float) nbBitmap));
            case BAS_HAUT:
                tailleObturateur = hauteur / nbBitmap;
                resteObturateur = (float) nbBitmap / ((float) nbBitmap - ((float) hauteur % (float) nbBitmap));
            case DROITE_GAUCHE:
                tailleObturateur = largeur / nbBitmap;
                resteObturateur = (float) nbBitmap / ((float) nbBitmap - ((float) largeur % (float) nbBitmap));
        }
        compteurResteObturateur = 0;
        bitmapTraitees = 0;

    }


    public enum Direction{
        HAUT_BAS,
        GAUCHE_DROITE,
        BAS_HAUT,
        DROITE_GAUCHE
    }

    public void extractAndCopy(Bitmap bitmap){
        switch (direction){
            case HAUT_BAS:
                hautBas(bitmap);
        }
    }

    public void hautBas(Bitmap bitmap){
        if (bitmapTraitees < nbBitmap){
            System.out.println("debut haut vers bas");
            int[] pixelsSourceFrame = new int[largeur * hauteur];
            bitmap.getPixels(pixelsSourceFrame, 0, largeur, 0, 0, largeur, hauteur);


            int[] pixelsResult = new int[largeur * hauteur];
            result.getPixels(pixelsResult,0,largeur,0,0,largeur,hauteur);

            for (int i = numLigneStart; i < nombreDeLigneAPRENDRE + numLigneStart; i++) {
                for (int j = 0; j < largeur; j++) { // pour chaque element dans la ligne
                    if(j ==0 ){
                        int index = (i * largeur) + j;
                        System.out.println("index: "+ index);
                    }
                    pixelsResult[(i * largeur) + j] = pixelsSourceFrame[(i * largeur)  + j];
                }
            }
            System.out.println("je change les pixels");
            result.setPixels(pixelsResult, 0, largeur, 0, 0, largeur, hauteur);
            System.out.println("apres changement pixels");
        }
    }
    else{

    }

}
