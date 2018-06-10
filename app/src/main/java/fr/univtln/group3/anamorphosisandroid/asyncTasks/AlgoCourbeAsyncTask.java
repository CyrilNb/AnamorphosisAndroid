package fr.univtln.group3.anamorphosisandroid.asyncTasks;


import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import fr.univtln.group3.anamorphosisandroid.AlgoCourbe;
import fr.univtln.group3.anamorphosisandroid.utility.FrameExtractor;
import fr.univtln.group3.anamorphosisandroid.activities.ResultActivity;

/**
 * Start the custom anamorphosis in background.
 */
public class AlgoCourbeAsyncTask extends AsyncTask<String, Bitmap, Void> {

    ImageView imageViewResult;
    ResultActivity caller;

    private List<float[]> floatsPointsList;
    private List<Point> pointList;
    private int canvasWidth;
    private int canvasHeight;

    /**
     * CONSTRUCTOR OF ALGOCOURBEASYNCTASK
     *
     * @param caller
     * @param imageView
     * @param pointList
     * @param canvasHeight
     * @param canvasWidth
     */
    public AlgoCourbeAsyncTask(ResultActivity caller, ImageView imageView, List<Point> pointList, int canvasHeight, int canvasWidth) {
        this.imageViewResult = imageView;
        this.caller = caller;
        this.pointList = pointList;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
    }

    /**
     * Method that is done in background.
     *
     * @param selectedVideoPath
     * @return
     */
    @Override
    protected Void doInBackground(String... selectedVideoPath) {

        FrameExtractor frameExtractor = new FrameExtractor(selectedVideoPath[0]);

        floatsPointsList = new ArrayList<>();
        for (Point p : pointList) {
            floatsPointsList.add(new float[]{p.x, frameExtractor.getHeight() - 1 - (p.y)});
        }

        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bitmapResult = Bitmap.createBitmap(frameExtractor.getWidth(),
                frameExtractor.getHeight(),
                conf);

        AlgoCourbe algoCourbe = new AlgoCourbe(bitmapResult, floatsPointsList,
                frameExtractor.getNbFrames(), frameExtractor.getHeight(), frameExtractor.getWidth(),
                canvasHeight, canvasWidth);

        Bitmap bitmapCurrent;
        Bitmap bitmapCurrentSave = null;
        while (!frameExtractor.isOutputDone()) {
            bitmapCurrent = frameExtractor.getNextBitmap();
            if (bitmapCurrent != null) {
                bitmapCurrentSave = bitmapCurrent;
                algoCourbe.extractAndCopy(bitmapCurrent);
                publishProgress(bitmapResult);
            }
        }

        algoCourbe.combler(bitmapCurrentSave);
        publishProgress(bitmapResult);

        return null;
    }


    /**
     * Called while the other method works in background.
     * @param bitmap
     */
    @Override
    protected void onProgressUpdate(Bitmap... bitmap) {
        imageViewResult.setImageBitmap(bitmap[0]);
    }

    /**
     * Called once the one working in background is done.
     *
     * @param aVoid
     */
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        this.caller.getBtnDownload().setVisibility(View.VISIBLE);
        this.caller.getBtnShare().setVisibility(View.VISIBLE);
    }
}
