package fr.univtln.group3.anamorphosisandroid.asyncTasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;


/**
 * Created by Cyril Niobé on 10/06/2018.
 * <p>
 * Saves an image into intenal storage of the device in a background thread.
 * Does not block UI thread.
 */
public class SaveImageAsyncTask extends AsyncTask {

    /***********
     * MEMBERS *
     ***********/
    String fileName;
    Bitmap bitmap;
    View view;

    public SaveImageAsyncTask(View view, String fileName, Bitmap bitmap) {
        super();
        this.fileName = fileName;
        this.bitmap = bitmap;
        this.view = view;
    }

    @Override
    protected Void doInBackground(Object[] params) {
        OutputStream fOut = null;
        try {
            File root = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "AndroidAnamorphosis" + File.separator);
            root.mkdirs();
            File sdImageMainDirectory = new File(root, this.fileName + ".png");
            fOut = new FileOutputStream(sdImageMainDirectory);
        } catch (Exception e) {
            final Snackbar snackbar = Snackbar.make(view, "ERROR. TRY AGAIN", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            final Snackbar snackbar = Snackbar.make(view, "IMAGE SAVED", Snackbar.LENGTH_LONG);
            snackbar.setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
        } catch (Exception e) {
            Log.d("exception", e.getMessage());
            final Snackbar snackbar = Snackbar.make(view, "IMAGE NOT SAVED !", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        return null;
    }

}
