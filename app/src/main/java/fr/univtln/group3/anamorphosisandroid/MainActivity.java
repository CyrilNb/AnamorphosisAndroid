package fr.univtln.group3.anamorphosisandroid;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOAD_VIDEO_GALLERY_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedVideoUri = data.getData();
            String selectedVideoPath = Utils.getPath(this,selectedVideoUri);
            if (selectedVideoPath != null) {
                FFmpegMediaMetadataRetriever mediaMetadataRetriever = new FFmpegMediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(selectedVideoPath);

                //GET VIDEO DURATION IN SEVERAL UNITE
                String mVideoDuration =  mediaMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION);
                long timeInMilliSeconds = Long.parseLong(mVideoDuration);
                long timeInMicroSeconds = timeInMilliSeconds * 1000;
                long duration = timeInMilliSeconds / 1000;
                long hours = duration / 3600;
                long minutes = (duration - hours * 3600) / 60;
                long seconds = duration - (hours * 3600 + minutes * 60);

                System.out.println("duration: "+seconds+" seconds");
                for(int i=0; i< timeInMicroSeconds; i+=1000000){
                    System.out.println(i);
                    Bitmap frame = mediaMetadataRetriever.getFrameAtTime(i, FFmpegMediaMetadataRetriever.OPTION_CLOSEST); // frame at 1 seconds
                    //listFrames.add(frame);

                    ImageView iv = new ImageView(getApplicationContext());
                    iv.setImageBitmap(frame);
                    linearlayoutFrames.addView(iv);

                }
                System.out.println("nb list: "+listFrames.size());
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

}
