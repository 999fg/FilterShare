package team16.filtershare;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    OkHttpClient client;
    String android_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_filter_make);

        try {
            client = OHClient.getClient();
        } catch(IOException e) {
            e.printStackTrace();
        }

        GlobalVariables sfApp = ((GlobalVariables)getApplicationContext());
        String picturepath = sfApp.get_picture_path();
        bitmap = BitmapFactory.decodeFile(picturepath);

        GLSurfaceView mEffectView = (GLSurfaceView) findViewById(R.id.image_preview);
        mEffectView.setEGLContextClientVersion(2);
        mEffectView.setRenderer(this);
        mEffectView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        Bitmap bitimg = BitmapFactory.decodeFile(picturepath);
        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        brightness = FilterEffect.BRIGHTNESS.getValue();
        contrast = FilterEffect.CONTRAST.getValue();
        saturation = FilterEffect.SATURATION.getValue();
        sharpen = FilterEffect.SHARPEN.getValue();
        temperature = FilterEffect.TEMPERATURE.getValue();
        fade = FilterEffect.FADE.getValue();
        vignette = FilterEffect.VIGNETTE.getValue();
        grain = FilterEffect.GRAIN.getValue();

        mEffectView.requestRender();
        final TextInputLayout filternameWrapper = (TextInputLayout) findViewById(R.id.filternameWrapper);
        final TextInputLayout usernameWrapper = (TextInputLayout) findViewById(R.id.usernameWrapper);
        final TextInputLayout tagsWrapper = (TextInputLayout) findViewById(R.id.tagsWrapper);

        class CustomOnClickListener implements View.OnClickListener {
            public void onClick(View v) {
                hideKeyboard();
                String name = filternameWrapper.getEditText().getText().toString();
                String username = usernameWrapper.getEditText().getText().toString();
                String tags = tagsWrapper.getEditText().getText().toString();
                if (name.length() < 1) {
                    filternameWrapper.setError("Please input name to share your filter!");
                }
                else if (username.length() < 1) {
                    usernameWrapper.setError("Please input username to share your filter!");
                } else if (tags.length() < 1) {
                    tagsWrapper.setError("Please input hashtags to share your filter!");
                } else {
                    filternameWrapper.setErrorEnabled(false);
                    usernameWrapper.setErrorEnabled(false);
                    tagsWrapper.setErrorEnabled(false);
                    shareAsyncTask SAT = new shareAsyncTask();
                    SAT.execute(name, username, tags);
                    Intent intent = new Intent(FilterMakingConfirmActivity.this, ShareFilterActivity.class);
                    startActivity(intent);
                }
            }
        }

        CustomOnClickListener cocl = new CustomOnClickListener();

        final Button back_button = (Button) findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilterMakingConfirmActivity.this, FilterMakingActivity.class);
                startActivity(intent);
            }
        });

        final Button share_button = (Button) findViewById(R.id.share_button);
        share_button.setOnClickListener(cocl);
        /*
        share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilterMakingConfirmActivity.this, ShareFilterActivity.class);
                startActivity(intent);
            }
        });
        */
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private JSONObject ifShareValidated(String name, String username, String tags) throws Exception{

        Date date = new Date();
        RequestBody formBody = new FormBody.Builder()
                .add("name", name)
                .add("username", username)
                .add("tags", tags)
                .add("device_id", android_id)
                .add("brightness", "39.0")
                .add("contrast", "39.0")
                .add("saturation", "39.0")
                .add("sharpen", "39.0")
                .add("temperature", "39.0")
                .add("tint", "39.0")
                .add("vignette", "39.0")
                .add("grain", "39.0")
                .add("date_created", date.getTime()+"")
                .build();
        Request request = new Request.Builder()
                .url("http://52.52.31.137/API/share_filter.php")
                .post(formBody)
                .build();

        JSONObject jsonObject = null;
        String jsonData = "";
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            jsonData = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            jsonObject = new JSONObject(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private class shareAsyncTask extends AsyncTask<String, Void, JSONObject> {
        JSONObject res = null;
        public shareAsyncTask() {

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try{
                res = ifShareValidated(params[0],params[1],params[2]);
            }catch(Exception e){
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            JSONObject jsonobject = result;
            Log.e("Success?", result+"");
        }

        protected JSONObject getres(){
            return res;
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
