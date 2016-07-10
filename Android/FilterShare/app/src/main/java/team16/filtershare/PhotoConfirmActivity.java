package team16.filtershare;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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
        Log.d("filepath in confirm", mApp.get_picture_path());
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
    }
}
