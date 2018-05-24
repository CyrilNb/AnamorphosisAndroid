package fr.univtln.group3.anamorphosisandroid;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    private static final int LOAD_VIDEO_GALLERY_ACTIVITY_REQUEST_CODE = 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @BindView(R.id.btnLoadGallery)
    Button btnLoadGallery;
    @BindView(R.id.imgViewResult)
    ImageView imageViewResult;

    List<Bitmap> listFrames;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        listFrames = new ArrayList<>();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOAD_VIDEO_GALLERY_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedVideoUri = data.getData();
            String selectedVideoPath = Utils.getPath(this, selectedVideoUri);
            System.out.println(selectedVideoPath);
            if (selectedVideoPath != null) {
                System.out.println("*-*-*-*-*-DEBUT-*-*-*-*-*");
                new TestMediaCodecAsync(imageViewResult).execute(selectedVideoPath);
                System.out.println("*-*-*-*-*- FIN -*-*-*-*-*");
            } else {
                System.out.println("video path is null");
            }
        }

    }



    /**
     * Runs when the gallery button is clicked from the bottom menu
     * Performs the load of an image from the gallery
     */
    @OnClick(R.id.btnLoadGallery)
    public void onLoadFromGalleryButtonClicked() {
        verifyStoragePermissions(MainActivity.this);
        Intent galleryIntent = new Intent();
        galleryIntent.setType("video/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryIntent, LOAD_VIDEO_GALLERY_ACTIVITY_REQUEST_CODE);

    }

    /**
     * If APK >= 23, we need to check at runtime for user permissions
     * Checks if the app has permission to write to device storage
     * If the app does not has permissions required then the user will be prompted to grant permissions
     *
     * @param activity which performs the operation where permissions are requested
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if the application has write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // If not, prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


}