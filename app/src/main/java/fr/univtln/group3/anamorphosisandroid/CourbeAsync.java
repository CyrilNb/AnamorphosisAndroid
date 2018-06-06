package fr.univtln.group3.anamorphosisandroid;


import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import fr.univtln.group3.anamorphosisandroid.Utility.FrameExtractor;

public class CourbeAsync extends AsyncTask<String, Bitmap, Void> {

    ImageView imageViewResult;


    public CourbeAsync(ImageView imageView){
        imageViewResult = imageView;
    }

    public List<float[]> bezier(float[][] L, int n) {
        // L : 4 points de controle
        // n : nombre de points tracés
        float u = 0;
        List<float[]> l_points = new ArrayList<>();
        for (int i=0; i<n+1; i++){
            float[] point = bezier_r(L, n, u);
            l_points.add(point);
            u += 1f / n;
        }
        return l_points;
    }


    private float[] bezier_r(float[][] L, int n, float u) {
        int N = L.length - 1;
        float[][] newL = new float[N][2];
        for (int i=0; i<N; i++) {
            newL[i][0] = (L[i][0] * (1 - u) + L[i + 1][0] * u);
            newL[i][1] = (L[i][1] * (1 - u) + L[i + 1][1] * u);
        }
        if (newL.length!= 1){
            return bezier_r(newL, 1, u);
        }
        else{
            return newL[0];
        }
    }

    @Override
    protected Void doInBackground(String... selectedVideoPath) {

        FrameExtractor frameExtractor = new FrameExtractor(selectedVideoPath[0]);

        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bitmapResult = Bitmap.createBitmap(frameExtractor.getWidth(),
                frameExtractor.getHeight(),
                conf);

        int largeur = frameExtractor.getWidth();
        int hauteur = frameExtractor.getHeight();
        float[][] L = {{0,0}, {0,hauteur}, {largeur,hauteur}, {largeur,0}};
        List<float[]> pointsCourbe = bezier(L, 230);

//        for (float[] i: pointsCourbe
//             ) {
//            System.out.println("i : " + i[0] + " " + i[1]);
//        }

        AlgoCourbe algoCourbe = new AlgoCourbe(bitmapResult, pointsCourbe, AlgoCourbe.CONTRAINTE.NE,
                frameExtractor.getNbFrames(), frameExtractor.getHeight(), frameExtractor.getWidth());

        Bitmap bitmapCurrent;
        Bitmap bitmapCurrentSave = null;
        while(!frameExtractor.isOutputDone()){
            bitmapCurrent = frameExtractor.getNextBitmap();
            if (bitmapCurrent!=null){
                bitmapCurrentSave = bitmapCurrent;
                algoCourbe.extractAndCopy(bitmapCurrent);
                publishProgress(bitmapResult);
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
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

}
