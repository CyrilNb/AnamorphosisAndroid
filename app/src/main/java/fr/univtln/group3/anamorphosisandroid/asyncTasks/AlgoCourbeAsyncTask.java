package fr.univtln.group3.anamorphosisandroid.asyncTasks;


import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import fr.univtln.group3.anamorphosisandroid.AlgoCourbe;
import fr.univtln.group3.anamorphosisandroid.Utility.FrameExtractor;
import fr.univtln.group3.anamorphosisandroid.activities.ResultActivity;

public class AlgoCourbeAsyncTask extends AsyncTask<String, Bitmap, Void> {

    ImageView imageViewResult;
    ResultActivity caller;

    private List<float[]> floatsPointsList;
    private List<Point> pointList;
    private int canvasWidth;
    private int canvasHeight;

    public AlgoCourbeAsyncTask(ResultActivity caller, ImageView imageView, List<Point> pointList, int canvasHeight, int canvasWidth) {
        this.imageViewResult = imageView;
        this.caller = caller;
        this.pointList = pointList;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
    }

    public List<float[]> bezier(float[][] L, int n) {
        // L : 4 points de controle
        // n : nombre de points tracés
        float u = 0;
        List<float[]> l_points = new ArrayList<>();
        for (int i = 0; i < n + 1; i++) {
            float[] point = bezier_r(L, n, u);
            l_points.add(point);
            u += 1f / n;
        }
        return l_points;
    }


    private float[] bezier_r(float[][] L, int n, float u) {
        int N = L.length - 1;
        float[][] newL = new float[N][2];
        for (int i = 0; i < N; i++) {
            newL[i][0] = (L[i][0] * (1 - u) + L[i + 1][0] * u);
            newL[i][1] = (L[i][1] * (1 - u) + L[i + 1][1] * u);
        }
        if (newL.length != 1) {
            return bezier_r(newL, 1, u);
        } else {
            return newL[0];
        }
    }

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

        int largeur = frameExtractor.getWidth();
        int hauteur = frameExtractor.getHeight();
        float[][] L = {{50, 300}, {350, 300}, {650, 300}, {950, 300}};
        List<float[]> pointsCourbe = bezier(L, 100);

        List<float[]> pointsCourbe2 = new ArrayList<>();
        pointsCourbe2.add(new float[]{200f, 50f});
        pointsCourbe2.add(new float[]{(float) frameExtractor.getWidth() - 200, (float) frameExtractor.getHeight() - 50});

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
