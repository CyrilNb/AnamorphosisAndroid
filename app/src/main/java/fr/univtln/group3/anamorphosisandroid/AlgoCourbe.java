package fr.univtln.group3.anamorphosisandroid;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static java.lang.Math.*;

public class AlgoCourbe {

    private String TAG = "AlgoCourbe";

    private int pictureWidth;
    private int pictureHeight;
    private Droite perpendiculaireCourante;
    private Droite perpendiculairePrecedente;
    private List<float[]> pointsCourbe;
    private int nbBitmap;

    private int bitmapTraitees = 0;
    private int positionPoint = 1;
    Bitmap bitmapResult;

    boolean isDone = false;

    public enum CONTRAINTE {
        NW,
        SW,
        NE,
        SE
    }

    private CONTRAINTE contrainte;

    public AlgoCourbe(Bitmap bitmapResult, List<float[]> pointsCourbe, CONTRAINTE contrainte, int nbBitmap, int pictureHeight, int pictureWidth) {
        this.bitmapResult = bitmapResult;
        this.pointsCourbe = pointsCourbe;
        this.contrainte = contrainte;
        this.nbBitmap = nbBitmap;
        this.pictureHeight = pictureHeight;
        this.pictureWidth = pictureWidth;


        if (pointsCourbe.size() == 2) {
            majPointsForDiagonal();
        } else {
            majListePoints();
        }

        for (float[] f : pointsCourbe) {
            System.out.println(f[0] + "-" + f[1]);
        }

    }

    private void majListePoints() {

        int tailleUtilisee = (pointsCourbe.size() - 2);

        if (tailleUtilisee < nbBitmap) {

            int pointsARajouter = nbBitmap - tailleUtilisee;
            int quotientSaut = pointsARajouter / tailleUtilisee;
            float reste = ((float) pointsARajouter) % ((float) tailleUtilisee);
            float resteSaut;
            if (reste == 0) {
                resteSaut = tailleUtilisee + 1;
            } else {
                resteSaut = ((float) tailleUtilisee) / reste;
            }


            float compteurResteSaut = 0;
            int cptCheat = 0;
            for (int i = 1; i < tailleUtilisee + 1; i++) {
                int oneMore = 0;
                if (cptCheat >= compteurResteSaut) {
                    oneMore = 1;
                    compteurResteSaut += resteSaut;
                }
                addNewPoints(i, quotientSaut + oneMore);
                i += quotientSaut + oneMore;
                tailleUtilisee += quotientSaut + oneMore;

                cptCheat++;

            }
        } else if (tailleUtilisee > nbBitmap) {

            int pointsAenlever = tailleUtilisee - nbBitmap;
            float sautDelete = ((float) tailleUtilisee) / ((float) pointsAenlever);
            float compteurSautDelete = 0;

            for (int i = 1; i < tailleUtilisee + 1; i++) {
                if (i >= compteurSautDelete) {
                    pointsCourbe.set(i, null);
                    compteurSautDelete += sautDelete;
                }
            }
            pointsCourbe.removeAll(Collections.singleton(null));
        }

    }

    private void addNewPoints(int index, int nbPoints) {
        float[][] pointsARajouter = new float[nbPoints][2];
        for (int i = 0; i < nbPoints; i++) {
            pointsARajouter[i][0] = ((pointsCourbe.get(index)[0] - pointsCourbe.get(index - 1)[0]) * (((float) (i + 1)) / ((float) (nbPoints + 1)))) + pointsCourbe.get(index - 1)[0];
            pointsARajouter[i][1] = ((pointsCourbe.get(index)[1] - pointsCourbe.get(index - 1)[1]) * (((float) (i + 1)) / ((float) (nbPoints + 1)))) + pointsCourbe.get(index - 1)[1];
        }
        int iAjout = index;
        for (float[] point : pointsARajouter
                ) {
            pointsCourbe.add(iAjout, point);
            iAjout++;
        }
    }

    private void majPointsForDiagonal() {
        float[] p1 = pointsCourbe.get(0);
        float[] p2 = pointsCourbe.get(1);
        if (p1[0] < p2[0]) {
            if (p1[1] < p2[1]) contrainte = CONTRAINTE.NE;
            else contrainte = CONTRAINTE.SE;
        } else {
            if (p1[1] < p2[1]) contrainte = CONTRAINTE.NW;
            else contrainte = CONTRAINTE.SW;
        }

        addNewPoints(1, nbBitmap);
    }

