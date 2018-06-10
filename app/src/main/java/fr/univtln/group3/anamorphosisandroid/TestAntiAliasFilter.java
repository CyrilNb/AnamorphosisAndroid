package fr.univtln.group3.anamorphosisandroid;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.ImageView;


/**
 * This AsyncTask is a test class
 * and so its conducted operations can directly be done in the Activity main AsyncTask
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
         * Get the BitmapDrawable contained by the ImageView
         */
        Bitmap bitmapResult = ((BitmapDrawable) imageViewResult.getDrawable()).getBitmap();


        /**
         * Here we can choose which of the antialiasing algorithm we want to apply on the image (Bitmap)
         */

        bitmapResult = ConvolutionFilter.ConvoFilterMethod.gaussianBlurFilter(bitmapResult);


        /**
         * We refresh the ImageView with the same image which get edited
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
