package fr.univtln.group3.anamorphosisandroid;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * Created by Cyril Niobé on 27/04/2018.
 */
public class AsyncTaskExtractFrame extends AsyncTask<String,Integer,Bitmap> {

    @Override
    protected Bitmap doInBackground(String... params) {
        FFmpegMediaMetadataRetriever mediaMetadataRetriever = new FFmpegMediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(params[0]);
        Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(Integer.parseInt(params[1]));
        //MainActivity.addBitmapToList(bitmap);
        mediaMetadataRetriever.release();
        return bitmap;
    }

}
