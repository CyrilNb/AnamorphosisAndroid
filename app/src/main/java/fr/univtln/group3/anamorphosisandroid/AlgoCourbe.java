package fr.univtln.group3.anamorphosisandroid;


import android.graphics.Bitmap;

import static java.lang.Math.*;

public class AlgoCourbe {

    private String TAG = "AlgoCourbe";

    private final int pictureWidth = 720;
    private final int pictureHeight = 1280;
    private Droite perpendiculaireCourante;
    private Droite perpendiculairePrecedente;
    private float[][] pointsCourbe;

    int position = 1;
    Bitmap bitmapresult;

    public enum CONTRAINTE{
        NW,
        SW,
        NE,
        SE
    }
    private CONTRAINTE contrainte;

    public AlgoCourbe(CONTRAINTE contrainte) {
        float[][] L = {{0,0}, {1280,0}, {0,720}, {1280,720}};
        pointsCourbe = bezier(L, 100);

//        for (int i= 0; i<101; i++){
//            System.out.println("pt: " + pointsCourbe[i][0] + " ," + pointsCourbe[i][1]);
//        }

        this.contrainte = contrainte;
    }

    private Droite calculTangente(){

        float numerateur = pointsCourbe[position-1][1] - pointsCourbe[position+1][1];
        float denominateur = pointsCourbe[position-1][0] - pointsCourbe[position+1][0];
        if (denominateur == 0){   // droite verticale x=cst
            return new Droite(null, null, pointsCourbe[position][0]);
        }
        float coeffDirecteur = numerateur / denominateur;
        float ordonneeOrigine = pointsCourbe[position-1][1] - coeffDirecteur * pointsCourbe[position-1][0];
        return new Droite(coeffDirecteur, ordonneeOrigine, null);
    }

    public void calculPerpendiculaire(){

        perpendiculairePrecedente = perpendiculaireCourante;
        Droite tangente = calculTangente();

        if (tangente.getXcst() != null){  // perpendiculaire horizontale
            perpendiculaireCourante =  new Droite(0f, pointsCourbe[position][1], null);
        }

        else if (tangente.getCoeffDirecteur() == 0){  // tangente horizontale y=cst donc perpendiculaire verticale x=cst
            perpendiculaireCourante =  new Droite(null, null, pointsCourbe[position][0]);
        }
        else {
            float coeffDirecteur = -1 / tangente.getCoeffDirecteur();
            float ordonneeOrigine = pointsCourbe[position][1] - coeffDirecteur * pointsCourbe[position][0];
            perpendiculaireCourante = new Droite(coeffDirecteur, ordonneeOrigine, null);
        }
    }

    public void remplissage(){

        calculPerpendiculaire();
//        System.out.println("position: " + position);
//        System.out.println("point courant: " + pointsCourbe[position][0] + " ," + pointsCourbe[position][1]);
//        if (perpendiculairePrecedente != null) {
//            System.out.println("perpendiculaire précédente: " + perpendiculairePrecedente.getCoeffDirecteur() + " *x + " + perpendiculairePrecedente.getOrdOrigine());
//        }
//        else{
//            System.out.println("perpendiculaire precedente is null");
//        }
//        if (perpendiculaireCourante != null) {
//            System.out.println("perpendiculaire courante: " + perpendiculaireCourante.getCoeffDirecteur() + " *x + " + perpendiculaireCourante.getOrdOrigine());
//        }
//        else{
//            System.out.println("perpendiculaire courante is null");
//        }

        while (position < pointsCourbe.length) {
            if (perpendiculaireCourante == null) {
                // erreur
                System.out.println("erreur, aucune perpendiculaire n'a été calculée");
            } else if (perpendiculairePrecedente == null) {
                //  première perpendiculaire
                System.out.println("rempli debut");
                remplirDebutFin();
            }

            else {
                // entre
                System.out.println("rempli entre");
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
            position++;
        }
        contrainte = contrainteFin();
        remplirDebutFin();
    }

    private void rempliLigne(){
        System.out.println("rempli ligne");
        for (int y = 0; y < pictureHeight; y++) {
            int x1 = (int) perpendiculaireCourante.f2y(y);
            int x2 = (int) perpendiculairePrecedente.f2y(y);
            System.out.println("rempli droite ("+x1+","+y+") ("+x2+","+y+").");
        }
    }

    private void rempliColonne(){
        System.out.println("rempli colonne");
        for (int x = 0; x < pictureWidth; x++) {
            int y1 = (int) perpendiculaireCourante.f2x(x);
            int y2 = (int) perpendiculairePrecedente.f2x(x);
            int[] result = ajustY(y1, y2);
            if (result[2]==1) {
                y1 = result[0];
                y2 = result[1];
                System.out.println("rempli droite (" + x + "," + y1 + ") (" + x + "," + y2 + ").");
            }
        }
    }

    private int[] ajustY(int y1, int y2){
        int[] result = {y1, y2, 1};
        if ((y1>=0 && y1<=pictureHeight) && (y2>=0 && y2<=pictureHeight)){
            return result;
        }
        else if (y1>=0 && y1<=pictureHeight){
            if (y2<0) result[1] = 0;
            else result[1] = pictureHeight;
        }
        else if(y2>=0 && y2<=pictureHeight) {
            if (y1<0) result[0] = 0;
            else result[0] = pictureHeight;
        }
        else{
            result[2] = 0;
        }

        return result;
    }

    private void rempliPAV(){
        float xcst1 = perpendiculaireCourante.getXcst();
        float xcst2 = perpendiculairePrecedente.getXcst();
        for (int y = 0; y < pictureHeight; y++) {
            System.out.println("rempli droite ("+ xcst1+","+y+") ("+
                    xcst2+","+y+").");
        }
    }

    private void remplirDebutFin(){
        int ordonnéeRemplissage, ordIntersection;
        int x1, y1, x2, y2;
        switch (contrainte) {
            case SE:
                System.out.println("remplisage debut (contrainte SE) ");
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
                    ordonnéeRemplissage++;
                }
                break;

            case NE:
                System.out.println("remplisage debut (contrainte NE) ");
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
                    System.out.println("rempli droite ("+x1+","+y1+") ("+x2+","+y2+").");
                    ordonnéeRemplissage++;
                }
                break;

            case NW:
                System.out.println("remplisage debut (contrainte NW) ");
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
                    ordonnéeRemplissage++;
                }
                break;

            case SW:
                System.out.println("remplisage debut (contrainte SW) ");
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


    public float[][] bezier(float[][] L, int n) {
        // L : 4 points de controle
        // n : nombre de points tracés
        float u = 0;
        float[][] l_points = new float[n+1][2];
        for (int i=0; i<n+1; i++){
            float[] point = bezier_r(L, n, u);
            l_points[i][0] = point[0];
            l_points[i][1] = point[1];
            u += 1f / n;
        }
        return l_points;
    }


    private float[] bezier_r(float[][] L, int n, float u) {
        int N = L.length - 1;
        float[][] newL = new float[N][2];
        for (int i=0; i<N; i++) {
            newL[i][0] = (L[i][0] * (1 - u) + L[i + 1][0] * u);
            newL[i][1] = (L[i][1] * (1 - u) + L[i + 1][1] * u);
        }
        if (newL.length!= 1){
            return bezier_r(newL, 1, u);
        }
        else{
            return newL[0];
        }
    }

    private CONTRAINTE contrainteFin(){
        switch (contrainte){
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
}
