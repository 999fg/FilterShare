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
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by shinjaemin on 2016. 7. 6..
 */
public class FilterMakingActivity extends AppCompatActivity implements GLSurfaceView.Renderer, View.OnClickListener{
    private GLSurfaceView mEffectView;
    private int[] mTextures = new int[2];
    private EffectContext mEffectContext;
    private Effect mEffect;
    private TextureRenderer mTexRenderer = new TextureRenderer();
    private int mImageWidth;
    private int mImageHeight;
    private boolean mInitialized = false;
    int mCurrentEffect;

    private int brProgress;

    public void setCurrentEffect(int effect) {
        mCurrentEffect = effect;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_filter);

        /* Initial settings for image preview (GLSurfaceView) */
        mEffectView = (GLSurfaceView) findViewById(R.id.imgPreview);
        mEffectView.setEGLContextClientVersion(2);
        mEffectView.setRenderer(this);
        mEffectView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mCurrentEffect = R.id.none;

        Button brightness_button = (Button) findViewById(R.id.brightness_button);
        Button contrast_button = (Button) findViewById(R.id.contrast_button);
        Button saturation_button = (Button) findViewById(R.id.saturation_button);
        Button sharpen_button = (Button) findViewById(R.id.sharpen_button);
        Button temperature_button = (Button) findViewById(R.id.temperature_button);
        Button tint_button = (Button) findViewById(R.id.tint_button);
        Button vignette_button = (Button) findViewById(R.id.vignette_button);
        Button grain_button = (Button) findViewById(R.id.grain_button);

        brightness_button.setOnClickListener(this);
        contrast_button.setOnClickListener(this);
        saturation_button.setOnClickListener(this);
        sharpen_button.setOnClickListener(this);
        temperature_button.setOnClickListener(this);
        tint_button.setOnClickListener(this);
        vignette_button.setOnClickListener(this);
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

    public void onClick(View v) {
        switch(v.getId()){
            case R.id.brightness_button:
                //Check if the Layout already exists
                LinearLayout seek_bar = (LinearLayout)findViewById(R.id.seekBar);
                if(seek_bar == null){
                    //Inflate the Hidden Layout Information View
                    LinearLayout myLayout = (LinearLayout)findViewById(R.id.seekBarLayout);
                    View hiddenInfo = getLayoutInflater().inflate(R.layout.activity_make_filter_slider, myLayout, false);
                    myLayout.addView(hiddenInfo);
                }
                SeekBar br_seekbar = (SeekBar) findViewById(R.id.brSeekBar);
                br_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        brProgress = seekBar.getProgress();
                        Log.d("brProgress", Integer.toString(brProgress));
                        mEffectView.requestRender();
                    }
                });
                break;
            case R.id.contrast_button:
                break;
            case R.id.saturation_button:
                break;
            case R.id.sharpen_button:
                break;
            case R.id.temperature_button:
                break;
            case R.id.tint_button:
                break;
            case R.id.vignette_button:
                break;
            case R.id.grain_button:
                break;
        }
    }
    private void loadTextures() {
        // Generate textures
        GLES20.glGenTextures(2, mTextures, 0);

        // Load input bitmap
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.test_horizontal);
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
        /*
        if (mEffect != null) {
            mEffect.release();
        }
        */
        mEffect = effectFactory.createEffect(EffectFactory.EFFECT_BRIGHTNESS);
        Log.d("BR PROGRESS:", Integer.toString(progress));
        mEffect.setParameter("brightness", ((float) progress / 100) + 1.0f);
    }
    private void applyEffect() {
        mEffect.apply(mTextures[0], mImageWidth, mImageHeight, mTextures[1]);
    }

    private void renderResult() {
        //if (mCurrentEffect != R.id.none) {
            // if no effect is chosen, just render the original bitmap
            mTexRenderer.renderTexture(mTextures[1]);
        /*} else {
            // render the result of applyEffect()
            mTexRenderer.renderTexture(mTextures[0]);
        }*/
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
        //if (mCurrentEffect != R.id.none) {
        Log.d("IN", "HERE");
        //if an effect is chosen initialize it and apply it to the texture
        initEffect(brProgress);
        applyEffect();
        //}
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
}

