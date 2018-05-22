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

import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;

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
    Bitmap result;


    int compteur_numColonneStart;

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
            compteur_numColonneStart = 0; //cpt_h
            Uri selectedVideoUri = data.getData();
            String selectedVideoPath = Utils.getPath(this, selectedVideoUri);
            System.out.println(selectedVideoPath);
            if (selectedVideoPath != null) {
                FrameGrabber frameGrabber = new FFmpegFrameGrabber(selectedVideoPath);

                int largeur = 0;
                int hauteur = 0;

                try {
                    frameGrabber.start();
                    largeur = frameGrabber.getImageWidth();
                    hauteur = frameGrabber.getImageHeight();

                    if (largeur != 0 | hauteur != 0) {
                        System.out.println("largeur: " + largeur);
                        System.out.println("hauteur: " + hauteur);
                        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
                        result = Bitmap.createBitmap(largeur, hauteur, conf); // this creates a MUTABLE bitmap
                    } else {
                        System.out.println("ERROR: largeur and hauteur = 0 from FrameGrabber");
                    }

                } catch (FrameGrabber.Exception e) {
                    e.printStackTrace();
                }

                frameGrabber.setFormat("mp4");

                Frame frame = null;

                int nbFrames = frameGrabber.getLengthInFrames();
                System.out.println("nbFrames: " + nbFrames);

                int facteurLargeurEtNbFrames = largeur / nbFrames; //s1
                float reste = (float) nbFrames / ((float) nbFrames - ((float) largeur % (float) nbFrames));

                System.out.println("facteurLargeurEtNbFrames: " + facteurLargeurEtNbFrames);
                System.out.println("reste: " + reste);

                float compteurDuReste = 0; //cpt_s2
                int compteurNombreDeFrames = 0; //cpt
                int countImagesRecupereGraceAuReste = 0; //cpt_img

                while (compteurNombreDeFrames < nbFrames) {
                    frame = getFrame(frameGrabber);
                    if (frame != null) {
                        convertAndExec(frame, facteurLargeurEtNbFrames, hauteur, largeur);

                        if (compteurNombreDeFrames < compteurDuReste) {
                            countImagesRecupereGraceAuReste++;
                            convertAndExec(frame, 1, hauteur, largeur);
                        } else {
                            compteurDuReste += reste;
                        }
                        compteurNombreDeFrames++;
                        imageViewResult.setImageBitmap(result);
                    }
                }

                System.out.println("countImagesRecupereGraceAuReste: " + countImagesRecupereGraceAuReste);
                System.out.println("CEST FINIIIIIIIIIIII");

            } else {
                System.out.println("video path is null");
            }
        }

    }


    private Frame getFrame(FrameGrabber frameGrabber) {
        Frame frame = null;
        try {
            frame = frameGrabber.grabFrame();
            if (frame != null) {
                if (frame.image != null) {
                    return frame;
                }
            } else {
                return null;
            }
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void convertAndExec(Frame frame, int facteurHauteurEtNbFrames, int hauteur, int largeur) {
        AndroidFrameConverter androidFrameConverter = new AndroidFrameConverter();
        Bitmap bitmap = androidFrameConverter.convert(frame);
        gaucheVersDroite(bitmap, compteur_numColonneStart, facteurHauteurEtNbFrames, hauteur, largeur);
        compteur_numColonneStart += facteurHauteurEtNbFrames;
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

    private void gaucheVersDroite(Bitmap sourceFrame, int numColonneStart, int nombreDeColonnesAPRENDRE, int hauteur, int largeur) {

        int[] pixelsSourceFrame = new int[largeur * hauteur];
        sourceFrame.getPixels(pixelsSourceFrame, 0, largeur, 0, 0, largeur, hauteur);

        int[] pixelsResult = new int[largeur * hauteur];
        result.getPixels(pixelsResult,0,largeur,0,0,largeur,hauteur);

        for (int j = numColonneStart; j < nombreDeColonnesAPRENDRE + numColonneStart; j++) {
            for (int i = 0; i < hauteur; i++) { // pour chaque element dans la ligne
                if(i ==0 ){
                    int index = (i * largeur) + j;
                    System.out.println("index: "+ index);
                }
                pixelsResult[(i * largeur) + j] = pixelsSourceFrame[(i * largeur)  + j];
            }
        }
        System.out.println("je change les pixels");
        result.setPixels(pixelsResult, 0, largeur, 0, 0, largeur, hauteur);
    }

}