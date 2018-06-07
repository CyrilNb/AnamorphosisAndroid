package fr.univtln.group3.anamorphosisandroid;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import fr.univtln.group3.anamorphosisandroid.Utility.FrameExtractor;
import fr.univtln.group3.anamorphosisandroid.Utility.PixelsExtractor;
import fr.univtln.group3.anamorphosisandroid.activities.ResultActivity;


public class TestMediaCodecAsync extends AsyncTask<String, Bitmap, Void> {

    ImageView imageViewResult;
    ResultActivity caller;
    Utils.Direction direction;

    public TestMediaCodecAsync(ResultActivity caller, ImageView imageView, Utils.Direction direction){
        this.imageViewResult = imageView;
        this.caller = caller;
        this.direction = direction;

    }

    @Override
    protected Void doInBackground(String... selectedVideoPath) {

        FrameExtractor frameExtractor = new FrameExtractor(selectedVideoPath[0]);

        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bitmapResult = Bitmap.createBitmap(frameExtractor.getWidth(),
                frameExtractor.getHeight(),
                conf);


        PixelsExtractor pixelsExtractor = new PixelsExtractor(bitmapResult,
                direction,
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

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        this.caller.getBtnDownload().setVisibility(View.VISIBLE);
    }

}
