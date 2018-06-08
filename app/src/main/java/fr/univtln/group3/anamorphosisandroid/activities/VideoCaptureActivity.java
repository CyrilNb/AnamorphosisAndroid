package fr.univtln.group3.anamorphosisandroid.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import fr.univtln.group3.anamorphosisandroid.R;

/**
 * VideoCaptureActivity handles the shooting of a new video inside the application
 */
public class VideoCaptureActivity extends Activity {

    /***********
     * MEMBERS *
     ***********/
    public static String newVideoPath;

    private final int REQUEST_CODE = 100;
    private final String[] STORAGE_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    /**
     * OnCreate called when the activity is being created
     *
     * @param savedInstanceBundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        newVideoPath = null;
        int writePermission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED ||
                readPermission != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(STORAGE_PERMISSIONS, REQUEST_CODE);
        } else {
            captureLaunch();
        }

    }

    /**
     * Handles the result of requesting needed permissions
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE && grantResults.length == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                captureLaunch();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            this.finish();
        }
    }

    /**
     * Launches Camera intent
     */
    private void captureLaunch() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * Handles the result of the camera intent
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            System.out.println(resultCode);
            if (data.getData() != null) {
                Uri videoUri = data.getData();
                Intent step2Intent = new Intent(getApplicationContext(), Step2Activity.class);
                step2Intent.putExtra("cameraVideoPath", getRealPathFromURI(videoUri));
                startActivity(step2Intent);
                finish();
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
            }
        }
    }

    /**
     * Method to convert Uri into Path string
     *
     * @param contentUri uri to be converted
     * @return path
     */
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
