package team16.filtershare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.logging.Filter;

/**
 * Created by shinjaemin on 2016. 7. 6..
 */
public class FilterMakingActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_filter);

        final Button change_image_button = (Button) findViewById(R.id.change_image_button);
        change_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilterMakingActivity.this, PhotoConfirmActivity.class);
                startActivity(intent);
            }
        });
        final Button next_button = (Button) findViewById(R.id.next_button);
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilterMakingActivity.this, FilterMakingConfirmActivity.class);
                startActivity(intent);
            }
        });
    }
}
