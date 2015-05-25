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
import org.w3c.dom.Text;

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
    public enum GameItem{
        NONE,
        GUN,
        INTEL,
        POISON,
    }

    public enum CountdownTask{
        GAME_INIT,
        GAMEPLAY,
        WAIT_NEXT_ROUND,
    }

    public static final String VOLLEY_TAG = SecretAgentMiniGame.class.getSimpleName();

    private WeaverActivity mainActivity;
    private TextView versusTxtView;
    private TextView gameMsgTxtView;
    private TextView scoreview_p1;
    private TextView scoreview_p2;
    private TextView gametimerTxtView;
    private RequestQueue reqQueue;
    private ImageButton gunBtn;
    private ImageButton intelBtn;
    private ImageButton poisonBtn;

    private Map<String, String> params = new HashMap<String, String>();
    private CountDownTimer countdownTimer;

    private int agentID;
    private int opponentID;
    private GameItem selected = GameItem.NONE;

    private int p1score;
    private int p2score;
    private int gameRound;
    private boolean isGameOver;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_minigame, container, false);

        mainActivity = (WeaverActivity) getActivity();

        //btn = (Button) view.findViewById(R.id.button);
        versusTxtView = (TextView) view.findViewById(R.id.versusTextView);
        gameMsgTxtView = (TextView) view.findViewById(R.id.gamemsgTextView);
        gametimerTxtView = (TextView) view.findViewById(R.id.gametimerTextView);
        scoreview_p1 = (TextView) view.findViewById(R.id.scoretext01);
        scoreview_p2 = (TextView) view.findViewById(R.id.scoretext02);

        gunBtn = (ImageButton) view.findViewById(R.id.gunButton);
        intelBtn = (ImageButton) view.findViewById(R.id.intelButton);
        poisonBtn = (ImageButton) view.findViewById(R.id.poisonButton);
        selected = GameItem.NONE;

        isGameOver=false;
        gameRound=1;
        p1score=0;
        p2score=0;

        //disable game buttons at the start
        ToggleClickableItems(false);

        //load user preferences
       //SharedPreferences userPref = mainActivity.getSharedPreferences(WeaverActivity.PREFS_NAME, 0);

        reqQueue = VolleySingleton.GetInstance(mainActivity.getApplicationContext()).GetRequestQueue();


        //uncomment to clear shared preferences
       // userPref.edit().clear().commit();

        //TODO:if this is the to_user, then these values will have to be reversed
        agentID = mainActivity.from_usercode;
        opponentID = mainActivity.to_usercode;
        SetAgentText();


        InitializeGameTimer();


        //handle button clicks
        View.OnClickListener clickListener = new View.OnClickListener(){
            @Override
            public void onClick(View v){

                if(v==gunBtn){
                    Log.d("BN", "View is GUN!");
                    v.setSelected(true);
                    selected = GameItem.GUN;
                    intelBtn.setSelected(false);
                    poisonBtn.setSelected(false);
                    //startJsonGetRequest();
                    //startStringPostRequest();


                }
                else if(v==intelBtn){
                    Log.d("BN", "View is INTEL!");
                    v.setSelected(true);
                    selected = GameItem.INTEL;
                    gunBtn.setSelected(false);
                    poisonBtn.setSelected(false);
                    //startStringGetRequest("http://www.mixitmedia.ca/api/users");
                    //startStringGetRequest("http://www.mixitmedia.ca/api/users/9529");
                    //startStringGetRequest("http://www.mixitmedia.ca/api/challenge/9529");


                    String challengeUrl = "http://www.mixitmedia.ca/api/challenge/" + mainActivity.from_usercode + "/" + mainActivity.to_usercode;
                    Log.d("BN","Challenge GET: " + challengeUrl);
                    startStringGetRequest(challengeUrl);


                    //startJsonPostRequest();
                }
                else if(v==poisonBtn){
                    Log.d("BN", "View is POISON!");
                    v.setSelected(true);
                    selected = GameItem.POISON;
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



        return view;
    }


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

        countdownTimer = null;
        //cancel all requests
        if(reqQueue != null)
            reqQueue.cancelAll(VOLLEY_TAG);
    }




    private void startStringGetRequest(String url){
        reqQueue = VolleySingleton.GetInstance(mainActivity.getApplicationContext()).GetRequestQueue();

       //Request a string response from the provided URL.
       StringRequest stringReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
           @Override
           public void onResponse(String response){
               //gameMsgTxtView.setText("Response is: " + response);
               Log.d("BN", response);

           }
       }, new Response.ErrorListener(){
           @Override
           public void onErrorResponse(VolleyError error){
               //gameMsgTxtView.setText("That didn't work!");
           }
       });

       //Add the request to the RequestQueue.
       stringReq.setTag(VOLLEY_TAG);
       reqQueue.add(stringReq);

    }

    private void startStringPostRequest(String url){
        reqQueue = Volley.newRequestQueue(mainActivity);

        StringRequest stringReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                       //gameMsgTxtView.setText("Response: " + response.toString());
                        Log.d("BN", "onResponse-" + response.toString());



                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error){
                VolleyLog.d(VOLLEY_TAG, "Error: " + error.getMessage());
                Log.d("BN", "string post error: " + error.getMessage());

                //gameMsgTxtView.setText("BN: Response Error!");
            }


        }){

            @Override
            public byte[] getBody()  {

               String postData;



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

        stringReq.setTag(VOLLEY_TAG);
        reqQueue.add(stringReq);


    }


    private void SetAgentText(){
        Log.d("BN", "valid ID was saved! agentID = " + agentID);
        versusTxtView.setText("AGENT# " + agentID + " \nVS" + " \nAGENT# " + opponentID);
        scoreview_p1.setText(Integer.toString(p1score));
        scoreview_p2.setText(Integer.toString(p2score));

    }



    //initial wait before game starts
    private void InitializeGameTimer(){
        gameMsgTxtView.setText("ROUND " + gameRound + "\nGET READY!");
        gametimerTxtView.setVisibility(View.INVISIBLE);
        ToggleClickableItems(false);
        gunBtn.setSelected(false);
        intelBtn.setSelected(false);
        poisonBtn.setSelected(false);
        selected = GameItem.NONE;

        if(countdownTimer == null) {

            countdownTimer = new CountDownTimer(2200, 2200) {

                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    //display win/lose message
                    Log.d("BN", "Countdown finished");
                    ToggleClickableItems(true);
                    //gameMsgTxtView.setVisibility(View.INVISIBLE);
                    gameMsgTxtView.setText("CHOOSE YOUR TACTIC!");
                    gametimerTxtView.setVisibility(View.VISIBLE);
                    countdownTimer = null;
                    StartGameTimer();
                }
            }.start();
        }
    }



    private void StartGameTimer(){

        if(countdownTimer == null) {

            //intelBtn.setVisibility(View.INVISIBLE);
            //update ontick often otherwise timer display will lag
            countdownTimer = new CountDownTimer(6000, 10) {

                public void onTick(long millisUntilFinished) {
                    gametimerTxtView.setText("" + millisUntilFinished / 1000);
                }

                public void onFinish() {
                    //display win/lose message
                    Log.d("BN", "Countdown finished");
                    ToggleClickableItems(false);
                    gameMsgTxtView.setVisibility(View.INVISIBLE);
                    gametimerTxtView.setText("0");
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
                    String winmsg;


                    if(selected == GameItem.NONE){
                        //winmsg = "Nothing Selected, You Were Assassinated!";
                        p2score++;
                        if(p2score == 3) {
                            winmsg = "You Were Assassinated!";
                            isGameOver = true;
                        }
                        else{
                            winmsg = "Nothing Was Selected.\n Agent #" + opponentID + " Wins The Round";
                        }
                    }
                    else {
                        Random r = new Random();

                        winmsg = (r.nextBoolean()) ? "Assassination Successful!" : " You Were Assassinated!";
                        if(r.nextBoolean()){
                            p1score++;
                            if(p1score == 3) {
                                winmsg = "Assassination Successful\nYou Win!";
                                isGameOver = true;
                            }
                            else {
                                winmsg = "Agent #" + agentID + " Wins The Round";
                            }
                        }
                        else{
                            p2score++;
                            if(p2score == 3) {
                                winmsg = "winmsg = \"You Were Assassinated!";
                                isGameOver = true;
                            }
                            else {
                                winmsg = "Agent #" + opponentID + " Wins The Round";
                            }
                        }

                    }

                    if(isGameOver) {
                        gametimerTxtView.setVisibility(View.INVISIBLE);
                    }

                    scoreview_p1.setText(Integer.toString(p1score));
                    scoreview_p2.setText(Integer.toString(p2score));

                    gameMsgTxtView.setText(winmsg);
                    gameMsgTxtView.setVisibility(View.VISIBLE);

                    countdownTimer = null;
                    NextRoundTimer();
                }
            }.start();
        }
    }


    private void NextRoundTimer(){

        if(countdownTimer == null) {


            countdownTimer = new CountDownTimer(2500, 2500) {

                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {


                    countdownTimer = null;

                    if(!isGameOver) {
                        gameRound++;
                        InitializeGameTimer();


                    }


                }
            }.start();
        }
    }



    private void ToggleClickableItems(boolean toggle){
        gunBtn.setClickable(toggle);
        intelBtn.setClickable(toggle);
        poisonBtn.setClickable(toggle);
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
                        gameMsgTxtView.setText("Response: " + response.toString());
                        Log.d("BN", "onResponse-" + response.toString());

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error){
                VolleyLog.d(VOLLEY_TAG, "Error: " + error.getMessage());
                gameMsgTxtView.setText("BN: Response Error! " + error.getMessage());
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
                    //gameMsgTxtView.setText("Response: " + response.toString());
                    /*
                    try {
                        gameMsgTxtView.setText("Response: " + response.getString("title"));
                        Log.d("BN", "onResponse-" + response.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/


                }
            }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error){
                VolleyLog.d(VOLLEY_TAG, "Error: " + error.getMessage());
                Log.d("BN:", "JSON GET ERROR RESPONSE-" + error.getMessage());
                //gameMsgTxtView.setText("BN: JSON GET Response Error!");
            }


        });

        reqQueue.add(jsonObjReq);

    }







}










