package fr.univtln.group3.anamorphosisandroid;

import android.graphics.Bitmap;
import android.util.Log;

public class AlgoObturateur {

    public static void hautVersBas() {
        int N = 3;
        int h = 3;
        int l = 3;
        int[][][] liste_img = new int[N][h][l];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < h; j++) {
                for (int k = 0; k < l; k++) {
                    liste_img[i][j][k] = k + j * h + i * h * l;
                }
            }
        }


        int[][] img = new int[h][l];


        int s1 = N / h;    // combien y a trop ou pas assez d’images par rapport a la hauteur de l’image finale
        int s2 = N % h; // le reste
        int s3;
        if (s2 != 0)
            s3 = h / s2;
        else
            s3 = h + 1; // pour ne jamais remplir la condition du if


        int saut = s3;
        int cpt = 0;
        for (int i = 0; i < h; i++) {  // pour chaque ligne de l’image finale
            if (i > saut) {
                cpt++;
                saut += s3;
            }
            for (int j = 0; j < l; j++) { // pour chaque element dans la ligne
                img[i][j] = liste_img[cpt][i][j];
            }
            cpt += s1;
        }


        Log.d("lol", "AFFICHAGE IMG FINALE");

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < l; j++) {
                Log.d("lol", Integer.toString(img[i][j]));
            }
        }
    }


    public static void basVersHaut() {
        int N = 3;
        int h = 3;
        int l = 3;
        int[][][] liste_img = new int[N][h][l];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < h; j++) {
                for (int k = 0; k < l; k++) {
                    liste_img[i][j][k] = k + j * h + i * h * l;
                }
            }
        }


        int[][] img = new int[h][l];


        int s1 = N / h;    // combien y a trop ou pas assez d’images par rapport a la hauteur de l’image finale
        int s2 = N % h; // le reste
        int s3;
        if (s2 != 0)
            s3 = h / s2;
        else
            s3 = h + 1; // pour ne jamais remplir la condition du if


        int saut = s3;
        int cpt = 0;
        for (int i = 0; i < h; i++) {  // pour chaque ligne de l’image finale
            if (i > saut) {
                cpt++;
                saut += s3;
            }
            for (int j = 0; j < l; j++) { // pour chaque element dans la ligne
                img[i][j] = liste_img[cpt][h-1-i][j];
            }
            cpt += s1;
        }


        Log.d("lol", "AFFICHAGE IMG FINALE");

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < l; j++) {
                Log.d("lol", Integer.toString(img[i][j]));
            }
        }
    }


    public static void gaucheVersDroite(Bitmap source) {
        int N = 3;
        int h = 3;
        int l = 3;
        int[][][] liste_img = new int[N][h][l];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < h; j++) {
                for (int k = 0; k < l; k++) {
                    liste_img[i][j][k] = k + j * h + i * h * l;
                }
            }
        }


        int[][] img = new int[h][l];

        if(N < l)// si le nombre d'images et inferieur à la largeur
        {
            int s1 = l / N;
            int s2 = l % N; //le reste
        }

        int s1 = N / l;    // combien y a trop ou pas assez d’images par rapport a la hauteur de l’image finale
        int s2 = N % l; // le reste
        int s3;
        if (s2 != 0)
            s3 = l / s2;
        else
            s3 = l + 1; // pour ne jamais remplir la condition du if


        int saut = s3;
        int cpt = 0;
        for (int j = 0; j < l; j++) {  // pour chaque ligne de l’image finale
            if (j > saut) {
                cpt++;
                saut += s3;
            }
            for (int i = 0; i < h; i++) { // pour chaque element dans la ligne
                img[i][j] = liste_img[cpt][i][j];
            }
            cpt += s1;
        }


        Log.d("lol", "AFFICHAGE IMG FINALE");

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < l; j++) {
                Log.d("lol", Integer.toString(img[i][j]));
            }
        }
    }


    public static void droiteVersGauche() {
        int N = 3;
        int h = 3;
        int l = 3;
        int[][][] liste_img = new int[N][h][l];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < h; j++) {
                for (int k = 0; k < l; k++) {
                    liste_img[i][j][k] = k + j * h + i * h * l;
                }
            }
        }


        int[][] img = new int[h][l];


        int s1 = N / l;    // combien y a trop ou pas assez d’images par rapport a la hauteur de l’image finale
        int s2 = N % l; // le reste
        int s3;
        if (s2 != 0)
            s3 = l / s2;
        else
            s3 = l + 1; // pour ne jamais remplir la condition du if


        int saut = s3;
        int cpt = 0;
        for (int j = 0; j < l; j++) {  // pour chaque ligne de l’image finale
            if (j > saut) {
                cpt++;
                saut += s3;
            }
            for (int i = 0; i < h; i++) { // pour chaque element dans la ligne
                img[i][j] = liste_img[cpt][i][l-1-j];
            }
            cpt += s1;
        }


        Log.d("lol", "AFFICHAGE IMG FINALE");

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < l; j++) {
                Log.d("lol", Integer.toString(img[i][j]));
            }
        }
    }


}
