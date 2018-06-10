package fr.univtln.group3.anamorphosisandroid.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.univtln.group3.anamorphosisandroid.R;
import fr.univtln.group3.anamorphosisandroid.utility.Utils;

/**
 * Step1Activity handles the home screen
 */
public class Step1Activity extends AppCompatActivity {

    /*********************************
     * STATIC MEMBERS INITIALISATION *
     *********************************/
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int LOAD_VIDEO_GALLERY_ACTIVITY_REQUEST_CODE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /******************************
     * BINDVIEWS with Butterknife *
     ******************************/
    @BindView(R.id.txtViewStep1)
    TextView txtViewStep1;

    /**
     * OnCreate method when the activity is created
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step1);
        ButterKnife.bind(this);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/LuckiestGuy-Regular.ttf"); // create a typeface from the raw ttf
        txtViewStep1.setTypeface(typeface);

        TextView toolbarText = findViewById(R.id.toolbar_text);
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbarText != null && toolbar != null) {
            toolbarText.setText(getTitle());
            toolbarText.setTypeface(typeface);
            setSupportActionBar(toolbar);
        }

    }

    /**
     * onActivityResult to handle process when we come from the gallery activity
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOAD_VIDEO_GALLERY_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedVideoUri = data.getData();
            String selectedVideoPath = Utils.getPath(this, selectedVideoUri);
            System.out.println(selectedVideoPath);
            if (selectedVideoPath != null) {
                Intent intent = new Intent(getApplicationContext(), Step2Activity.class);
                intent.putExtra("selectedVideoPath", selectedVideoPath);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
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
        verifyStoragePermissions(Step1Activity.this);
        Intent galleryIntent = new Intent();
        galleryIntent.setType("video/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryIntent, LOAD_VIDEO_GALLERY_ACTIVITY_REQUEST_CODE);
    }

    /**
     * Runs when the camera button is clicked from the bottom menu
     * Starts the camera activity
     */
    @OnClick(R.id.btnLoadCamera)
    public void onCameraButtonClicked() {
        Intent cameraIntent = new Intent(getApplicationContext(), VideoCaptureActivity.class);
        startActivity(cameraIntent);
        finish();
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
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
