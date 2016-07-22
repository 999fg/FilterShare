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
import android.widget.Button;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by shinjaemin on 2016. 7. 6..
 */
public class FilterMakingConfirmActivity extends AppCompatActivity implements GLSurfaceView.Renderer {

    int[] mTextures = new int[3];
    private EffectContext mEffectContext;
    private Effect[] mEffectArray = new Effect[8];
    private int mImageWidth;
    private int mImageHeight;
    private  TextureRenderer mTexRenderer = new TextureRenderer();
    int firstEffect = 0;
    private boolean mInitialized = false;

    int brightness, contrast, saturation, sharpen, temperature, fade, vignette, grain;

    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_filter_make);

        GlobalVariables sfApp = ((GlobalVariables)getApplicationContext());
        String picturepath = sfApp.get_picture_path();
        bitmap = BitmapFactory.decodeFile(picturepath);

        GLSurfaceView mEffectView = (GLSurfaceView) findViewById(R.id.image_preview);
        mEffectView.setEGLContextClientVersion(2);
        mEffectView.setRenderer(this);
        mEffectView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        brightness = FilterEffect.BRIGHTNESS.getValue();
        contrast = FilterEffect.CONTRAST.getValue();
        saturation = FilterEffect.SATURATION.getValue();
        sharpen = FilterEffect.SHARPEN.getValue();
        temperature = FilterEffect.TEMPERATURE.getValue();
        fade = FilterEffect.FADE.getValue();
        vignette = FilterEffect.VIGNETTE.getValue();
        grain = FilterEffect.GRAIN.getValue();

        mEffectView.requestRender();

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

    @Override
    public void onDrawFrame(GL10 gl) {
        if (!mInitialized) {
        //Only need to do this once
            mEffectContext = EffectContext.createWithCurrentGlContext();
            mTexRenderer.init();
            loadTextures();
            mInitialized = true;
        }
        initEffect(brightness, contrast, saturation, sharpen, temperature, fade, vignette, grain);
        applyEffect();
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

    public void loadTextures() {
        // Generate textures
        GLES20.glGenTextures(3, mTextures, 0);

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

    private void initEffect(int brightness, int contrast, int saturation, int sharpen, int temperature, int fade, int vignette, int grain) {
        EffectFactory effectFactory = mEffectContext.getFactory();

        mEffectArray[0] = effectFactory.createEffect(EffectFactory.EFFECT_BRIGHTNESS);
        mEffectArray[0].setParameter("brightness", ((float) brightness / 100) + 1.0f);

        mEffectArray[1] = effectFactory.createEffect(EffectFactory.EFFECT_CONTRAST);
        mEffectArray[1].setParameter("contrast", ((float) contrast / 100) + 1.0f);

        mEffectArray[2] = effectFactory.createEffect(
                EffectFactory.EFFECT_SATURATE);
        mEffectArray[2].setParameter("scale", (((float) saturation / 100) - 0.5f) * 2);

        mEffectArray[3] = effectFactory.createEffect(
                EffectFactory.EFFECT_SHARPEN);
        mEffectArray[3].setParameter("scale", ((float) sharpen / 100));

        mEffectArray[4] = effectFactory.createEffect(
                EffectFactory.EFFECT_TEMPERATURE);
        mEffectArray[4].setParameter("scale", ((float) temperature / 100));

        mEffectArray[5] = effectFactory.createEffect(
                EffectFactory.EFFECT_FILLLIGHT);
        mEffectArray[5].setParameter("strength", (float) fade / 100);

        mEffectArray[6] = effectFactory.createEffect(
                EffectFactory.EFFECT_VIGNETTE);
        mEffectArray[6].setParameter("scale", ((float) vignette / 100));

        mEffectArray[7] = effectFactory.createEffect(
                EffectFactory.EFFECT_GRAIN);
        mEffectArray[7].setParameter("strength", ((float) grain / 100));
    }

    private void applyEffect() {
        mEffectArray[firstEffect].apply(mTextures[0], mImageWidth, mImageHeight, mTextures[1]);
        for (FilterEffect f : FilterEffect.values()) { // if more that one effect
            Log.d("APPLY", f.getName());
            int sourceTexture = mTextures[1];
            int destinationTexture = mTextures[2];
            mEffectArray[f.ordinal()].apply(sourceTexture, mImageWidth, mImageHeight, destinationTexture);
            mTextures[1] = destinationTexture; // changing the textures array, so 1 is always the texture for output,
            mTextures[2] = sourceTexture; // 2 is always the sparse texture
        }
    }

    private void renderResult() {
        mTexRenderer.renderTexture(mTextures[1]);
    }
}
