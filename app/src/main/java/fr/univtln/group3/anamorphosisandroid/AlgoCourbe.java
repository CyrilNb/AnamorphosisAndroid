package fr.univtln.group3.anamorphosisandroid;


import android.graphics.Bitmap;
import android.util.Log;

import static java.lang.Math.*;

public class AlgoCourbe {

    private String TAG = "AlgoCourbe";

    private int pictureWidth;
    private int pictureHeight;
    private Droite perpendiculaireCourante;
    private Droite perpendiculairePrecedente;
    private float[][] pointsCourbe;
    private int nbBitmap;
    private int sautPoint;
    private float sautPonctuel;
    private boolean jumpBitmap = false;

    private int bitmapTraitees = 0;
    private int compteurSautPonctuel = 0;
    private int positionPoint = 1;
    Bitmap bitmapResult;

    public enum CONTRAINTE{
        NW,
        SW,
        NE,
        SE
    }
    private CONTRAINTE contrainte;

    public AlgoCourbe(Bitmap bitmapResult, float[][] pointsCourbe, CONTRAINTE contrainte, int nbBitmap, int pictureHeight, int pictureWidth) {
        this.bitmapResult = bitmapResult;
        this.pointsCourbe = pointsCourbe;
        this.contrainte = contrainte;
        this.nbBitmap = nbBitmap;
        this.pictureHeight = pictureHeight;
        this.pictureWidth = pictureWidth;

        sautPoint = (pointsCourbe.length - 2) / nbBitmap;
        sautPonctuel = (float) nbBitmap / ((float) (pointsCourbe.length - 2) % (float) nbBitmap);
        if ((pointsCourbe.length - 2) < nbBitmap) jumpBitmap = true;

        System.out.println("largeur: " + pictureWidth);
        System.out.println("hauteru: " + pictureHeight);

        System.out.println("nbPoints: " + pointsCourbe.length);
        System.out.println("nbBitmap: "+ nbBitmap);
        System.out.println("SautPoint: " + sautPoint);
        System.out.println("SautPonctuel: " + sautPonctuel);
        System.out.println("jump: " + jumpBitmap);

    }

    private Droite calculTangente(){

        float numerateur = pointsCourbe[positionPoint -1][1] - pointsCourbe[positionPoint +1][1];
        float denominateur = pointsCourbe[positionPoint -1][0] - pointsCourbe[positionPoint +1][0];
        if (denominateur == 0){   // droite verticale x=cst
            return new Droite(null, null, pointsCourbe[positionPoint][0]);
        }
        float coeffDirecteur = numerateur / denominateur;
        float ordonneeOrigine = pointsCourbe[positionPoint -1][1] - coeffDirecteur * pointsCourbe[positionPoint -1][0];
        return new Droite(coeffDirecteur, ordonneeOrigine, null);
    }

    public void calculPerpendiculaire(){

        perpendiculairePrecedente = perpendiculaireCourante;
        Droite tangente = calculTangente();

        if (tangente.getXcst() != null){  // perpendiculaire horizontale
            perpendiculaireCourante =  new Droite(0f, pointsCourbe[positionPoint][1], null);
        }

        else if (tangente.getCoeffDirecteur() == 0){  // tangente horizontale y=cst donc perpendiculaire verticale x=cst
            perpendiculaireCourante =  new Droite(null, null, pointsCourbe[positionPoint][0]);
        }
        else {
            float coeffDirecteur = -1 / tangente.getCoeffDirecteur();
            float ordonneeOrigine = pointsCourbe[positionPoint][1] - coeffDirecteur * pointsCourbe[positionPoint][0];
            perpendiculaireCourante = new Droite(coeffDirecteur, ordonneeOrigine, null);
        }
    }

