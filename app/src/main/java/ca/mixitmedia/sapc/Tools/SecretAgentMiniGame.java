package ca.mixitmedia.sapc.Tools;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import ca.mixitmedia.sapc.R;
import ca.mixitmedia.sapc.WeaverActivity;
import java.util.Random;


/**
 * Created by Barry N on 2015-04-16.
 *
 */
public class SecretAgentMiniGame extends Fragment {

    public static final String TAG = SecretAgentMiniGame.class.getSimpleName();

    private WeaverActivity mainActivity;
    private TextView txtview;
    private TextView txtview2;
    private TextView gametimerView;
    private RequestQueue reqQueue;
    private ImageButton gunBtn;
    private ImageButton intelBtn;
    private ImageButton poisonBtn;

    private Map<String, String> params = new HashMap<String, String>();
    private CountDownTimer countdownTimer;
    private int agentID;
    //private String agentID;
    private boolean isGenerateID = false;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_minigame, container, false);

        mainActivity = (WeaverActivity) getActivity();

        //btn = (Button) view.findViewById(R.id.button);
        txtview = (TextView) view.findViewById(R.id.textView);
        txtview2 = (TextView) view.findViewById(R.id.textView2);
        gametimerView = (TextView) view.findViewById(R.id.gametimerView);

        gunBtn = (ImageButton) view.findViewById(R.id.gunButton);
        intelBtn = (ImageButton) view.findViewById(R.id.intelButton);
        poisonBtn = (ImageButton) view.findViewById(R.id.poisonButton);


        //load user preferences
        SharedPreferences userPref = mainActivity.getSharedPreferences(WeaverActivity.PREFS_NAME, 0);

        //uncomment to clear shared preferences
       // userPref.edit().clear().commit();

        agentID = userPref.getInt("access_code", 999999);


        if(agentID == 999999){
            Log.d("BN", "invalid agentID, get new ID");
            isGenerateID = true;
            startStringPostRequest("http://www.mixitmedia.ca/api/users");
        }
        else{
            SetAgentText();

        }





        //handle button clicks
        View.OnClickListener clickListener = new View.OnClickListener(){
            @Override
            public void onClick(View v){

                if(v==gunBtn){
                    Log.d("BN", "View is GUN!");
                    v.setSelected(true);
                    intelBtn.setSelected(false);
                    poisonBtn.setSelected(false);
                    //startJsonGetRequest();
                    //startStringPostRequest();


                }
                else if(v==intelBtn){
                    Log.d("BN", "View is INTEL!");
                    v.setSelected(true);
                    gunBtn.setSelected(false);
                    poisonBtn.setSelected(false);
                    startStringGetRequest("http://www.mixitmedia.ca/api/users");
                    startStringGetRequest("http://www.mixitmedia.ca/api/users/9529");

                    //startJsonPostRequest();
                }
                else if(v==poisonBtn){
                    Log.d("BN", "View is POISON!");
                    v.setSelected(true);
                    gunBtn.setSelected(false);
                    intelBtn.setSelected(false);
                   // startStringGetRequest();
                }
            }

            //Setup up Volley Request after countdown finishes
        };

        gunBtn.setOnClickListener(clickListener);

        intelBtn.setOnClickListener(clickListener);

        poisonBtn.setOnClickListener(clickListener);

        /*
        //code for button that has now been removed!
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
           public void onClick(View v){
                txtview.setText(editTxt.getText().toString());

               //startStringGetRequest("http://www.mixitmedia.ca/api/users");
                //startJsonGetRequest("http://192.168.56.1:3000/posts/1");


                //startJsonPostRequest("http://192.168.56.1:3000/posts");

                //startStringPostRequest("http://www.mixitmedia.ca/api/users");
                //id:4331


                startStringGetRequest("http://www.mixitmedia.ca/api/users");
                startStringGetRequest("http://www.mixitmedia.ca/api/challenges");
                startStringGetRequest("http://www.mixitmedia.ca/api/location");
                //8880
           }
        });*/

        return view;
    }


    //Example of editing sharedpreferences
    /*
    @Override
    protected void onStop(){
       super.onStop();

      // We need an Editor object to make preference changes.
      // All objects are from android.context.Context
      SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
      SharedPreferences.Editor editor = settings.edit();
      editor.putBoolean("silentMode", mSilentMode);

      // Commit the edits!
      editor.commit();
    }
    * */


    /*
    private void ProcessSelection(){
        if(selection == gunBtn)
            gunBtn.setSelected(!gunBtn.isSelected());

    }*/


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onStop(){
        super.onStop();

        //cancel all requests
        if(reqQueue != null)
            reqQueue.cancelAll(TAG);
    }




    private void startStringGetRequest(String url){
        reqQueue = Volley.newRequestQueue(mainActivity);
       // String url = "http://www.thomas-bayer.com/sqlrest/CUSTOMER/502";

       //Request a string response from the provided URL.
       StringRequest stringReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
           @Override
           public void onResponse(String response){
               //txtview2.setText("Response is: " + response);
               Log.d("BN", response);

           }
       }, new Response.ErrorListener(){
           @Override
           public void onErrorResponse(VolleyError error){
               //txtview2.setText("That didn't work!");
           }
       });

       //Add the request to the RequestQueue.
       reqQueue.add(stringReq);


    }

    private void startStringPostRequest(String url){
        reqQueue = Volley.newRequestQueue(mainActivity);
        //String url = "http://www.thomas-bayer.com/sqlrest/CUSTOMER";
        //JSON post request

        StringRequest stringReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                       //txtview2.setText("Response: " + response.toString());
                        Log.d("BN", "onResponse-" + response.toString());

                        if(isGenerateID) {
                            //save the agent ID
                            SharedPreferences userPref = mainActivity.getSharedPreferences(WeaverActivity.PREFS_NAME, 0);
                            SharedPreferences.Editor editor = userPref.edit();


                            try{
                                int tmp = Integer.parseInt(response);
                                editor.putInt("access_code", Integer.parseInt(response));
                                editor.commit();

                                //get the new id from userPrefs!
                                agentID = userPref.getInt("access_code", 999999);
                                SetAgentText();


                            }catch (NumberFormatException err){
                                Log.d("BN", "Unable to parse response to Integer!");
                            }
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error){
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Log.d("BN", "string post error: " + error.getMessage());

                //txtview2.setText("BN: Response Error!");
            }


        }){

            @Override
            public byte[] getBody()  {

               String postData;
                //don't post any new data, instead create an new empty user
                if(isGenerateID) {
                    Log.d("BN", "getBody NULL, new agent!");
                    return null;
                }


                postData = "<firstname>barry</firstname>";
                /*String postData = "<CUSTOMER xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" +
                "  <ID>502</ID>\n" +
                "  <FIRSTNAME>Hideo</FIRSTNAME>\n" +
                "  <LASTNAME>Kojima</LASTNAME>\n" +
                "  <STREET>294 Seventh Av.</STREET>\n" +
                "  <CITY>Paris</CITY>\n" +
                "</CUSTOMER>"; */

                 Log.d("BN", "getBody-" + postData);


                try{
                    return postData.getBytes(getParamsEncoding());

                } catch (UnsupportedEncodingException uee) {
                    // TODO consider if some other action should be taken
                    return null;
                }
            }
            @Override
            public String getBodyContentType() {
                Log.d("BN", "getBodyContentType-" + getParamsEncoding());
                return "application/x-www-form-urlencoded; charset=" +
                        getParamsEncoding();
            }

        };

        stringReq.setTag(TAG);
        reqQueue.add(stringReq);


    }

    private void startJsonPostRequest(String url){
        reqQueue = Volley.newRequestQueue(mainActivity);


        //String url = "http://192.168.42.199:3000/posts";

        /*
        //JSON post request
        if(jsonbody == null) {
            Log.d("BN", "jsonbody not set! cannot complete post request!");
            return;
        }*/

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        txtview2.setText("Response: " + response.toString());
                        Log.d("BN", "onResponse-" + response.toString());

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error){
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                txtview2.setText("BN: Response Error! " + error.getMessage());
            }


        });/*{
            @Override
            public  Map<String, String> getParams() throws AuthFailureError{
                Map<String,String> params = new HashMap<String, String>();
                params.put("title", "hello");
                params.put("author", "world");

                //String element1 = (String) params.get("author");
                //Log.d("BN", "getParams-" + element1);

                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                Log.d("BN", "getHeaders-" + getParamsEncoding());
                Log.d("BN", "getHeaders-" + params.get("Content-Type"));
                return params;

            }
        };*/

        reqQueue.add(jsonObjReq);

    }

    private void startJsonGetRequest(String url){
        reqQueue = Volley.newRequestQueue(mainActivity);
       // String url = "http://jsonplaceholder.typicode.com/posts/10";
        //JSON post request
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>(){
                @Override
                public void onResponse(JSONObject response) {
                    //txtview2.setText("Response: " + response.toString());
                    /*
                    try {
                        txtview2.setText("Response: " + response.getString("title"));
                        Log.d("BN", "onResponse-" + response.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/


                }
            }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error){
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Log.d("BN:", "JSON GET ERROR RESPONSE-" + error.getMessage());
                //txtview2.setText("BN: JSON GET Response Error!");
            }


        });

        reqQueue.add(jsonObjReq);

    }


    private void SetAgentText(){
        Log.d("BN", "valid ID was saved! agentID = " + agentID);
        txtview.setText("AGENT# " + agentID);
        StartGameTimer();

    }

    private void StartGameTimer(){
        if(countdownTimer == null) {

            //intelBtn.setVisibility(View.INVISIBLE);
            countdownTimer = new CountDownTimer(4000, 100) {

                public void onTick(long millisUntilFinished) {
                    gametimerView.setText("" + millisUntilFinished / 1000);
                    Log.d("BN", "gametime: " + millisUntilFinished/1000 + ", " + millisUntilFinished);
                }

                public void onFinish() {
                    //display win/lose message
                    Log.d("BN", "Countdown finished");
                    //intelBtn.setVisibility(View.VISIBLE);
                    gametimerView.setText("0");
                    countdownTimer = null;
                    ShowWinner();

                }
            }.start();
        }
    }


    //display win/lose game message after a few seconds
    private void ShowWinner(){
        if(countdownTimer == null) {
            countdownTimer = new CountDownTimer(500, 500) {

                public void onTick(long millisUntilFinished) {}

                public void onFinish() {
                    //display win/lose message
                    Log.d("BN", "Countdown finished");
                    //intelBtn.setVisibility(View.VISIBLE);

                    Random r = new Random();

                    String winmsg = (r.nextBoolean())?"Assassination Successful!":" You Were Assassinated!";

                    gametimerView.setText(winmsg);
                    countdownTimer = null;

                }
            }.start();
        }
    }




}










