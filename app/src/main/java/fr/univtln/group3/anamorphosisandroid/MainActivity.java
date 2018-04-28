package fr.univtln.group3.anamorphosisandroid;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadata;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wseemann.media.FFmpegMediaMetadataRetriever;

public class MainActivity extends AppCompatActivity {

    private static final int LOAD_VIDEO_GALLERY_ACTIVITY_REQUEST_CODE = 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @BindView(R.id.btnLoadGallery) Button btnLoadGallery;
    @BindView(R.id.scrollView) HorizontalScrollView scrollView;
    @BindView(R.id.linearlayoutFrames) LinearLayout linearlayoutFrames;

    static List<Bitmap> listFrames;

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
            String selectedVideoPath = Utils.getPath(this,selectedVideoUri);
            System.out.println(selectedVideoPath);
            if (selectedVideoPath != null) {
                final FFmpegMediaMetadataRetriever mediaMetadataRetriever = new FFmpegMediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(selectedVideoPath);

                String mVideoDuration =  mediaMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION);

                int framerate = (int) Double.parseDouble(mediaMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_FRAMERATE));

                int intervalRefresh = (1000000/framerate)*2;

                int currentTime = 0;

                int timeInMilliSeconds = Integer.parseInt(mVideoDuration);
                int timeInMicroSeconds = timeInMilliSeconds * 1000;

                while(currentTime < timeInMicroSeconds){
                    final int time = currentTime;
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            System.out.println("time: "+time);
                            Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(time);
                            addBitmapToList(bitmap);
                        }
                    };
                    thread.start();
                    currentTime+=intervalRefresh;
                }

                try {
                    Thread.sleep(46000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("nb frames: "+listFrames.size());
                mediaMetadataRetriever.release();
            }

        }
    }

        /**
         * Runs when the gallery button is clicked from the bottom menu
         * Performs the load of an image from the gallery
         */
        @OnClick(R.id.btnLoadGallery) public void onLoadFromGalleryButtonClicked (){
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
        public static void verifyStoragePermissions (Activity activity){
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

        public static synchronized void addBitmapToList(Bitmap frame){
            listFrames.add(frame);
        }

}
