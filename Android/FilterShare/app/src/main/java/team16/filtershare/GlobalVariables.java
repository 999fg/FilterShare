package team16.filtershare;

import android.app.Application;
import android.content.Context;

/**
 * Created by harrykim on 2016. 7. 9..
 */

//Application needed to save global variables, you need to get application context in the activities you want to use global variables.
public class GlobalVariables extends Application {

    private String picture_path;
    private static Context context;

    public void onCreate() {
        super.onCreate();
        GlobalVariables.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return GlobalVariables.context;
    }

    public String get_picture_path(){
        return picture_path;
    }

    public void set_picture_path(String new_path){
        picture_path = new_path;
    }


}