    private Droite calculTangente() {

        float numerateur = pointsCourbe.get(positionPoint - 1)[1] - pointsCourbe.get(positionPoint + 1)[1];
        float denominateur = pointsCourbe.get(positionPoint - 1)[0] - pointsCourbe.get(positionPoint + 1)[0];
        if (denominateur == 0) {   // droite verticale x=cst
            return new Droite(null, null, pointsCourbe.get(positionPoint)[0]);
        }
        float coeffDirecteur = numerateur / denominateur;
        float ordonneeOrigine = pointsCourbe.get(positionPoint - 1)[1] - coeffDirecteur * pointsCourbe.get(positionPoint - 1)[0];
        return new Droite(coeffDirecteur, ordonneeOrigine, null);
    }

    public void calculPerpendiculaire() {

        perpendiculairePrecedente = perpendiculaireCourante;
        Droite tangente = calculTangente();

        if (tangente.getXcst() != null) {  // perpendiculaire horizontale
            perpendiculaireCourante = new Droite(0f, pointsCourbe.get(positionPoint)[1], null);
        } else if (tangente.getCoeffDirecteur() == 0) {  // tangente horizontale y=cst donc perpendiculaire verticale x=cst
            perpendiculaireCourante = new Droite(null, null, pointsCourbe.get(positionPoint)[0]);
        } else {
            float coeffDirecteur = -1 / tangente.getCoeffDirecteur();
            float ordonneeOrigine = pointsCourbe.get(positionPoint)[1] - coeffDirecteur * pointsCourbe.get(positionPoint)[0];
            perpendiculaireCourante = new Droite(coeffDirecteur, ordonneeOrigine, null);
        }
    }

    public void remplissage(Bitmap bitmapCurrent) {
        if (bitmapTraitees < nbBitmap) {
            if (positionPoint < pointsCourbe.size() - 1) {
                calculPerpendiculaire();
                if (perpendiculaireCourante == null) {
                    // erreur
//                    System.out.println("erreur, aucune perpendiculaire n'a été calculée");
                } else if (perpendiculairePrecedente == null) {
                    //  première perpendiculaire
//                    System.out.println("rempli debut");
                    remplirDebutFin(bitmapCurrent);
                } else {
                    // entre
//                    System.out.println("rempli entre");
                    String sens = getSens();
                    switch (sens) {
                        case "HORIZONTAL":
                            rempliLigne(bitmapCurrent);

                            break;
                        case "VERTICAL":
                            rempliColonne(bitmapCurrent);

                            break;
                        case "PAV":
                            rempliPAV(bitmapCurrent);

                            break;
                        case "PA":
                            rempliColonne(bitmapCurrent);

                            break;
                    }
                }
            } else {
                if (!isDone) {
//                    System.out.println("rempli FIN !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    contrainte = contrainteFin();
                    remplirDebutFin(bitmapCurrent);
                    isDone = true;
                }
            }

        }
//        else{
//            System.out.println("Nombre de botmaps dépassé.");
//
//        }
    }

    private void rempliLigne(Bitmap bitmapCurrent) {
//        System.out.println("rempli ligne");
        for (int y = 0; y < pictureHeight; y++) {
            int x1 = (int) perpendiculaireCourante.f2y(y);
            int x2 = (int) perpendiculairePrecedente.f2y(y);

            int minx = min(x1, x2);
            int maxx = max(x1, x2) + 1;

            int[] result = ajustX(minx, maxx);
            if (result[2] == 1) {
                minx = result[0];
                maxx = result[1];

                for (int x = minx; x < maxx; x++) {
                    bitmapResult.setPixel(x, invertY(y), bitmapCurrent.getPixel(x, invertY(y)));
//                    System.out.println("rempli droite ("+x1+","+y+") ("+x2+","+y+").");
                }
            }
        }
    }

    private void rempliColonne(Bitmap bitmapCurrent) {
//        System.out.println("rempli colonne");
        for (int x = 0; x < pictureWidth; x++) {
            int y1 = (int) perpendiculaireCourante.f2x(x);
            int y2 = (int) perpendiculairePrecedente.f2x(x);

            int miny = min(y1, y2);
            int maxy = max(y1, y2) + 1;

            int[] result = ajustY(miny, maxy);
            if (result[2] == 1) {
                miny = result[0];
                maxy = result[1];

                for (int y = miny; y < maxy; y++) {
                    bitmapResult.setPixel(x, invertY(y), bitmapCurrent.getPixel(x, invertY(y)));
                }
            }
        }
    }

