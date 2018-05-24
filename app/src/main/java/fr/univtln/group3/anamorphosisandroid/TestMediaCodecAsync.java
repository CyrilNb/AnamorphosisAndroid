package fr.univtln.group3.anamorphosisandroid;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import fr.univtln.group3.anamorphosisandroid.Utility.FrameExtractor;
import fr.univtln.group3.anamorphosisandroid.Utility.PixelsExtractor;

//import org.bytedeco.javacv.FFmpegFrameGrabber;
//import org.bytedeco.javacv.FrameGrabber;

public class TestMediaCodecAsync extends AsyncTask<String, Bitmap, Void> {

    ImageView imageViewResult;
    public TestMediaCodecAsync(ImageView imageView){
        imageViewResult = imageView;
    }

    @Override
    protected Void doInBackground(String... selectedVideoPath) {

        FrameExtractor frameExtractor = new FrameExtractor(selectedVideoPath[0]);

        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bitmapResult = Bitmap.createBitmap(frameExtractor.getWidth(),
                frameExtractor.getHeight(),
                conf);


        PixelsExtractor pixelsExtractor = new PixelsExtractor(bitmapResult,
                PixelsExtractor.Direction.GAUCHE_DROITE,
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
    protected void onProgressUpdate(Bitmap... bitmap) {
        imageViewResult.setImageBitmap(bitmap[0]);
    }

}
