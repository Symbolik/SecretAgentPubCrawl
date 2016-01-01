package ca.mixitmedia.sapc.Tools;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;

import ca.mixitmedia.sapc.R;
import ca.mixitmedia.sapc.WeaverActivity;

public class MinigameConnectFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private enum POST_MODE{
        GENERATE_CODE,
        CHALLENGE,

    }
    private RequestQueue reqQueue;
    private WeaverActivity mainActivity;
    //private Spinner [] spinners;
    //private Spinner spinner01;
    private Button challengeBtn;
    private Button backspaceBtn;
    private Button [] numpad;
    private TextView [] usercodeNumbers;

    //private ArrayAdapter<CharSequence> adapter;
    private SharedPreferences userPrefs;
    private TextView txtview;
    private int toUserCode;
    private int curNum;



    private boolean isValidUser = false;
    //wait until HTTP response is recieved before swapping fragment
    private boolean isChallengeIssued = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_minigame_connect, container, false);

        mainActivity = (WeaverActivity) getActivity();
        txtview = (TextView) view.findViewById(R.id.agentNumText);



        reqQueue = VolleySingleton.GetInstance(mainActivity.getApplicationContext()).GetRequestQueue();

        //load user preferences
        userPrefs = mainActivity.getSharedPreferences(WeaverActivity.PREFS_NAME, 0);



        //uncomment to clear the preferences for testing..
        //SharedPreferences.Editor editor = userPrefs.edit();
        //editor.clear();
        //editor.commit();


        //load code from SharedPreferences or get new code from the server if default value is used
        mainActivity.from_usercode = userPrefs.getInt("user_id", 999999);

        //post request for new user_id if default value was assigned, create new entry in db table
        if(mainActivity.from_usercode == 999999){
            Log.d("BN", "invalid user code, get new user code");
            //startStringPostRequest("http://www.mixitmedia.ca/api/users", POST_MODE.GENERATE_CODE);
            startStringPostRequest("http://www.erraticgames.com/slimproj/index.php/api/users", POST_MODE.GENERATE_CODE);

        }
        else{
            Log.d("BN", "the agent id was found in the user preferences");
            SetAgentText();

        }





        //bn: spinner code, access code was entered using 4 spinners, no longer being used
        /*
        int [] spinnerIds = {R.id.usercode_spinner1, R.id.usercode_spinner2, R.id.usercode_spinner3, R.id.usercode_spinner4};
        spinners = new Spinner[4];

        adapter = ArrayAdapter.createFromResource(mainActivity, R.array.usercode_spinner, R.layout.spinner_item);

        //layout to use for drop down choices
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        for(int i = 0; i < spinners.length;i++){
            spinners[i] = (Spinner) view.findViewById(spinnerIds[i]);

            //use string resource array and apply a default layout for the spinner
            spinners[i].setAdapter(adapter);


            //reset spinner values
            spinners[i].setSaveEnabled(false); //fixes issue with values not resetting to default
            spinners[i].setSelection(0);

            //spinners[i].setOnItemSelectedListener(this);
        }*/

        curNum = 0;
        challengeBtn = (Button) view.findViewById(R.id.challengeBtn);
        backspaceBtn = (Button) view.findViewById(R.id.backspacebtn);

        usercodeNumbers = new TextView[4];
        usercodeNumbers[0] = (TextView) view.findViewById(R.id.usercode_num1);
        usercodeNumbers[1] = (TextView) view.findViewById(R.id.usercode_num2);
        usercodeNumbers[2] = (TextView) view.findViewById(R.id.usercode_num3);
        usercodeNumbers[3] = (TextView) view.findViewById(R.id.usercode_num4);




        int [] buttonIDs = {R.id.zerobtn, R.id.onebtn, R.id.twobtn,
                             R.id.threebtn, R.id.fourbtn, R.id.fivebtn,
                             R.id.sixbtn, R.id.sevenbtn, R.id.eightbtn, R.id.ninebtn};


        //init Button array
        numpad = new Button[10];

        for(int i = 0; i < buttonIDs.length;i++){
           numpad[i] = (Button) view.findViewById(buttonIDs[i]);

            numpad[i].setOnClickListener(new View.OnClickListener() {
                public void onClick(View v){
                    Button tmpBtn = (Button) v;
                    usercodeNumbers[curNum].setText(((Button) v).getText());
                    if(curNum < 3)
                        curNum++;
                }
            });

        }

        backspaceBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(curNum > 0){
                    if(curNum == usercodeNumbers.length-1 && !usercodeNumbers[curNum].getText().equals("")){
                        usercodeNumbers[curNum].setText("");
                    }
                    else {
                        curNum--;
                        usercodeNumbers[curNum].setText("");
                    }
                }
            }
        });

        /*the user code is put together and stored in the main activity, the actual
        * minigame fragment itself will read from the static var*/
        challengeBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //TODO: don't attempt to challenge if the user didn't get an access code, warning msg: this requires internet connection

                StringBuilder sb = new StringBuilder(usercodeNumbers[0].getText().toString())
                        .append(usercodeNumbers[1].getText())
                        .append(usercodeNumbers[2].getText())
                        .append(usercodeNumbers[3].getText());

               //handle if nothing was entered
                if(sb.toString().equals("")){
                    sb.append("0");
                }


                toUserCode = Integer.valueOf(sb.toString());

                //bn: dec20,2015, commenting out for now while testing erraticgames db
                //verify that the entered to_usercode exists and start the challenge if ok
                //startStringGetRequest("http://www.mixitmedia.ca/api/users/"+toUserCode);

                Log.d("BN","challenge btn pressed, toUserCode = " + toUserCode);
                //startStringGetRequest("http://www.erraticgames.com/slimproj/index.php/api/users/" + toUserCode);
                Log.d("BN","http://www.erraticgames.com/slimproj/index.php/api/user?user_id=" + toUserCode);
                startStringGetRequest("http://www.erraticgames.com/slimproj/index.php/api/user?user_id=" + toUserCode);




                //bn:dec 31, todo, should post a new challenge request to server, then go to waiting screen,
                //once challenge has been accepted on the other side, we can swap to the minigame fragment


                //bn:dec 31, 2015, don't start challenge unless opponent id has been found
                //FOR OFFLINE DEBUG TESTING ONLY, TODO: don't swap until challenge has been accepted
                //swap with minigame fragment
                //mainActivity.to_usercode = toUserCode;
               // Tools.directlySwapTo(Tools.minigameFragment);
            }
        });


        /*
        Log.d("BN", "starting the get request now!");
        //bn: dec 20, 2015, try to connect to erraticgames.com using REST Api
        //todo: edit rest api so the url looks like: erraticgames.com/api/users,
        //if you put the index.php into the root public folder, you can omit the filename in the url
        startStringGetRequest("http://www.erraticgames.com/slimproj/index.php/api/users");
        */

        return view;
    }



    private void startStringPostRequest(String url, final POST_MODE postMode){
        StringRequest stringReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {


                        if(postMode == POST_MODE.GENERATE_CODE) {
                            //rest api used:  startStringPostRequest("http://www.mixitmedia.ca/api/users", POST_MODE.GENERATE_CODE);
                            //save the new user code
                             SharedPreferences.Editor editor = userPrefs.edit();

                            Log.d("BN", "generate id, onResponse: " + response);

                            try{
                                int tmp = Integer.parseInt(response);
                                Log.d("BN", "checking if value is parsed = " + tmp);

                                //commented out for now while testing erraticgames db
                                editor.putInt("user_id", Integer.parseInt(response));
                                editor.commit();

                                //get the new id from userPrefs!
                                mainActivity.from_usercode = userPrefs.getInt("user_id", 999999);
                                SetAgentText();


                            }catch (NumberFormatException err){
                                Log.d("BN", "Unable to parse response to Integer!");
                            }
                        }
                        else if(postMode == POST_MODE.CHALLENGE){
                            Log.d("BN", "Challenge POST Response Received: swap fragments!");


                            //Swap fragments when HTTP Response is received!
                            Tools.directlySwapTo(Tools.minigameFragment);

                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error){

                Log.d("BN", "string post error: " + error.getMessage());

                //txtview2.setText("BN: Response Error!");
            }


        }){

            @Override
            public byte[] getBody()  {
                String postData;
                //don't post any new data, instead create an new empty user
                if(postMode == POST_MODE.GENERATE_CODE) {
                    Log.d("BN", "getBody NULL, new agent!");
                    return null;
                }
                else if(postMode == POST_MODE.CHALLENGE){
                    Log.d("BN", "getBody NULL, challenge sent");
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

        stringReq.setTag(SecretAgentMiniGame.VOLLEY_TAG);
        reqQueue.add(stringReq);


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        //adapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void SetAgentText(){
        Log.d("BN", " stored agentID = " + mainActivity.from_usercode);
        txtview.setText("Secret Agent# " + mainActivity.from_usercode);
    }


    private void startStringGetRequest(String url){
        //Request a string response from the provided URL.
        StringRequest stringReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){

            @Override
            public void onResponse(String response){
                //gameMsgTxtView.setText("Response is: " + response);
                Log.d("BN", "GET request response: " + response);


                if(response.equals("[]")){
                    //bn: this prints out when trying to find a user fails
                    Log.d("BN", "user was not found! enter agent id again!");
                }
                else {

                    Log.d("BN", "user was found, waiting for challenge to be accepted!");
                    mainActivity.to_usercode = toUserCode; //store value in mainactivity so it can be used globally

                    String challengeUrl = "";
                    startStringPostRequest(challengeUrl, POST_MODE.CHALLENGE);

                    /*
                    ///bn: dec20, comment out for now, reeanble later
                    mainActivity.to_usercode = toUserCode;


                    //valid user found! post the challenge!
                    String challengeUrl = "http://www.mixitmedia.ca/api/challenge/" + mainActivity.from_usercode + "/" + toUserCode;
                    Log.d("BN", "Challenge POST: " + challengeUrl);
                    startStringPostRequest(challengeUrl, POST_MODE.CHALLENGE);
                    */

                }

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Log.d("BN", "ConnectFragment, GET FAILED!" + error);
            }
        });


        //Add the request to the RequestQueue otherwise the request will never be sent out
        stringReq.setTag(SecretAgentMiniGame.VOLLEY_TAG);
        reqQueue.add(stringReq);

    }

    @Override
    public void onStop(){
        super.onStop();

        //cancel all requests
        if(reqQueue != null)
            reqQueue.cancelAll(SecretAgentMiniGame.VOLLEY_TAG);
    }
}
