package team16.filtershare;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by harrykim on 2016. 7. 13..
 */
public class ServerConnector {

    public static JSONObject uploadToServer(JSONObject jsonobj, String url_tail) throws IOException, JSONException {
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
    }

    public static JSONObject GetFromServer(String url_tail) throws IOException, JSONException {
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
    }
}