    private int invertY(int y) {
        return pictureHeight - 1 - y;
    }


    private int[] ajustY(int y1, int y2) {
        int[] result = {y1, y2, 1};
        if ((y1 >= 0 && y1 <= pictureHeight) && (y2 >= 0 && y2 <= pictureHeight)) {
            // les 2 interieur
            return result;
        } else if (y1 >= 0 && y1 <= pictureHeight) {
            // y1 interieur
            if (y2 < 0) result[1] = 0;
            else result[1] = pictureHeight;
        } else if (y2 >= 0 && y2 <= pictureHeight) {
            // y2 interieur
            if (y1 < 0) result[0] = 0;
            else result[0] = pictureHeight;
        } else if (y1 > pictureHeight && y2 < 0) {
            result[0] = pictureHeight;
            result[1] = 0;
        } else if (y2 > pictureHeight && y1 < 0) {
            result[0] = 0;
            result[1] = pictureHeight;
        } else {
            result[2] = 0;
        }

        return result;
    }

    private int[] ajustX(int x1, int x2) {
        int[] result = {x1, x2, 1};
        if ((x1 >= 0 && x1 <= pictureWidth) && (x2 >= 0 && x2 <= pictureWidth)) {
            return result;
        } else if (x1 >= 0 && x1 <= pictureWidth) {
            if (x2 < 0) result[1] = 0;
            else result[1] = pictureWidth;
        } else if (x2 >= 0 && x2 <= pictureWidth) {
            if (x1 < 0) result[0] = 0;
            else result[0] = pictureWidth;
        } else if (x1 > pictureWidth && x2 < 0) {
            result[0] = pictureWidth;
            result[1] = 0;
        } else if (x2 > pictureWidth && x1 < 0) {
            result[0] = 0;
            result[1] = pictureWidth;
        } else {
            result[2] = 0;
        }

        return result;
    }


    private void rempliPAV(Bitmap bitmapCurrent) {
//        System.out.println("rempli PAV");
        float xcst1 = perpendiculaireCourante.getXcst();
        float xcst2 = perpendiculairePrecedente.getXcst();
        xcst1 = (xcst1 == pictureWidth) ? pictureWidth - 1 : xcst1;
        xcst2 = (xcst2 == pictureWidth) ? pictureWidth - 1 : xcst2;
        int minx = min((int) xcst1, (int) xcst2);
        int maxx = max((int) xcst1, (int) xcst2);
        for (int y = 0; y < pictureHeight; y++) {
//            System.out.println("rempli droite ("+ xcst1+","+y+") ("+xcst2+","+y+").");
            for (int x = minx; x < maxx; x++)
                bitmapResult.setPixel(x, invertY(y), bitmapCurrent.getPixel(x, invertY(y)));
        }
    }

