package team16.filtershare;

import android.util.Log;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

/**
 * Created by harrykim on 2016. 7. 13..
 */
public class ServerConnector {
    private static JsonObject server_result;

    public static JsonObject uploadToServer(JsonObject jsonobj, String url_tail) {


        //server_address = "domain address" + url tail
        //For example "http://52.52.31.137" + "API/share_filter.php"
        Ion.with(GlobalVariables.getAppContext())
                .load("http://52.52.31.137"+url_tail)
                .setJsonObjectBody(jsonobj)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        server_result = result;
                        Log.d("Post", result.toString());


                    }
                });
        return server_result;
        /*
        //server_address = "domain address" + url tail
        //For example "http://52.52.31.137" + "API/share_filter.php"
        String server_address = "http://52.52.31.137" + url_tail;
        Log.d("address", server_address);



        //String json = "{\"key\":1}";
        String json = jsonobj.toString();


        URL url = new URL(server_address);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("POST");

        OutputStream os = conn.getOutputStream();
        os.write(json.getBytes("UTF-8"));
        os.close();

        // read the response
        InputStream in = new BufferedInputStream(conn.getInputStream());

        BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();

        String inputStr;
        while ((inputStr = streamReader.readLine()) != null)
            responseStrBuilder.append(inputStr);
        JSONObject result =new JSONObject(responseStrBuilder.toString());

        in.close();
        conn.disconnect();

        return result;
        */
    }


    public static JsonObject GetFromServer(String url_tail) {
        //server_address = "domain address" + url tail
        //For example "http://52.52.31.137" + "API/share_filter.php"
        Ion.with(GlobalVariables.getAppContext())
                .load("http://52.52.31.137"+url_tail)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        server_result = result;
                        Log.d("Get", result.toString());


                    }
                });
        return server_result;



        /*
        //server_address = "domain address" + url tail
        //For example "http://52.52.31.137" + "API/share_filter.php"
        String server_address = "http://52.52.31.137" + url_tail;
        Log.d("address", server_address);


        URL url = new URL(server_address);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        conn.setDoOutput(false);
        conn.setDoInput(true);
        conn.setRequestMethod("GET");

        // read the response
        InputStream in = new BufferedInputStream(conn.getInputStream());

        BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();

        String inputStr;
        while ((inputStr = streamReader.readLine()) != null)
            responseStrBuilder.append(inputStr);
        JSONObject result = new JSONObject(responseStrBuilder.toString());

        in.close();
        conn.disconnect();

        return result;
        */
    }
}