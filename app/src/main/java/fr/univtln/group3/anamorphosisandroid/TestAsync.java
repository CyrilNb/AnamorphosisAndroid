package fr.univtln.group3.anamorphosisandroid;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import wseemann.media.FFmpegMediaMetadataRetriever;

import static java.lang.System.exit;

public class TestAsync extends AsyncTask<String, Void, Void> {

    Bitmap result;
    int compteur_numColonneStart;
    int compteur_numLigneStart;
    ImageView imageViewResult;
    int recup1, recup2;
    int c;

    public TestAsync(ImageView imageView){
        imageViewResult = imageView;
    }

    @Override
    protected Void doInBackground(String... objects) {
        // FFmpeg
        FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();
        retriever.setDataSource(objects[0]);

        // instanciation variables
        compteur_numColonneStart = 0; //cpt_h

        compteur_numLigneStart = 0; //cpt_h

        recup1 = 0;
        recup2 = 0;

        int largeur = 0;
        int hauteur = 0;

        largeur = Integer.getInteger(retriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        hauteur = Integer.getInteger(retriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));

        if (largeur != 0 | hauteur != 0) {
            System.out.println("largeur: " + largeur);
            System.out.println("hauteur: " + hauteur);
            Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
            result = Bitmap.createBitmap(largeur, hauteur, conf); // this creates a MUTABLE bitmap
        } else {
            System.out.println("ERROR: largeur and hauteur = 0 from FrameGrabber");
            exit(1);
        }


        int duration = Integer.getInteger(retriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION));
        int framerate = Integer.getInteger(retriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_FRAMERATE));
        int nbFrames = duration * framerate;
        System.out.println("duation: " + duration);
        System.out.println("framerate: " + framerate);
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
}
