package fr.univtln.group3.anamorphosisandroid;


import static java.lang.Math.abs;

public class AlgoCourbe {

    private final int pictureWidth = 720;
    private final int pictureHeight = 1280;
    private float[] perpendiculaireCourante = {};
    private float[] perpendiculairePrecedente = {};
    private float[][] pointsCourbe;

    private float[] calculeTangente(int position){

        float numerateur = pointsCourbe[position-1][1] - pointsCourbe[position+1][1];
        float denominateur = pointsCourbe[position-1][0] - pointsCourbe[position+1][0];
        if (denominateur == 0){   // droite verticale x=cst
            return new float[] {pointsCourbe[position][0]};
        }
        float coeffDirecteur = numerateur / denominateur;
        float ordonneeOrigine = pointsCourbe[position-1][1] - coeffDirecteur * pointsCourbe[position-1][0];
        return new float[] {coeffDirecteur, ordonneeOrigine};
    }

    private float[] calculePerpendiculaire(int position, float[] tangente){

        if (tangente.length == 1){  // perpendiculaire horizontale
            return new float[] {pointsCourbe[position][1]};
        }

        if (tangente[0] == 0){  // tangente horizontale y=cst donc perpendiculaire verticale x=cst
            return new float[] {pointsCourbe[position][0]};
        }

        float coeffDirecteur = -1/tangente[0];
        float ordonneeOrigine = pointsCourbe[position][1] - coeffDirecteur * pointsCourbe[position][0];
        return new float[] {coeffDirecteur, ordonneeOrigine};
    }

    private void remplissage(){
        float[] inter = intersection();

        if (perpendiculaireCourante.length == 0 ){
            return;
        }
        else{
            if (perpendiculairePrecedente.length == 0){
                remplirDebut();
            }
        }

        String sens = getSens();
        int compteur = 0;
        if (sens.equals("NS")){
            while (compteur < pictureHeight){
                rempLigneHor(compteur);
                compteur ++;
            }
        }
        else {
            while (compteur < pictureWidth){
                rempLigneVert(compteur);
                compteur ++;
            }
        }
    }

    private float f2x(float[] droite, float x){
        return droite[0] * x + droite[1];
    }

    private float f2y(float[] droite, float y){
        return (y - droite[1]) / droite[0];
    }

    private void rempLigneVert(int i){
        float x1 = f2y(perpendiculaireCourante, i);
        float x2 = f2y(perpendiculairePrecedente, i);
        System.out.println("rempli droite ("+x1+","+i+") ("+x2+","+i+").");
    }

    private void rempLigneHor(int i){
        float y1 = f2x(perpendiculaireCourante, i);
        float y2 = f2x(perpendiculairePrecedente, i);
        System.out.println("rempli droite ("+i+","+y1+") ("+i+","+y2+").");
    }

    private void remplirDebut(){
        if (pointsCourbe[0][0] < pointsCourbe[pointsCourbe.length-1][0]){
            int compteur = 1;
            while (compteur < f2x(perpendiculaireCourante, 0) && compteur < pictureHeight){
                System.out.println("rempli droite ("+0+","+compteur+") ("+f2y(perpendiculaireCourante, compteur)+","+compteur+").");
                compteur ++;
            }
        }
        else{
            if (pointsCourbe[0][0] > pointsCourbe[pointsCourbe.length-1][0]){
                int compteur = 1;
                while (compteur < f2x(perpendiculaireCourante, pictureWidth) && compteur < pictureWidth){
                    System.out.println("rempli droite ("+pictureWidth+","+compteur+") ("+f2y(perpendiculaireCourante, compteur)+","+compteur+").");
                    compteur ++;
                }
            }
        }
    }

    private float[] intersection(){
        float x = (perpendiculaireCourante[1] - perpendiculairePrecedente[1]) / (perpendiculairePrecedente[0] - perpendiculaireCourante[0]);
        float y = f2x(perpendiculaireCourante, x);
        return new float[]{x, y};
    }

    private float[] getPoints(float[] droite){
        float y0 = f2x(droite, 0);
        float x0 = f2y(droite, 0);
        float xHeight = f2y(droite, pictureHeight);

        float x1 = (y0 < pictureHeight && y0 > 0) ? 0 : ( (x0 < xHeight) ? x0 : xHeight);
        float y1 = f2x(droite, x1);

        return new float[]{x1, y1};
    }

    private String getSens(){
        float[] ptCourante = getPoints(perpendiculaireCourante);
        float[] ptPrecedente = getPoints(perpendiculairePrecedente);
        float deltaX = abs(ptCourante[0] - ptPrecedente[0]);
        float deltaY = abs(ptCourante[1] - ptPrecedente[1]);
        return (deltaX > deltaY) ? "NS" : "EW";
    }
}
