package fr.univtln.group3.anamorphosisandroid.activities;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.univtln.group3.anamorphosisandroid.R;
import fr.univtln.group3.anamorphosisandroid.asyncTasks.AlgoClassicAsyncTask;
import fr.univtln.group3.anamorphosisandroid.Utility.Utils;
import fr.univtln.group3.anamorphosisandroid.asyncTasks.AlgoCourbeAsyncTask;
import fr.univtln.group3.anamorphosisandroid.asyncTasks.SaveImageAsyncTask;

import static java.lang.System.lineSeparator;
import static java.lang.System.out;

/**
 * ResultActivity handles the result screen where AsyncTasks are performing
 */
public class ResultActivity extends AppCompatActivity {

    /******************************
     * BINDVIEWS with Butterknife *
     ******************************/
    @BindView(R.id.rootLinearLayoutResult)
    LinearLayout rootLinearLayoutResult;
    @BindView(R.id.txtViewStep3)
    TextView textViewStep3;
    @BindView(R.id.imgViewResult)
    ImageView imgViewResult;
    @BindView(R.id.btnDownload)
    Button btnDownload;
    @BindView(R.id.btnHome)
    Button btnHome;
    @BindView(R.id.btnShare)
    Button btnShare;

    /***********
     * MEMBERS *
     ***********/
    String videoPath;
    AlgoClassicAsyncTask classicAsyncTask;
    AlgoCourbeAsyncTask courbeAsyncTask;

    /**
     * OnCreate method called when the activity is being created
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/LuckiestGuy-Regular.ttf"); // create a typeface from the raw ttf
        textViewStep3.setTypeface(typeface);
        TextView toolbarText = findViewById(R.id.toolbar_text);
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbarText != null && toolbar != null) {
            toolbarText.setText(getTitle());
            toolbarText.setTypeface(typeface);
            setSupportActionBar(toolbar);
        }

        int canvasWidth;
        int canvasHeight;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            videoPath = extras.getString("selectedVideoPath");
            if (videoPath == null) {
                videoPath = extras.getString("cameraVideoPath");
            }
            canvasWidth = extras.getInt("canvasWidth");
            canvasHeight = extras.getInt("canvasHeight");

            boolean isCustom = extras.getBoolean("isCustom");

            if (isCustom) {
                ArrayList<Point> pointList = (ArrayList<Point>) extras.getSerializable("pointsList");
                if (pointList != null) {
                    out.println("size: " + pointList.size());
                    courbeAsyncTask = new AlgoCourbeAsyncTask(this, imgViewResult, pointList, canvasHeight, canvasWidth);
                    courbeAsyncTask.execute(videoPath);
                }
            } else {
                String directionString = extras.getString("direction");
                Utils.Direction direction = null;
                if (directionString != null) {
                    if (directionString.equals("up")) {
                        direction = Utils.Direction.UP;
                    }
                    if (directionString.equals("down")) {
                        direction = Utils.Direction.DOWN;
                    }
                    if (directionString.equals("right")) {
                        direction = Utils.Direction.RIGHT;
                    }
                    if (directionString.equals("left")) {
                        direction = Utils.Direction.LEFT;
                    }
                }
                classicAsyncTask = new AlgoClassicAsyncTask(this, imgViewResult, direction);
                classicAsyncTask.execute(videoPath);
            }

        }

    }

    /**
     * Downloads the result image to the intern storage of the phone.
     */
    @OnClick(R.id.btnDownload)
    public void downloadResultImage() {
        saveImage(((BitmapDrawable) imgViewResult.getDrawable()).getBitmap());
    }

    /**
     * Goes back to Home Screen
     * Cancels running AsyncTask if any
     */
    @OnClick(R.id.btnHome)
    public void goHome() {
        if (classicAsyncTask != null) {
            classicAsyncTask.cancel(true);
        }
        if (courbeAsyncTask != null) {
            courbeAsyncTask.cancel(true);
        }
        Intent intent = new Intent(getApplicationContext(), Step1Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }


    /**
     * Get Back to previous activity on back button pressed
     * Cancels running AsyncTask if any
     */
    @Override
    public void onBackPressed() {
        if (classicAsyncTask != null) {
            classicAsyncTask.cancel(true);
        }
        if (courbeAsyncTask != null) {
            courbeAsyncTask.cancel(true);
        }
        Intent intent = new Intent(getApplicationContext(), Step2Activity.class);
        intent.putExtra("selectedVideoPath", videoPath);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    /**
     * Getter btnDownload
     *
     * @return btnDownload
     */
    public Button getBtnDownload() {
        return btnDownload;
    }

    /**
     * Getter btnShare
     *
     * @return btnShare
     */
    public Button getBtnShare() {
        return btnShare;
    }

    /**
     * Runs when the save button is clicked
     * Displays a dialog to name the image and perfoms the save of it
     */
    public void saveImage(final Bitmap bitmap) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.NameSavedImageDialog);
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.saveimagedialog, null);
        builder.setView(dialogView);
        final EditText editTextName = dialogView.findViewById(R.id.editTxtDialogSaveImage);
        builder.setTitle("Image name");
        builder.setPositiveButton("DONE",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String filename = editTextName.getText().toString();
                        if (!filename.isEmpty()) {
                            SaveImageAsyncTask saveImageAsyncTask = new SaveImageAsyncTask(rootLinearLayoutResult, filename, bitmap);
                            saveImageAsyncTask.execute();
                        } else {
                            final Snackbar snackbar = Snackbar.make(rootLinearLayoutResult, "NO FILE NAME", Snackbar.LENGTH_LONG);
                            snackbar.setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snackbar.dismiss();
                                }
                            });
                            snackbar.show();
                        }
                    }
                });
        builder.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
