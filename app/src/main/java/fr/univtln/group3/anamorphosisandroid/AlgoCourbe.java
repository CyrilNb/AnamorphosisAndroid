package fr.univtln.group3.anamorphosisandroid;


import android.util.Log;

import static java.lang.Math.abs;
import static java.lang.Math.acos;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;

public class AlgoCourbe {

    private String TAG = "AlgoCourbe";

    private final int pictureWidth = 720;
    private final int pictureHeight = 1280;
    private Droite perpendiculaireCourante;
    private Droite perpendiculairePrecedente;
    private float[][] pointsCourbe;

    public enum CONTRAINTE{
        NW,
        SW,
        NE,
        SE
    }
    private CONTRAINTE contrainte;

    private Droite calculeTangente(int position){

        float numerateur = pointsCourbe[position-1][1] - pointsCourbe[position+1][1];
        float denominateur = pointsCourbe[position-1][0] - pointsCourbe[position+1][0];
        if (denominateur == 0){   // droite verticale x=cst
            return new Droite(null, null, pointsCourbe[position][0]);
        }
        float coeffDirecteur = numerateur / denominateur;
        float ordonneeOrigine = pointsCourbe[position-1][1] - coeffDirecteur * pointsCourbe[position-1][0];
        return new Droite(coeffDirecteur, ordonneeOrigine, null);
    }

    private Droite calculePerpendiculaire(int position, Droite tangente){

        if (tangente.getXcst() != null){  // perpendiculaire horizontale
            return new Droite(0f, pointsCourbe[position][1], null);
        }

        if (tangente.getCoeffDirecteur() == 0){  // tangente horizontale y=cst donc perpendiculaire verticale x=cst
            return new Droite(null, null, pointsCourbe[position][0]);
        }

        float coeffDirecteur = -1/tangente.getCoeffDirecteur();
        float ordonneeOrigine = pointsCourbe[position][1] - coeffDirecteur * pointsCourbe[position][0];
        return new Droite(coeffDirecteur, ordonneeOrigine, null);
    }

    private void remplissage(){

        if (perpendiculaireCourante == null ){
            // erreur
            Log.d(TAG, "erreur, aucune perpendiculaire n'a été calculée");
        }
        else if (perpendiculairePrecedente == null){
            //  première perpendiculaire
            remplirDebut();
        }

        else {
            String sens = getSens();
            switch (sens) {
                case "HORIZONTAL":
                    rempliLigne();
                    break;
                case "VERTICAL":
                    rempliColonne();
                    break;
                case "PAV":
                    rempliPAV();
                    break;
                case "PA":
                    rempliColonne();
                    break;
            }
        }
    }

    private void rempliLigne(){
        for (int y = 0; y < pictureHeight; y++) {
            float x1 = perpendiculaireCourante.f2y(y);
            float x2 = perpendiculairePrecedente.f2y(y);
            System.out.println("rempli droite ("+x1+","+y+") ("+x2+","+y+").");
        }
    }

    private void rempliColonne(){
        for (int x = 0; x < pictureWidth; x++) {
            float y1 = perpendiculaireCourante.f2x(x);
            float y2 = perpendiculairePrecedente.f2x(x);
            System.out.println("rempli droite ("+x+","+y1+") ("+x+","+y2+").");
        }
    }

    private void rempliPAV(){
        float xcst1 = perpendiculaireCourante.getXcst();
        float xcst2 = perpendiculairePrecedente.getXcst();
        for (int y = 0; y < pictureHeight; y++) {
            System.out.println("rempli droite ("+ xcst1+","+y+") ("+
                                xcst2+","+y+").");
        }
    }