    public void remplissage(Bitmap bitmapCurrent){
        System.out.println("Position: " + positionPoint);
        if (bitmapTraitees < nbBitmap) {
            if (positionPoint < pointsCourbe.length - 1){
                calculPerpendiculaire();
                if (perpendiculaireCourante == null) {
                    // erreur
                    System.out.println("erreur, aucune perpendiculaire n'a été calculée");
                } else if (perpendiculairePrecedente == null) {
                    //  première perpendiculaire
                    System.out.println("rempli debut");
                    remplirDebutFin(bitmapCurrent);
                } else {
                    // entre
                    System.out.println("rempli entre");
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
                System.out.println("rempli FIN !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                contrainte = contrainteFin();
                remplirDebutFin(bitmapCurrent);
            }

        }
        else{
            Log.d(TAG, "Nombre de botmaps dépassé.");
        }
    }

    private void rempliLigne(Bitmap bitmapCurrent){
        System.out.println("rempli ligne");
        for (int y = 0; y < pictureHeight; y++) {
            int x1 = (int) perpendiculaireCourante.f2y(y);
            int x2 = (int) perpendiculairePrecedente.f2y(y);
            int[] result = ajustX(x1, x2);
            if (result[2] == 1){
                x1 = result[0];
                x2 = result[1];

                int minx =  min(x1,x2);
                int maxx =  max(x1, x2);
                for (int x = minx; x < maxx; x++) {
                    bitmapResult.setPixel(x, invertY(y), bitmapCurrent.getPixel(x, invertY(y)));
                }
            }
//            System.out.println("rempli droite ("+x1+","+y+") ("+x2+","+y+").");
        }
    }

    private void rempliColonne(Bitmap bitmapCurrent){
        System.out.println("rempli colonne");
        for (int x = 0; x < pictureWidth; x++) {
            int y1 = (int) perpendiculaireCourante.f2x(x);
            int y2 = (int) perpendiculairePrecedente.f2x(x);
            int[] result = ajustY(y1, y2);
            if (result[2]==1) {
                y1 = result[0];
                y2 = result[1];
//                System.out.println("rempli droite (" + x + "," + y1 + ") (" + x + "," + y2 + ").");

                int miny = min(y1, y2);
                int maxy = max(y1, y2);
                for (int y = miny; y < maxy; y++) {
                    bitmapResult.setPixel(x, invertY(y), bitmapCurrent.getPixel(x, invertY(y)));
                }
            }
        }
    }

    private int invertY(int y){
        return pictureHeight - 1 - y;
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

    private int[] ajustX(int x1, int x2){
        int[] result = {x1, x2, 1};
        if ((x1>=0 && x1<=pictureWidth) && (x2>=0 && x2<=pictureWidth)){
            return result;
        }
        else if (x1>=0 && x1<=pictureWidth){
            if (x2<0) result[1] = 0;
            else result[1] = pictureWidth;
        }
        else if(x2>=0 && x2<=pictureWidth) {
            if (x1<0) result[0] = 0;
            else result[0] = pictureWidth;
        }
        else{
            result[2] = 0;
        }

        return result;
    }


    private void rempliPAV(Bitmap bitmapCurrent){
        float xcst1 = perpendiculaireCourante.getXcst();
        float xcst2 = perpendiculairePrecedente.getXcst();
        xcst1 = (xcst1==pictureWidth)?pictureWidth-1:xcst1;
        xcst2 = (xcst2==pictureWidth)?pictureWidth-1:xcst2;
        int minx =  min((int) xcst1, (int) xcst2);
        int maxx =  max((int) xcst1, (int) xcst2);
        for (int y = 0; y < pictureHeight; y++) {
//            System.out.println("rempli droite ("+ xcst1+","+y+") ("+xcst2+","+y+").");
            for (int x = minx; x<maxx; x++)
            bitmapResult.setPixel(x, invertY(y), bitmapCurrent.getPixel(x, invertY(y)));
        }
    }

    private void remplirDebutFin(Bitmap bitmapCurrent){
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

//                    System.out.println("rempli droite ("+x1+","+y1+") ("+x2+","+y2+").");
                    for (int x=x1;x<x2;x++){
                        bitmapResult.setPixel(x, y1, bitmapCurrent.getPixel(x, y1));
                    }
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
                        x1 = 0;
                    }

                    //  appelle fonction remplissagePixels(x1, y1, x2, y2)
                    ordonnéeRemplissage++;
                }
                break;

            case SW:
                System.out.println("remplisage debut (contrainte SW) ");
                Droite test = new Droite(null, null, (float) pictureWidth);
                ordIntersection = (int) perpendiculaireCourante.intersection(test)[1];
                ordonnéeRemplissage = (ordIntersection > 0) ? ordIntersection : 0;
                while (ordonnéeRemplissage < pictureHeight){

                    x1 = (int) perpendiculaireCourante.f2y(ordonnéeRemplissage);
                    y1 = ordonnéeRemplissage;
                    x2 = pictureWidth;
                    y2 = ordonnéeRemplissage;

                    if (x1 < 0){
                        x1 = 0;
                    }

                    //  appelle fonction remplissagePixels(x1, y1, x2, y2)
                    for (int x=x1;x<x2;x++){
                        bitmapResult.setPixel(x, y1, bitmapCurrent.getPixel(x, y1));
                    }
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

    public void extractAndCopy(Bitmap bitmapCurrent){
        int oneMore = 0;
        if (bitmapTraitees > compteurSautPonctuel){
            oneMore = 1;
            compteurSautPonctuel += sautPonctuel;
        }
        if (jumpBitmap && oneMore == 1){
            remplissage(bitmapCurrent);
            System.out.println("pos: " + positionPoint);
            positionPoint ++;
        }
        else if (!jumpBitmap){
            positionPoint += oneMore;
            remplissage(bitmapCurrent);
            positionPoint += sautPoint;
        }

        System.out.println("bitmap traitee: " + bitmapTraitees);
        bitmapTraitees ++;
    }
}
