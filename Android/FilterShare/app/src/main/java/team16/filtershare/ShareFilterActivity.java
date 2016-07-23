package team16.filtershare;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

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

import org.ocpsoft.prettytime.PrettyTime;

public class ShareFilterActivity extends AppCompatActivity {

    OkHttpClient client;
    String android_id;
    int biggest;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<SFData> sfDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_filter);

        try {
            client = OHClient.getClient();
        } catch(IOException e) {
            e.printStackTrace();
        }
        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        GlobalVariables sfApp = ((GlobalVariables)getApplicationContext());
        String picturepath = sfApp.get_picture_path();
        final Bitmap bitimg = BitmapFactory.decodeFile(picturepath);

        sfDataset = new ArrayList<>();
        adapter = new SFAdapter(sfDataset);
        recyclerView.setAdapter(adapter);
        biggest = 0; //TODO: filter_biggest API to be implemented

        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if(biggest <= 45)
                    new queryAsyncTask(bitimg).execute(biggest+"");
                if(biggest == 45)
                    biggest = 46;
                else if (biggest > 40)
                    biggest = 45;
                else
                    biggest = biggest + 5;
            }
        });
        queryAsyncTask QAT = new queryAsyncTask(bitimg);
        QAT.execute(biggest+"");
        if(biggest == 45)
            biggest = 46;
        else if (biggest > 40)
            biggest = 45;
        else
            biggest = biggest + 5;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.filter_add) {
            Intent intent = new Intent(ShareFilterActivity.this, FilterMakingActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    private void addFilter(String name, String username, String[] tags, String usedCount, String date_created){
        sfDataset.
    }
    */

    private JSONObject ifqueryValidated(String data_paging) throws Exception{

        RequestBody formBody = new FormBody.Builder()
                .add("device_id", android_id)
                .add("data_paging", data_paging)
                .build();
        Request request = new Request.Builder()
                .url("http://52.52.31.137/API/filter_query.php")
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

    private class queryAsyncTask extends AsyncTask<String, Void, JSONObject> {
        JSONObject res = null;
        Bitmap bitimg = null;
        public queryAsyncTask(Bitmap img) {
            bitimg = img;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try{
                res = ifqueryValidated(params[0]);
            }catch(Exception e){
                e.printStackTrace();
            }
            return res;
        }
        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            JSONObject jsonobject = result;
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(jsonobject.optString("data"));
            } catch(JSONException e){
                e.printStackTrace();
            }
            PrettyTime p = new PrettyTime();
            for (int i = 0; i<jsonArray.length(); i++){
                JSONObject jo = null;
                JSONArray jotags = null;
                try {
                    jo = jsonArray.getJSONObject(i);
                    jotags = new JSONArray(jo.optString("tags"));
                } catch(JSONException e) {
                    e.printStackTrace();
                }
                Log.e("json", jo.optString("tags"));
                String[] tags = new String[jotags.length()];
                for (int j = 0; j<jotags.length(); j++){
                    try {
                        tags[j] = jotags.get(j) + "";
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                }

                sfDataset.add(new SFData(
                        bitimg,
                        jo.optString("name"),
                        22803,
                        tags,
                        jo.optString("username") + ", " + p.format(new Date(Long.parseLong(jo.optString("date_created"))))));
            }
            adapter.notifyItemInserted(0);
        }

        protected JSONObject getres(){
            return res;
        }
    }
}