package team16.filtershare;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by shinjaemin on 2016. 7. 6..
 */

public class FilterMakingActivity extends AppCompatActivity implements GLSurfaceView.Renderer, View.OnClickListener{
    private GLSurfaceView mEffectView;
    private int[] mTextures = new int[3];
    private EffectContext mEffectContext;
    private TextureRenderer mTexRenderer = new TextureRenderer();
    private int mImageWidth;
    private int mImageHeight;
    private boolean mInitialized = false;

    private int progress = 0;
    private int origProgress = 0;
    private FilterEffect currentEffect = null;
    private Effect[] mEffectArray = new Effect[8];
    private boolean[] isEffectApplied = new boolean[]{false, false, false, false, false, false, false, false};
    private int mEffectCount = 0;
    private int firstEffect = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_filter);

        /* Initial settings for image preview (GLSurfaceView) */
        mEffectView = (GLSurfaceView) findViewById(R.id.imgPreview);
        mEffectView.setEGLContextClientVersion(2);
        mEffectView.setRenderer(this);
        mEffectView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        inflateEffects();
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
            case R.id.sharpen_button:
                currentEffect = FilterEffect.SHARPEN;
                inflateSlider();
                break;
            case R.id.temperature_button:
                currentEffect = FilterEffect.TEMPERATURE;
                inflateSlider();
                break;
            case R.id.fade_button:
                currentEffect = FilterEffect.FADE;
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
                inflateEffects();
                progress = origProgress;
                mEffectView.requestRender();
                break;
            case R.id.done_button:
                SeekBar seekBar = (SeekBar) findViewById(R.id.seek_bar);
                currentEffect.setValue(seekBar.getProgress());
                inflateEffects();
                break;
        }
    }

    private void loadTextures() {
        // Generate textures
        GLES20.glGenTextures(3, mTextures, 0);

        // Load selected image into bitmap
        GlobalVariables sfApp = ((GlobalVariables)getApplicationContext());
        String picturepath = sfApp.get_picture_path();
        Bitmap bitmap = BitmapFactory.decodeFile(picturepath);

        // Store bitmap info and update texture size
        mImageWidth = bitmap.getWidth();
        mImageHeight = bitmap.getHeight();
        mTexRenderer.updateTextureSize(mImageWidth, mImageHeight);

        // Upload to texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        // Set texture parameters
        GLToolbox.initTexParams();
    }

    private void initEffect(int progress) {
        EffectFactory effectFactory = mEffectContext.getFactory();

        switch (currentEffect) {
            case BRIGHTNESS:
                Log.d("BR PROGRESS:", Integer.toString(progress));
                mEffectArray[0] = effectFactory.createEffect(EffectFactory.EFFECT_BRIGHTNESS);
                mEffectArray[0].setParameter("brightness", ((float) progress / 100) + 1.0f);
                isEffectApplied[0] = true;
                if (mEffectCount == 0) {
                    firstEffect = 0;
                    mEffectCount++;
                }
                break;
            case CONTRAST:
                Log.d("CT PROGRESS:", Integer.toString(progress));
                mEffectArray[1] = effectFactory.createEffect(EffectFactory.EFFECT_CONTRAST);
                mEffectArray[1].setParameter("contrast", ((float) progress / 100) + 1.0f);
                isEffectApplied[1] = true;
                if (mEffectCount == 0) {
                    firstEffect = 1;
                    mEffectCount++;
                }
                break;
            case SATURATION:
                Log.d("ST PROGRESS:", Integer.toString(progress));
                mEffectArray[2] = effectFactory.createEffect(
                        EffectFactory.EFFECT_SATURATE);
                mEffectArray[2].setParameter("scale", (((float) progress / 100) - 0.5f) * 2);
                isEffectApplied[2] = true;
                if (mEffectCount == 0) {
                    firstEffect = 2;
                    mEffectCount++;
                }
                break;
            case SHARPEN:
                Log.d("SH PROGRESS:", Integer.toString(progress));
                mEffectArray[3] = effectFactory.createEffect(
                        EffectFactory.EFFECT_SHARPEN);
                mEffectArray[3].setParameter("scale", ((float) progress / 100));
                isEffectApplied[3] = true;
                if (mEffectCount == 0) {
                    firstEffect = 3;
                    mEffectCount++;
                }
                break;
            case TEMPERATURE:
                Log.d("TM PROGRESS:", Integer.toString(progress));
                mEffectArray[4] = effectFactory.createEffect(
                        EffectFactory.EFFECT_TEMPERATURE);
                mEffectArray[4].setParameter("scale", ((float) progress / 100));
                isEffectApplied[4] = true;
                if (mEffectCount == 0) {
                    firstEffect = 4;
                    mEffectCount++;
                }
                break;
            case FADE:
                Log.d("FL PROGRESS:", Integer.toString(progress));
                mEffectArray[5] = effectFactory.createEffect(
                        EffectFactory.EFFECT_FILLLIGHT);
                mEffectArray[5].setParameter("strength", (float) progress / 100);
                isEffectApplied[5] = true;
                if (mEffectCount == 0) {
                    firstEffect = 5;
                    mEffectCount++;
                }
                break;
            case VIGNETTE:
                Log.d("VG PROGRESS:", Integer.toString(progress));
                mEffectArray[6] = effectFactory.createEffect(
                        EffectFactory.EFFECT_VIGNETTE);
                mEffectArray[6].setParameter("scale", ((float) progress / 100));
                isEffectApplied[6] = true;
                if (mEffectCount == 0) {
                    firstEffect = 6;
                    mEffectCount++;
                }
                break;
            case GRAIN:
                Log.d("GR PROGRESS:", Integer.toString(progress));
                mEffectArray[7] = effectFactory.createEffect(
                        EffectFactory.EFFECT_GRAIN);
                mEffectArray[7].setParameter("strength", ((float) progress / 100));
                isEffectApplied[7] = true;
                if (mEffectCount == 0) {
                    firstEffect = 7;
                    mEffectCount++;
                }
                break;
            default:
                break;
        }
    }

    private void applyEffect() {
        if (mEffectCount > 0) {
            mEffectArray[firstEffect].apply(mTextures[0], mImageWidth, mImageHeight, mTextures[1]);
            for (FilterEffect f : FilterEffect.values()) { // if more that one effect
                if (isEffectApplied[f.ordinal()]) {
                    int sourceTexture = mTextures[1];
                    int destinationTexture = mTextures[2];
                    mEffectArray[f.ordinal()].apply(sourceTexture, mImageWidth, mImageHeight, destinationTexture);
                    mTextures[1] = destinationTexture; // changing the textures array, so 1 is always the texture for output,
                    mTextures[2] = sourceTexture; // 2 is always the sparse texture
                }
            }
        }
    }

    private void renderResult() {
        if (currentEffect != null) {
            // if no effect is chosen, just render the original bitmap
            mTexRenderer.renderTexture(mTextures[1]);
        } else {
            // render the result of applyEffect()
            mTexRenderer.renderTexture(mTextures[0]);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (!mInitialized) {
            //Only need to do this once
            mEffectContext = EffectContext.createWithCurrentGlContext();
            mTexRenderer.init();
            loadTextures();
            mInitialized = true;
        }
        if (currentEffect != null) {
            Log.d("IN", "HERE");
            //if an effect is chosen initialize it and apply it to the texture
            initEffect(progress);
            applyEffect();
        }
        renderResult();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (mTexRenderer != null) {
            mTexRenderer.updateViewSize(width, height);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
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
                progress = seekBar.getProgress();
                Log.d("progress", Integer.toString(progress));
                mEffectView.requestRender();

                TextView seekBarValue = (TextView) findViewById(R.id.seek_bar_value);
                seekBarValue.setText(Integer.toString(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                progress = seekBar.getProgress();
                Log.d("progress", Integer.toString(progress));
                mEffectView.requestRender();

                TextView seekBarValue = (TextView) findViewById(R.id.seek_bar_value);
                seekBarValue.setText(Integer.toString(progress));
            }
        });

    }

    private void inflateEffects() {
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
        ImageButton sharpen_button = (ImageButton) findViewById(R.id.sharpen_button);
        sharpen_button.setOnClickListener(this);
        ImageButton temperature_button = (ImageButton) findViewById(R.id.temperature_button);
        temperature_button.setOnClickListener(this);
        ImageButton fade_button = (ImageButton) findViewById(R.id.fade_button);
        fade_button.setOnClickListener(this);
        ImageButton vignette_button = (ImageButton) findViewById(R.id.vignette_button);
        vignette_button.setOnClickListener(this);
        ImageButton grain_button = (ImageButton) findViewById(R.id.grain_button);
        grain_button.setOnClickListener(this);

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