    private void remplirDebut(){
        int ordonnéeRemplissage, ordIntersection;
        int x1, y1, x2, y2;
        switch (contrainte) {
            case SE:
                ordIntersection = (int) perpendiculaireCourante.intersection(new Droite(null, null, 0f))[1];
                ordonnéeRemplissage = (ordIntersection > 0) ? ordIntersection : 0;
                while (ordonnéeRemplissage < pictureHeight) {

                    x1 = 0;
                    y1 = ordonnéeRemplissage;
                    x2 = (int) perpendiculaireCourante.f2y(ordonnéeRemplissage);
                    y2 = ordonnéeRemplissage;

                    if (x2 > pictureWidth){
                        x2 = pictureWidth;
                    }

                    //  appelle foction remplissagePixel(x1, y1, x2, y2)
                    System.out.println("remplisage debut (contrainte SE) ");
                    ordonnéeRemplissage++;
                }
                break;

            case NE:
                ordonnéeRemplissage = 0;
                while (ordonnéeRemplissage < perpendiculaireCourante.f2x(0) && ordonnéeRemplissage < pictureHeight) {

                    x1 = 0;
                    y1 = ordonnéeRemplissage;
                    x2 = (int) perpendiculaireCourante.f2y(ordonnéeRemplissage);
                    y2 = ordonnéeRemplissage;

                    if (x2 > pictureWidth){
                        x2 = pictureWidth;
                    }

                    //  appelle fonction remplissagePixels(x1, y1, x2, y2)
                    System.out.println("remplisage debut (contrainte NE) ");
                    ordonnéeRemplissage++;
                }
                break;

            case NW:
                ordonnéeRemplissage = 0;
                while (ordonnéeRemplissage < perpendiculaireCourante.f2x(pictureWidth) && ordonnéeRemplissage < pictureWidth) {

                    x1 = (int) perpendiculaireCourante.f2y(ordonnéeRemplissage);
                    y1 = ordonnéeRemplissage;
                    x2 = pictureWidth;
                    y2 = ordonnéeRemplissage;

                    if (x1 < 0){
                        x2 = 0;
                    }

                    //  appelle fonction remplissagePixels(x1, y1, x2, y2)
                    System.out.println("remplisage debut (contrainte NW) ");
                    ordonnéeRemplissage++;
                }
                break;

            case SW:
                ordIntersection = (int) perpendiculaireCourante.intersection(new Droite(null, null, (float) pictureWidth))[1];
                ordonnéeRemplissage = (ordIntersection > 0) ? ordIntersection : 0;
                while (ordonnéeRemplissage < pictureHeight){

                    x1 = (int) perpendiculaireCourante.f2y(ordonnéeRemplissage);
                    y1 = ordonnéeRemplissage;
                    x2 = pictureWidth;
                    y2 = ordonnéeRemplissage;

                    if (x1 < 0){
                        x2 = 0;
                    }

                    //  appelle fonction remplissagePixels(x1, y1, x2, y2)
                    System.out.println("remplisage debut (contrainte SW) ");
                    ordonnéeRemplissage ++;
                }
                break;
        }
    }

    private double getAngle(float[] A, float[] B, float[] C){

        float[] vectAB = {abs(A[0]) - abs(B[0]), abs(A[1]) - abs(B[1])};
        float[] vectAC = {abs(A[0]) - abs(C[0]), abs(A[1]) - abs(C[1])};

        float prdScalaire = (vectAB[0] * vectAC[0]) + (vectAB[1] * vectAC[1]);

        double AB = sqrt(vectAB[0]*vectAB[0] + vectAB[1]*vectAB[1]);
        double AC = sqrt(vectAC[0]*vectAC[0] + vectAC[1]*vectAC[1]);

        return toDegrees(acos(prdScalaire/(AB*AC)));
    }

    private String getSens(){
        String resultParallel = perpendiculaireCourante.isParallel(perpendiculairePrecedente);
        if (! (resultParallel).equals("NO")){
            return resultParallel;
        }
        float[] ptIntersection = perpendiculaireCourante.intersection(perpendiculairePrecedente);
        float xm1 = ptIntersection[0] - 1;
        float[] ptCourante = {xm1, perpendiculaireCourante.f2x(xm1)};
        float[] ptPrecedente = {xm1, perpendiculairePrecedente.f2x(xm1)};
        double angle = getAngle(ptIntersection, ptCourante, ptPrecedente);

        return (angle > 90) ? "HORIZONTAL" : "VERTICAL";
    }
}
