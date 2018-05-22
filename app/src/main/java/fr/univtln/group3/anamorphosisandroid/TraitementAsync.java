package fr.univtln.group3.anamorphosisandroid;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;

public class TraitementAsync extends AsyncTask<String, Bitmap, Bitmap> {


    Bitmap result;
    int compteur_numColonneStart;
    int compteur_numLigneStart;
    ImageView imageViewResult;
    int recup1, recup2;
    int c;

    public TraitementAsync(ImageView imageView){
        imageViewResult = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... selectedVideoPath) {
        compteur_numColonneStart = 0; //cpt_h

        compteur_numLigneStart = 0; //cpt_h

        FrameGrabber frameGrabber = new FFmpegFrameGrabber(selectedVideoPath[0]);

        recup1 = 0;
        recup2 = 0;

        int largeur = 0;
        int hauteur = 0;

        try {
            frameGrabber.start();
            largeur = frameGrabber.getImageWidth();
            hauteur = frameGrabber.getImageHeight();

            if (largeur != 0 | hauteur != 0) {
                System.out.println("largeur: " + largeur);
                System.out.println("hauteur: " + hauteur);
                Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
                result = Bitmap.createBitmap(largeur, hauteur, conf); // this creates a MUTABLE bitmap
            } else {
                System.out.println("ERROR: largeur and hauteur = 0 from FrameGrabber");
            }

        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }

        frameGrabber.setFormat("mp4");

        Frame frame = null;

        int nbFrames = frameGrabber.getLengthInFrames();
        System.out.println("nbFrames: " + nbFrames);

        int facteurLargeurEtNbFrames = largeur / nbFrames; //s1
//        float reste = (float) nbFrames / ((float) nbFrames - ((float) largeur % (float) nbFrames));

        int facteurHauteurEtNbFrames = hauteur / nbFrames; //s1
//        float reste = (float) nbFrames / ((float) nbFrames - ((float) hauteur % (float) nbFrames));
        float reste = (float) nbFrames / ((float) hauteur % (float) nbFrames);

        System.out.println("facteurLargeurEtNbFrames: " + facteurLargeurEtNbFrames);
        System.out.println("facteurHauteurEtNbFrames: " + facteurHauteurEtNbFrames);
        System.out.println("reste: " + reste);

        float compteurDuReste = 0; //cpt_s2
        int compteurNombreDeFrames = 0; //cpt
        int countImagesRecupereGraceAuReste = 0; //cpt_img

        c = 0;
        while (compteurNombreDeFrames < nbFrames) {
            System.out.println("debut grab");
            frame = getFrame(frameGrabber);
            System.out.println("fin grab");
            if (frame != null) {
//                System.out.println("frame image not null");
////                convertAndExecLargeur(frame, facteurLargeurEtNbFrames, hauteur, largeur);
//                convertAndExecHauteur(frame, facteurHauteurEtNbFrames, hauteur, largeur);
//
//                if (compteurNombreDeFrames < compteurDuReste) {
//                    countImagesRecupereGraceAuReste++;
////                    convertAndExecLargeur(frame, 1, hauteur, largeur);
//                      convertAndExecHauteur(frame, 1, hauteur, largeur);
//                } else {
//                    compteurDuReste += reste;
//                }
                compteurNombreDeFrames++;
                System.out.println("c: " + c);
//                publishProgress(result);
//                System.out.println("frame not null fin");
            }
        }

        publishProgress(result);
        System.out.println("countImagesRecupereGraceAuReste: " + countImagesRecupereGraceAuReste);
        System.out.println("CEST FINIIIIIIIIIIII");
     return result;
    }


    private Frame getFrame(FrameGrabber frameGrabber) {
        Frame frame = null;
        try {
            frame = frameGrabber.grabFrame();
            System.out.println(frameGrabber.getFrameNumber());
            System.out.println(frameGrabber.getLengthInFrames());
            if (frame != null) {
                if (frame.image != null) {
                    c++;
                    return frame;
                }
            }
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void convertAndExecLargeur(Frame frame, int facteurLargeurEtNbFrames, int hauteur, int largeur) {
        AndroidFrameConverter androidFrameConverter = new AndroidFrameConverter();
        Bitmap bitmap = androidFrameConverter.convert(frame);
        gaucheVersDroite(bitmap, compteur_numColonneStart, facteurLargeurEtNbFrames, hauteur, largeur);
        compteur_numColonneStart += facteurLargeurEtNbFrames;
    }

    private void convertAndExecHauteur(Frame frame, int facteurHauteurNbFrames, int hauteur, int largeur) {
        AndroidFrameConverter androidFrameConverter = new AndroidFrameConverter();
        Bitmap bitmap = androidFrameConverter.convert(frame);
        hautVersBas(bitmap, compteur_numLigneStart, facteurHauteurNbFrames, hauteur, largeur);
        compteur_numLigneStart += facteurHauteurNbFrames;
    }

    private void gaucheVersDroite(Bitmap sourceFrame, int numColonneStart, int nombreDeColonnesAPRENDRE, int hauteur, int largeur) {
        System.out.println("debut gauche vers droite");
        int[] pixelsSourceFrame = new int[largeur * hauteur];
        sourceFrame.getPixels(pixelsSourceFrame, 0, largeur, 0, 0, largeur, hauteur);


        int[] pixelsResult = new int[largeur * hauteur];
        result.getPixels(pixelsResult,0,largeur,0,0,largeur,hauteur);

        for (int j = numColonneStart; j < nombreDeColonnesAPRENDRE + numColonneStart; j++) {
            for (int i = 0; i < hauteur; i++) { // pour chaque element dans la ligne
                if(i ==0 ){
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


    private void hautVersBas(Bitmap sourceFrame, int numLigneStart, int nombreDeLigneAPRENDRE, int hauteur, int largeur) {
        System.out.println("debut haut vers bas");
        int[] pixelsSourceFrame = new int[largeur * hauteur];
        sourceFrame.getPixels(pixelsSourceFrame, 0, largeur, 0, 0, largeur, hauteur);


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


    @Override
    protected void onProgressUpdate(Bitmap... bitmap) {
        imageViewResult.setImageBitmap(bitmap[0]);
    }
}
