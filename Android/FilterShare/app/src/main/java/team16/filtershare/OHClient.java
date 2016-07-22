package team16.filtershare;

import java.io.IOException;

import okhttp3.OkHttpClient;

/**
 * Created by shinjaemin on 2016. 7. 20..
 */
public class OHClient {
    private static OkHttpClient client;

    public static OkHttpClient getClient() throws IOException {
        if(client == null){
            client = new OkHttpClient();
        }
        return client;
    }
}