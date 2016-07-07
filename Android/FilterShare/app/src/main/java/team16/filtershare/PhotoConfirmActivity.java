package team16.filtershare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by shinjaemin on 2016. 7. 6..
 */
public class PhotoConfirmActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_photo);


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
