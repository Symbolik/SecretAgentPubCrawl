package ca.mixitmedia.sapc.Tools;

import android.app.Application;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ca.mixitmedia.sapc.WeaverActivity;

/**
 * Created by Barry N on 2015-05-13.
 *
 * a single instance of a request queue that will last the lifetime of the app
 */
public class VolleySingleton {
    private static VolleySingleton instance = null;
    private RequestQueue reqQueue;
    private static Context appCtx;

    private VolleySingleton(Context context){

        // reqQueue = Volley.newRequestQueue(mainActivity);
        appCtx = context;
        reqQueue = Volley.newRequestQueue(appCtx);

    }

    public static VolleySingleton GetInstance(Context context){
        if(instance == null){
            instance = new VolleySingleton(context);
        }

        return instance;
    }


    public RequestQueue GetRequestQueue(){
        return reqQueue;
    }





}
