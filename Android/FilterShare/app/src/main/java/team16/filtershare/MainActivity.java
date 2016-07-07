package team16.filtershare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by shinjaemin on 2016. 7. 6..
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button camera_button = (Button) findViewById(R.id.goto_camera);
        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PhotoConfirmActivity.class);
                startActivity(intent);
            }
        });

        final Button gallary_button = (Button) findViewById(R.id.goto_gallary);
        gallary_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PhotoConfirmActivity.class);
                startActivity(intent);
            }
        });
    }
}
