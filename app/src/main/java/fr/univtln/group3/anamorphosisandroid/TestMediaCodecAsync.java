package fr.univtln.group3.anamorphosisandroid;

import android.graphics.Bitmap;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TestMediaCodecAsync extends AsyncTask<String, Bitmap, Bitmap> {

    Bitmap result;
    int compteur_numColonneStart;
    int compteur_numLigneStart;
    ImageView imageViewResult;
    int recup1, recup2;
    int c;

    public TestMediaCodecAsync(ImageView imageView){
        imageViewResult = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... selectedVideoPath) {
        ExtractMpegFrames extractMpegFrames = new ExtractMpegFrames(selectedVideoPath[0]);
        try {
            extractMpegFrames.extractMpegFrames();
        } catch (IOException e) {
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
