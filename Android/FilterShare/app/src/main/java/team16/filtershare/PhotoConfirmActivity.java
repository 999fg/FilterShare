package team16.filtershare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.io.IOException;

/**
 * Created by shinjaemin on 2016. 7. 6..
 */
public class PhotoConfirmActivity extends AppCompatActivity {
    private ImageView mImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_photo);
        GlobalVariables mApp = ((GlobalVariables)getApplicationContext());
        //Log.d("filepath in confirm", mApp.get_picture_path());
        Bitmap bitimg = BitmapFactory.decodeFile(mApp.get_picture_path());

        mImage = (ImageView)findViewById(R.id.imgView);

        try {
            bitimg = MainActivity.rotate_image(mApp.get_picture_path(), bitimg);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mImage.setImageBitmap(bitimg);


        final Button retake_button = (Button) findViewById(R.id.retake_button);
        retake_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhotoConfirmActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        final Button confirm_button = (Button) findViewById(R.id.confirm_button);
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PhotoConfirmActivity.this, ShareFilterActivity.class);
                startActivity(intent);

            }
        });

        tutorial2();



    }

    private void tutorial2(){
        //make tutorial for the page
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        boolean didTutorial = sharedPref.getBoolean("didTutorial2", false);

        Button mConfirm = (Button) findViewById(R.id.confirm_button);
        if(!didTutorial) {
            ShowcaseView mShowcaseView1 = new ShowcaseView.Builder(this)

                    .setTarget(new ViewTarget(mConfirm))
                    .setContentTitle("Confirm the picture that you selected")
                    .setContentText("If you are satisfied with the current picture, touch Confirm button to apply filters")
                    //.setStyle(R.style.CustomShowcaseTheme2)
                    .blockAllTouches()
                    .replaceEndButton(R.layout.scv_button)

                    .build();

            Display display = getWindowManager().getDefaultDisplay();
            final Point screen_size = new Point();
            display.getSize(screen_size);
            final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            layoutParams.setMargins(0, screen_size.y * 3 / 10, 0, 0);

            mShowcaseView1.setButtonPosition(layoutParams);
            //mShowcaseView1.forceTextPosition(ShowcaseVie    editor.commit();

            sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("didTutorial2", true);
            editor.commit();

        }
    }
}
