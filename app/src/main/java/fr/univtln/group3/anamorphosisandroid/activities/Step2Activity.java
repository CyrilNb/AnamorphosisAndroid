package fr.univtln.group3.anamorphosisandroid.activities;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.univtln.group3.anamorphosisandroid.R;
import fr.univtln.group3.anamorphosisandroid.Utility.Utils;
import fr.univtln.group3.anamorphosisandroid.customViews.TouchView;

public class Step2Activity extends AppCompatActivity {

    @BindView(R.id.videoView)
    VideoView videoView;
    @BindView(R.id.txtViewStep2)
    TextView textViewStep2;
    @BindView(R.id.linearLayoutStep2)
    LinearLayout linearLayoutStep2;
    @BindView(R.id.tableLayout)
    TableLayout tableLayout;
    @BindView(R.id.touchView)
    TouchView touchView;
    @BindView(R.id.linearLayoutTouchView)
    LinearLayout linearLayoutTouchView;

    String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step2);
        ButterKnife.bind(this);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/LuckiestGuy-Regular.ttf"); // create a typeface from the raw ttf
        textViewStep2.setTypeface(typeface);
        TextView toolbarText = findViewById(R.id.toolbar_text);
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbarText != null && toolbar != null) {
            toolbarText.setText(getTitle());
            toolbarText.setTypeface(typeface);
            setSupportActionBar(toolbar);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            videoPath = extras.getString("selectedVideoPath");
            videoView.setVideoPath(videoPath);
            videoView.start();
        }
    }

    /**
     * Runs when the classic button is clicked
     * Displays directions to choose
     */
    @OnClick(R.id.btnClassicModeSelected)
    public void onClassicModeButtonClicked() {
        textViewStep2.setText(getString(R.string.txt_step3_classic));
        linearLayoutStep2.setVisibility(View.GONE);
        tableLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Runs when the custom button is clicked
     * Displays the touchview to draw custom curve
     */
    @OnClick(R.id.btnCustomModeSelected)
    public void onCustomModeButtonClicked() {
        textViewStep2.setText(getString(R.string.txt_step3_custom));
        linearLayoutStep2.setVisibility(View.GONE);
        touchView.setVisibility(View.VISIBLE);
        linearLayoutTouchView.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btnUpToDown)
    public void onUpToDownButtonClicked() {

        launchResultIntent(false, Utils.Direction.DOWN, null);
    }

    @OnClick(R.id.btnDownToUp)
    public void onDownToUpButtonClicked() {
        launchResultIntent(false, Utils.Direction.UP, null);
    }

    @OnClick(R.id.btnRightToLeft)
    public void onRightToLeftButtonClicked() {
        launchResultIntent(false, Utils.Direction.LEFT, null);
    }

    @OnClick(R.id.btnLeftToRight)
    public void onLeftToRightButtonClicked() {
        launchResultIntent(false, Utils.Direction.RIGHT, null);
    }

    @OnClick(R.id.btnSaveCustomDraw)
    public void onSaveCustomDrawButtonClicked() {
        if (touchView.getCurvePoints().isEmpty()) {
            Toast.makeText(this, "Please, draw a curve", Toast.LENGTH_SHORT).show();
        } else {
            launchResultIntent(true, null, touchView.getCurvePoints());
        }
    }

    /**
     * Get Back to previous activity on back button pressed
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Step1Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    /**
     * Starts ResultActivity which execute correct AsyncTask based on boolean isCustom
     *
     * @param isCustom   true if custom mode is selected, otherwise false
     * @param direction  in which the anamorphosis goes. Must be null for custom mode.
     * @param pointsList get from the user, represents the custom curve. Must be null in classic mode.
     */
    private void launchResultIntent(Boolean isCustom, Utils.Direction direction, ArrayList<Point> pointsList) {
        Intent intentResult = new Intent(getApplicationContext(), ResultActivity.class);
        intentResult.putExtra("selectedVideoPath", videoPath);
        intentResult.putExtra("isCustom", isCustom);
        if (direction != null)
            intentResult.putExtra("direction", direction.getValue());
        if (pointsList != null) {
            intentResult.putExtra("pointsList", pointsList);
        }
        startActivity(intentResult);
        finish();
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
    }

}
