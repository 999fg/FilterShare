package team16.filtershare;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by shinjaemin on 2016. 7. 10..
 */

public class SFAdapter extends RecyclerView.Adapter<SFAdapter.ViewHolder> {

    private ArrayList<SFData> sfDataset;
    private Context mContext;
    View content;
    OkHttpClient client;
    String android_id;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView filterTitle;
        public TextView filterLikes;
        public TextView filterHashtags;
        public TextView filterMadeby;

        public ViewHolder (View view) {
            super(view);
            imageView = (ImageView)view.findViewById(R.id.test_image);
            filterTitle = (TextView)view.findViewById(R.id.filter_title);
            filterLikes = (TextView)view.findViewById(R.id.filter_likes);
            filterHashtags = (TextView)view.findViewById(R.id.filter_hashtags);
            filterMadeby = (TextView)view.findViewById(R.id.filter_madeby);
        }
    }

    public SFAdapter(ArrayList<SFData> SFDataset, Context context, OkHttpClient mclient, String aid) {
        mContext = context;
        sfDataset = SFDataset;
        client = mclient;
        android_id = aid;
    }

    @Override
    public SFAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sf_view, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int filter_likes = sfDataset.get(position).likes;
        final int bitposition = position;
        String filter_hashtags = "";
        String[] hashtags = sfDataset.get(position).hashtags;
        final String title = sfDataset.get(position).title;
        final String atr_brightness = sfDataset.get(position).brightness+"";
        final String atr_contrast = sfDataset.get(position).contrast+"";
        final String atr_saturation = sfDataset.get(position).saturation+"";
        final String atr_fade = sfDataset.get(position).fade+"";
        final String atr_temperature = sfDataset.get(position).temperature+"";
        final String atr_tint = sfDataset.get(position).tint+"";
        final String atr_vignette = sfDataset.get(position).vignette+"";
        final String atr_grain = sfDataset.get(position).grain+"";
        final String filter_id = sfDataset.get(position).filter_id+"";
        Log.d("IMAGE SIZE", String.valueOf(sfDataset.get(position).imgbit.getWidth()));
        Log.d("IMAGE SIZE", String.valueOf(sfDataset.get(position).imgbit.getHeight()));
        holder.imageView.setImageBitmap(BitmapProcessing.applyEffects(sfDataset.get(position).imgbit,
                sfDataset.get(position).brightness,
                sfDataset.get(position).contrast,
                sfDataset.get(position).saturation,
                sfDataset.get(position).fade,
                sfDataset.get(position).temperature,
                sfDataset.get(position).tint,
                sfDataset.get(position).vignette,
                sfDataset.get(position).grain));
        holder.filterTitle.setText(sfDataset.get(position).title);
        if (filter_likes > 999999)
            holder.filterLikes.setText(Double.parseDouble(String.format("%.2f",(double) filter_likes / 1000000)) + "M times used");
        else if (filter_likes > 999)
            holder.filterLikes.setText(Double.parseDouble(String.format("%.2f",(double) filter_likes / 1000)) + "k times used");
        else if (filter_likes <= 1)
            holder.filterLikes.setText(filter_likes + " time used");
        else
            holder.filterLikes.setText(filter_likes + " times used");
        for(int i = 0; i < hashtags.length; i++)
            filter_hashtags += "#"+hashtags[i]+" ";
        holder.filterHashtags.setText(filter_hashtags);
        holder.filterMadeby.setText(sfDataset.get(position).madeby);
        Log.e("brightness", atr_brightness);
        Log.e("brightness", sfDataset.get(position).brightness+"");

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content = LayoutInflater.from(mContext).inflate(R.layout.sf_item_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setView(content);
                builder.setTitle(title.toUpperCase());
                builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        useAsyncTask UAT = new useAsyncTask();
                        UAT.execute(filter_id);
                        MainActivity.saveEditedImage(BitmapProcessing.applyEffects(
                                sfDataset.get(bitposition).realimgbit,
                                sfDataset.get(bitposition).brightness,
                                sfDataset.get(bitposition).contrast,
                                sfDataset.get(bitposition).saturation,
                                sfDataset.get(bitposition).fade,
                                sfDataset.get(bitposition).temperature,
                                sfDataset.get(bitposition).tint,
                                sfDataset.get(bitposition).vignette,
                                sfDataset.get(bitposition).grain));
                    }
                });
                builder.setNegativeButton("CANCEL", null);
                builder.setCancelable(true);
                builder.show();

                TextView brightness = (TextView) content.findViewById(R.id.brightness);
                brightness.setText(atr_brightness);
                TextView contrast = (TextView) content.findViewById(R.id.contrast);
                contrast.setText(atr_contrast);
                TextView saturation = (TextView) content.findViewById(R.id.saturation);
                saturation.setText(atr_saturation);
                TextView fade = (TextView) content.findViewById(R.id.fade);
                fade.setText(atr_fade);
                TextView temperature = (TextView) content.findViewById(R.id.temperature);
                temperature.setText(atr_temperature);
                TextView tint = (TextView) content.findViewById(R.id.tint);
                tint.setText(atr_tint);
                TextView vignette = (TextView) content.findViewById(R.id.vignette);
                vignette.setText(atr_vignette);
                TextView grain = (TextView) content.findViewById(R.id.grain);
                grain.setText(atr_grain);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sfDataset.size();
    }

    private JSONObject ifuseValidated(String filter_id) throws Exception{

        RequestBody formBody = new FormBody.Builder()
                .add("device_id", android_id)
                .add("filter_id", filter_id)
                .build();
        Request request = new Request.Builder()
                .url("http://52.52.31.137/API/used_filter.php")
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

    private class useAsyncTask extends AsyncTask<String, Void, JSONObject> {
        JSONObject res = null;
        public useAsyncTask() {
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try{
                res = ifuseValidated(params[0]);
            }catch(Exception e){
                e.printStackTrace();
            }
            return res;
        }
        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            JSONObject jsonobject = result;
            Log.e("result", result+"");
        }

        protected JSONObject getres(){
            return res;
        }
    }
}

class SFData{
    public Bitmap imgbit;
    public Bitmap realimgbit;
    public String title;
    public int likes;
    public String[] hashtags;
    public String madeby;
    public int brightness;
    public int contrast;
    public int saturation;
    public int fade;
    public int temperature;
    public int tint;
    public int vignette;
    public int grain;
    public int filter_id;
    public SFData(Bitmap imgbit, Bitmap realimgbit, String title, int likes, String[] hashtags, String madeby,
                  int brightness, int contrast, int saturation, int fade, int temperature,
                  int tint, int vignette, int grain, int filter_id){
        this.imgbit = imgbit;
        this.realimgbit = realimgbit;
        this.title = title;
        this.likes = likes;
        this.hashtags = hashtags;
        this.madeby = madeby;
        this.brightness = brightness;
        this.contrast = contrast;
        this.saturation = saturation;
        this.fade = fade;
        this.temperature = temperature;
        this.tint = tint;
        this.vignette = vignette;
        this.grain = grain;
        this.filter_id = filter_id;
    }
}