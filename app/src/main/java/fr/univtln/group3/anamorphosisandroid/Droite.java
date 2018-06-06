package fr.univtln.group3.anamorphosisandroid;


import android.util.Log;

public class Droite {
    private String TAG = "AlgoCourbe";
    private Float coeffDirecteur = null;
    private Float ordOrigine = null;
    private Float xcst = null;

    public Float getCoeffDirecteur() {
        return coeffDirecteur;
    }

    public Float getOrdOrigine() {
        return ordOrigine;
    }

    public Float getXcst() {
        return xcst;
    }


    public float f2x(float x){
        return coeffDirecteur * x + ordOrigine;
    }

    public float f2y(float y){
        if (coeffDirecteur == 0) System.out.println("PROBBLEME DANS F2Y");;
        return (y - ordOrigine) / coeffDirecteur;
    }


    public float[] intersection(Droite droite){
        if (xcst != null){
            return new float[] {xcst, droite.f2x(xcst)};
        }
        else if (droite.getXcst() != null){
            return new float[] {droite.getXcst(), f2x(droite.getXcst())};
        }
        float x = (droite.getOrdOrigine() - ordOrigine) / (coeffDirecteur - droite.getCoeffDirecteur());
        float y = f2x(x);
        return new float[]{x, y};
    }

    public String isParallel(Droite droite){
        if (xcst != null && droite.getXcst() != null){
            return "PAV";
        }
        else if (xcst == null && droite.getXcst() == null){
            if (coeffDirecteur == droite.getCoeffDirecteur()) return "PA";
            else return "NO";
        }
        else{
            return "NO";
        }
    }

    public Droite(Float coeffDirecteur, Float ordOrigine, Float xcst) {
        this.coeffDirecteur = coeffDirecteur;
        this.ordOrigine = ordOrigine;
        this.xcst = xcst;
    }
}
