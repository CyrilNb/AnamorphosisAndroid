package fr.univtln.group3.anamorphosisandroid;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;


public class MainActivity extends Activity {
    private final int REQUEST_CODE = 100;
    private final String[] STORAGE_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);

        int writePermission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED ||
                readPermission != PackageManager.PERMISSION_GRANTED){
            this.requestPermissions(STORAGE_PERMISSIONS, REQUEST_CODE);
        }
        else{
            captureLaunch();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE && grantResults.length == 2){

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED){

                Toast.makeText(MainActivity.this, "Permission granted !!", Toast.LENGTH_SHORT).show();
                captureLaunch();

            }
        }

        else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            this.finish();
        }
    }

    private void captureLaunch(){

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        initSave(intent);

        this.startActivityForResult(intent, REQUEST_CODE);
    }

    private void initSave(Intent intent) {

        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        File videoFile = new File(dir.getAbsolutePath()+ File.separator +"ANA_" + System.currentTimeMillis() + ".mp4");

        Uri videoUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, videoFile);


        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
    }
}