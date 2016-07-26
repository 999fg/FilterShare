package team16.filtershare;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
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
import android.widget.ImageView;

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
public class FilterMakingConfirmActivity extends AppCompatActivity {

    int[] mTextures = new int[3];
    private EffectContext mEffectContext;
    private Effect[] mEffectArray = new Effect[8];
    private int mImageWidth;
    private int mImageHeight;
    private  TextureRenderer mTexRenderer = new TextureRenderer();
    int firstEffect = 0;
    private boolean mInitialized = false;

    int brightness, contrast, saturation, fade, temperature, tint, vignette, grain;

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
        String picturepath = sfApp.get_scaled_path();
        bitmap = BitmapFactory.decodeFile(picturepath);

        ImageView imagePreview = (ImageView) findViewById(R.id.image_preview);
        imagePreview.setImageBitmap(BitmapProcessing.applyEffects(bitmap,
                FilterEffect.BRIGHTNESS.getValue(),
                FilterEffect.CONTRAST.getValue(),
                FilterEffect.SATURATION.getValue(),
                FilterEffect.FADE.getValue(),
                FilterEffect.TEMPERATURE.getValue(),
                FilterEffect.TINT.getValue(),
                FilterEffect.VIGNETTE.getValue(),
                FilterEffect.GRAIN.getValue()));
        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

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
                .add("brightness", String.valueOf(FilterEffect.BRIGHTNESS.getValue()))
                .add("contrast", String.valueOf(FilterEffect.CONTRAST.getValue()))
                .add("saturation", String.valueOf(FilterEffect.SATURATION.getValue()))
                .add("fade", String.valueOf(FilterEffect.FADE.getValue()))
                .add("temperature", String.valueOf(FilterEffect.TEMPERATURE.getValue()))
                .add("tint", String.valueOf(FilterEffect.TINT.getValue()))
                .add("vignette", String.valueOf(FilterEffect.VIGNETTE.getValue()))
                .add("grain", String.valueOf(FilterEffect.GRAIN.getValue()))
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
}
