package fr.univtln.group3.anamorphosisandroid.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.VideoView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.univtln.group3.anamorphosisandroid.R;
import fr.univtln.group3.anamorphosisandroid.Utils;
import fr.univtln.group3.anamorphosisandroid.asyncTasks.TraitementAsync;

public class Step2Activity extends AppCompatActivity {

    @BindView(R.id.videoView)
    VideoView videoView;
    @BindView(R.id.txtViewStep2)
    TextView textViewStep2;
    @BindView(R.id.linearLayoutStep2)
    LinearLayout linearLayoutStep2;
    @BindView(R.id.tableLayout)
    TableLayout tableLayout;

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
     * Goes to Step 3 Acitivity
     */
    @OnClick(R.id.btnClassicModeSelected)
    public void onClassicModeButtonClicked() {
        textViewStep2.setText(getString(R.string.txt_step3_classic));
        linearLayoutStep2.setVisibility(View.GONE);
        tableLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btnUpToDown)
    public void onUpToDownButtonClicked(){
        startClassicAnamophosis(Utils.Direction.DOWN);
    }

    @OnClick(R.id.btnDownToUp)
    public void onDownToUpButtonClicked(){
        startClassicAnamophosis(Utils.Direction.UP);
    }

    @OnClick(R.id.btnRightToLeft)
    public void onRightToLeftButtonClicked(){
        startClassicAnamophosis(Utils.Direction.LEFT);
    }

    @OnClick(R.id.btnLeftToRight)
    public void onLeftToRightButtonClicked(){
        startClassicAnamophosis(Utils.Direction.RIGHT);
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

    private void startClassicAnamophosis(Utils.Direction direction)
    {
        Intent intentStep3 = new Intent(getApplicationContext(), ResultActivity.class);
        intentStep3.putExtra("selectedVideoPath", videoPath);
        intentStep3.putExtra("direction", direction.getValue());
        startActivity(intentStep3);
        finish();
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
    }



}
