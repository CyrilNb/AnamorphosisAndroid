package fr.univtln.group3.anamorphosisandroid.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.univtln.group3.anamorphosisandroid.R;
import fr.univtln.group3.anamorphosisandroid.asyncTasks.TraitementAsync;

public class ResultActivity extends AppCompatActivity {


    /******************************
     * BINDVIEWS with Butterknife *
     ******************************/
    @BindView(R.id.txtViewStep3)
    TextView textViewStep3;
    @BindView(R.id.imgViewResult)
    ImageView imgViewResult;
    @BindView(R.id.btnDownload)
    Button btnDownload;
    @BindView(R.id.btnHome)
    Button btnHome;

    /***********
     * MEMBERS *
     ***********/
    String videoPath;
    TraitementAsync async;

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

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            videoPath = extras.getString("selectedVideoPath");
            String direction = extras.getString("direction");
            async = new TraitementAsync(this,imgViewResult, direction);
            async.execute(videoPath);
        }

    }

    @OnClick(R.id.btnDownload)
    public void downloadResultImage(){

    }

    @OnClick(R.id.btnHome)
    public void goHome(){
        Intent intent = new Intent(getApplicationContext(), Step1Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }


    /**
     * Get Back to previous activity on back button pressed
     */
    @Override
    public void onBackPressed() {
        async.cancel(true);
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
}
