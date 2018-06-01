package fr.univtln.group3.anamorphosisandroid;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.nio.IntBuffer;

import butterknife.BindView;
import fr.univtln.group3.anamorphosisandroid.Utility.FrameExtractor;
import fr.univtln.group3.anamorphosisandroid.Utility.PixelsExtractor;


public class TestMediaCodecAsync extends AsyncTask<String, Bitmap, Void> {

    ImageView imageViewResult;

    Button t_to_b_btn;

    public TestMediaCodecAsync(ImageView imageView,Button btn){
        imageViewResult = imageView;
        t_to_b_btn = btn;
    }


    @Override
    protected Void doInBackground(String... selectedVideoPath) {

        FrameExtractor frameExtractor = new FrameExtractor(selectedVideoPath[0]);

        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bitmapResult = Bitmap.createBitmap(frameExtractor.getWidth(),
                frameExtractor.getHeight(),
                conf);


        PixelsExtractor pixelsExtractor = new PixelsExtractor(bitmapResult,
                PixelsExtractor.Direction.DROITE_GAUCHE,
                frameExtractor.getWidth(),
                frameExtractor.getHeight(),
                frameExtractor.getNbFrames());

        Bitmap bitmapCurrent;
        Bitmap bitmapCurrentSave = null;
        while(!frameExtractor.isOutputDone()){
            bitmapCurrent = frameExtractor.getNextBitmap();
            if (bitmapCurrent!=null){
                bitmapCurrentSave = bitmapCurrent;
                pixelsExtractor.extractAndCopy(bitmapCurrent);
                publishProgress(bitmapResult);
            }
        }

        pixelsExtractor.combler(bitmapCurrentSave);

        publishProgress(bitmapResult);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        Log.d("onPostExecute", "in");

        t_to_b_btn.setEnabled(true);
    }

    @Override
    protected void onProgressUpdate(Bitmap... bitmap) {
        imageViewResult.setImageBitmap(bitmap[0]);
    }

}
