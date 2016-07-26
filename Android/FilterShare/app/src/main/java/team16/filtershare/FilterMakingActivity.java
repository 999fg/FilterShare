package team16.filtershare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

/**
 * Created by shinjaemin on 2016. 7. 6..
 */

public class FilterMakingActivity extends AppCompatActivity implements View.OnClickListener{
    FilterEffect currentEffect;
    int origProgress;
    int progress;
    ImageView imgPreview;
    Bitmap origBitmap;
    Bitmap bitmap;
    int[] origEffects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_filter);

        imgPreview = (ImageView) findViewById(R.id.imgPreview);

        GlobalVariables sfApp = ((GlobalVariables)getApplicationContext());
        String picturepath = sfApp.get_scaled_path();
        origBitmap = BitmapFactory.decodeFile(picturepath);
        imgPreview.setImageBitmap(origBitmap);

        inflateEffects();

        tutorial4();
    }

    public void onClick(View v) {
        switch(v.getId()){
            case R.id.brightness_button:
                currentEffect = FilterEffect.BRIGHTNESS;
                inflateSlider();
                break;
            case R.id.contrast_button:
                currentEffect = FilterEffect.CONTRAST;
                inflateSlider();
                break;
            case R.id.saturation_button:
                currentEffect = FilterEffect.SATURATION;
                inflateSlider();
                break;
            case R.id.fade_button:
                currentEffect = FilterEffect.FADE;
                inflateSlider();
                break;
            case R.id.temperature_button:
                currentEffect = FilterEffect.TEMPERATURE;
                inflateSlider();
                break;
            case R.id.tint_button:
                currentEffect = FilterEffect.TINT;
                inflateSlider();
                break;
            case R.id.vignette_button:
                currentEffect = FilterEffect.VIGNETTE;
                inflateSlider();
                break;
            case R.id.grain_button:
                currentEffect = FilterEffect.GRAIN;
                inflateSlider();
                break;
            case R.id.cancel_button:
                currentEffect.setValue(origProgress);
                inflateEffects();
                break;
            case R.id.done_button:
                SeekBar seekBar = (SeekBar) findViewById(R.id.seek_bar);
                currentEffect.setValue(seekBar.getProgress());
                inflateEffects();
                break;
        }
    }

    private void inflateSlider() {
        //Check if seek bar layout already exists i.e. if it's slider mode
        Log.d("Inflate slider", "IN");
        // Remove existing select effect view
        View bottomBar = findViewById(R.id.select_effect_layout);
        ViewGroup parent = (ViewGroup)bottomBar.getParent();
        int index = parent.indexOfChild(bottomBar);
        parent.removeView(bottomBar);

        //Inflate the slider view
        bottomBar = getLayoutInflater().inflate(R.layout.activity_make_filter_slider, parent, false);
        parent.addView(bottomBar, index);

        TextView effectName = (TextView) findViewById(R.id.effect_name);
        effectName.setText(currentEffect.getName().toUpperCase());

        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(this);

        Button doneButton = (Button) findViewById(R.id.done_button);
        doneButton.setOnClickListener(this);

        origProgress = (int) currentEffect.getValue();

        SeekBar seekBar = (SeekBar) findViewById(R.id.seek_bar);
        seekBar.setProgress(origProgress);
        TextView seekBarValue = (TextView) findViewById(R.id.seek_bar_value);
        seekBarValue.setText(Integer.toString(origProgress));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                progress = seekBar.getProgress();
                Log.d("progress", Integer.toString(progress));
                currentEffect.setValue(progress);
                bitmap = BitmapProcessing.applyEffects(origBitmap,
                        FilterEffect.BRIGHTNESS.getValue(),
                        FilterEffect.CONTRAST.getValue(),
                        FilterEffect.SATURATION.getValue(),
                        FilterEffect.FADE.getValue(),
                        FilterEffect.TEMPERATURE.getValue(),
                        FilterEffect.TINT.getValue(),
                        FilterEffect.VIGNETTE.getValue(),
                        FilterEffect.GRAIN.getValue());
                imgPreview.setImageBitmap(bitmap);

                TextView seekBarValue = (TextView) findViewById(R.id.seek_bar_value);
                seekBarValue.setText(Integer.toString(progress));
            }
        });

    }

    private void inflateEffects() {
        bitmap = BitmapProcessing.applyEffects(origBitmap,
                FilterEffect.BRIGHTNESS.getValue(),
                FilterEffect.CONTRAST.getValue(),
                FilterEffect.SATURATION.getValue(),
                FilterEffect.FADE.getValue(),
                FilterEffect.TEMPERATURE.getValue(),
                FilterEffect.TINT.getValue(),
                FilterEffect.VIGNETTE.getValue(),
                FilterEffect.GRAIN.getValue());
        imgPreview.setImageBitmap(bitmap);

        // Find the parent container for bottom bar
        LinearLayout parent = (LinearLayout) findViewById(R.id.bottom_bar);

        // Remove seek_bar_layout view if exists
        View bottomBar = findViewById(R.id.seek_bar_layout);
        int index = parent.indexOfChild(bottomBar);
        parent.removeView(bottomBar);

        //Inflate the slider view
        bottomBar = getLayoutInflater().inflate(R.layout.activity_select_effect, parent, false);
        parent.addView(bottomBar, index);

        ImageButton brightness_button = (ImageButton) findViewById(R.id.brightness_button);
        brightness_button.setOnClickListener(this);
        ImageButton contrast_button = (ImageButton) findViewById(R.id.contrast_button);
        contrast_button.setOnClickListener(this);
        ImageButton saturation_button = (ImageButton) findViewById(R.id.saturation_button);
        saturation_button.setOnClickListener(this);
        ImageButton fade_button = (ImageButton) findViewById(R.id.fade_button);
        fade_button.setOnClickListener(this);
        ImageButton temperature_button = (ImageButton) findViewById(R.id.temperature_button);
        temperature_button.setOnClickListener(this);
        ImageButton tint_button = (ImageButton) findViewById(R.id.tint_button);
        tint_button.setOnClickListener(this);
        ImageButton vignette_button = (ImageButton) findViewById(R.id.vignette_button);
        vignette_button.setOnClickListener(this);
        ImageButton grain_button = (ImageButton) findViewById(R.id.grain_button);
        grain_button.setOnClickListener(this);

        final Button change_image_button = (Button) findViewById(R.id.change_image_button);
        change_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeFilterEffects();
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

    void initializeFilterEffects() {
        FilterEffect.BRIGHTNESS.setValue(50);
        FilterEffect.CONTRAST.setValue(50);
        FilterEffect.SATURATION.setValue(50);
        FilterEffect.FADE.setValue(0);
        FilterEffect.TEMPERATURE.setValue(50);
        FilterEffect.TINT.setValue(0);
        FilterEffect.VIGNETTE.setValue(0);
        FilterEffect.GRAIN.setValue(0);
    }

    private void tutorial4(){
        ImageButton mBrightness = (ImageButton) findViewById(R.id.brightness_button);
        //make tutorial for the page
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        boolean didTutorial = sharedPref.getBoolean("didTutorial4", false);

        if(!didTutorial) {
            ShowcaseView mShowcaseView1 = new ShowcaseView.Builder(this)

                    .setTarget(new ViewTarget(mBrightness))
                    .setContentTitle("Edit the filter property you want to modify")
                    .setContentText("There are 8 filter properties that you can modify and combine to make your own filter. Touch the icon and edit the filter")
                    //.setStyle(R.style.CustomShowcaseTheme2)
                    .blockAllTouches()
                    .replaceEndButton(R.layout.scv_button)

                    .build();

            Display display = getWindowManager().getDefaultDisplay();
            final Point screen_size = new Point();
            display.getSize(screen_size);
            final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            layoutParams.setMargins(0, screen_size.y * 4 / 11, 0, 0);

            mShowcaseView1.setButtonPosition(layoutParams);
            //mShowcaseView1.forceTextPosition(ShowcaseView.LEFT_OF_SHOWCASE);

            mShowcaseView1.setOnShowcaseEventListener(new OnShowcaseEventListener() {
                @Override
                public void onShowcaseViewHide(ShowcaseView showcaseView) {
                    Button mNext = (Button) findViewById(R.id.next_button);

                    ShowcaseView mShowcaseView2 = new ShowcaseView.Builder(FilterMakingActivity.this)

                            .setTarget(new ViewTarget(mNext))
                            .setContentTitle("Confirm your filter")
                            .setContentText("If you are satisfied with your own filter, touch the next button to upload your own filter")
                            //.setStyle(R.style.CustomShowcaseTheme2)
                            .blockAllTouches()

                            .replaceEndButton(R.layout.scv_button)

                            .build();
                    mShowcaseView2.setButtonPosition(layoutParams);

                    SharedPreferences sharedPref = FilterMakingActivity.this.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("didTutorial4", true);
                    editor.commit();

                }

                @Override
                public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                }

                @Override
                public void onShowcaseViewShow(ShowcaseView showcaseView) {
                }

                @Override
                public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

                };

            });
        }
    }
}