    public void remplirDebutFin(Bitmap bitmapCurrent) {
        int ordonnéeRemplissage, ordIntersection;
        int x1, y1, x2, y2;
        switch (contrainte) {
            case SE:
                ordIntersection = (int) perpendiculaireCourante.intersection(new Droite(null, null, 0f))[1];
                ordonnéeRemplissage = (ordIntersection > 0) ? ordIntersection : 0;
                while (ordonnéeRemplissage < pictureHeight) {

                    x1 = 0;
                    y1 = ordonnéeRemplissage;
                    x2 = (int) perpendiculaireCourante.f2y(ordonnéeRemplissage + 1);
                    y2 = ordonnéeRemplissage;

                    if (x2 > pictureWidth) {
                        x2 = pictureWidth;
                    }
                    //  appelle foction remplissagePixel(x1, y1, x2, y2)

                    for (int x = x1; x < x2; x++) {
                        bitmapResult.setPixel(x, invertY(y1), bitmapCurrent.getPixel(x, invertY(y1)));
                    }
                    ordonnéeRemplissage++;
                }
                break;

            case NE:
                ordonnéeRemplissage = 0;
                while (ordonnéeRemplissage < perpendiculaireCourante.f2x(0) && ordonnéeRemplissage < pictureHeight) {

                    x1 = 0;
                    y1 = ordonnéeRemplissage;
                    x2 = (int) perpendiculaireCourante.f2y(ordonnéeRemplissage) + 1;
                    y2 = ordonnéeRemplissage;

                    if (x2 > pictureWidth) {
                        x2 = pictureWidth;
                    }

                    //  appelle fonction remplissagePixels(x1, y1, x2, y2)

//                    System.out.println("rempli droite ("+x1+","+y1+") ("+x2+","+y2+").");
                    for (int x = x1; x < x2; x++) {
                        bitmapResult.setPixel(x, invertY(y1), bitmapCurrent.getPixel(x, invertY(y1)));
                    }
                    ordonnéeRemplissage++;
                }
                break;

            case NW:
                ordonnéeRemplissage = 0;
                while (ordonnéeRemplissage < perpendiculaireCourante.f2x(pictureWidth) && ordonnéeRemplissage < pictureWidth) {

                    x1 = (int) perpendiculaireCourante.f2y(ordonnéeRemplissage) - 1;
                    y1 = ordonnéeRemplissage;
                    x2 = pictureWidth;
                    y2 = ordonnéeRemplissage;

                    if (x1 < 0) {
                        x1 = 0;
                    }

                    //  appelle fonction remplissagePixels(x1, y1, x2, y2)
                    for (int x = x1; x < x2; x++) {
                        bitmapResult.setPixel(x, invertY(y1), bitmapCurrent.getPixel(x, invertY(y1)));
                    }
                    ordonnéeRemplissage++;
                }
                break;

            case SW:
                ordIntersection = (int) perpendiculaireCourante.intersection(new Droite(null, null, (float) pictureWidth))[1];
                ordonnéeRemplissage = (ordIntersection > 0) ? ordIntersection : 0;
                while (ordonnéeRemplissage < pictureHeight) {

                    x1 = (int) perpendiculaireCourante.f2y(ordonnéeRemplissage + 1);
                    y1 = ordonnéeRemplissage;
                    x2 = pictureWidth;
                    y2 = ordonnéeRemplissage;

                    if (x1 < 0) {
                        x1 = 0;
                    }

                    //  appelle fonction remplissagePixels(x1, y1, x2, y2)
                    for (int x = x1; x < x2; x++) {
                        bitmapResult.setPixel(x, invertY(y1), bitmapCurrent.getPixel(x, invertY(y1)));
                    }
                    ordonnéeRemplissage++;
                }
                break;
        }
    }

    private double getAngle(float[] A, float[] B, float[] C) {

        float[] vectAB = {abs(A[0]) - abs(B[0]), abs(A[1]) - abs(B[1])};
        float[] vectAC = {abs(A[0]) - abs(C[0]), abs(A[1]) - abs(C[1])};

        float prdScalaire = (vectAB[0] * vectAC[0]) + (vectAB[1] * vectAC[1]);

        double AB = sqrt(vectAB[0] * vectAB[0] + vectAB[1] * vectAB[1]);
        double AC = sqrt(vectAC[0] * vectAC[0] + vectAC[1] * vectAC[1]);

        return toDegrees(acos(prdScalaire / (AB * AC)));
    }

    private String getSens() {
        String resultParallel = perpendiculaireCourante.isParallel(perpendiculairePrecedente);
        if (!(resultParallel).equals("NO")) {
            return resultParallel;
        }
        float[] ptIntersection = perpendiculaireCourante.intersection(perpendiculairePrecedente);
        float xm1 = ptIntersection[0] - 1;
        float[] ptCourante = {xm1, perpendiculaireCourante.f2x(xm1)};
        float[] ptPrecedente = {xm1, perpendiculairePrecedente.f2x(xm1)};
        double angle = getAngle(ptIntersection, ptCourante, ptPrecedente);

        return (angle > 90) ? "HORIZONTAL" : "VERTICAL";
    }


    private CONTRAINTE contrainteFin() {
        switch (contrainte) {
            case SE:
                return CONTRAINTE.NW;

            case SW:
                return CONTRAINTE.NE;

            case NE:
                return CONTRAINTE.SW;

            case NW:
                return CONTRAINTE.SE;
        }
        return null;
    }

    public void extractAndCopy(Bitmap bitmapCurrent) {
        remplissage(bitmapCurrent);
        positionPoint++;
        bitmapTraitees++;
    }

    public void combler(Bitmap bitmapCurrent) {
        if (!isDone) {
            positionPoint--; // ne sert a rien juste pour se rappeler
            contrainte = contrainteFin();
            remplirDebutFin(bitmapCurrent);
        }
    }


}
