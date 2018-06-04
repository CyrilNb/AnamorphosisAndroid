package fr.univtln.group3.anamorphosisandroid;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.ImageView;


/**
 * Cette AsyncTask a servi de test
 * et donc les opérations efféctuées ici pourront se faire dans l'AsyncTask principale
 */
public class TestAntiAliasFilter extends AsyncTask<Void,Bitmap,Void> {

    ImageView imageViewResult;
    //Canvas canvas;
    Button btn;


    public TestAntiAliasFilter(ImageView imageView, Button btn){
        imageViewResult = imageView;
        this.btn = btn;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        /**
         * Récupère la BitmapDrawable qui est dans l'ImageView
         */
        Bitmap bitmapResult = ((BitmapDrawable) imageViewResult.getDrawable()).getBitmap();


        /**
         * Ici on peut choisir quel algo d'anti-aliasing on veut appliquer à l'image (Bitmap)
         */

        bitmapResult = ConvolutionFilter.ConvoFilterMethod.gaussianBlurFilter(bitmapResult);


        /**
         * On rafraîchit l'ImageView avec la même image qui a été modifié
         */
        publishProgress(bitmapResult);


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        btn.setEnabled(false);
    }

    @Override
    protected void onProgressUpdate(Bitmap... bitmap) {
        imageViewResult.setImageBitmap(bitmap[0]);
    }
}
