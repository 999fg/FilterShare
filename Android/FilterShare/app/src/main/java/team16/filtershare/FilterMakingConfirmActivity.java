package team16.filtershare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by shinjaemin on 2016. 7. 6..
 */
public class FilterMakingConfirmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_filter_make);

        final Button back_button = (Button) findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilterMakingConfirmActivity.this, FilterMakingActivity.class);
                startActivity(intent);
            }
        });

        final Button share_button = (Button) findViewById(R.id.share_button);
        share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilterMakingConfirmActivity.this, ShareFilterActivity.class);
                startActivity(intent);
            }
        });
    }
}
