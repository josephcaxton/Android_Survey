package uk.co.bbc.survey;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SurveySectionFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String WELCOMEMESSAGE = "Welcome to the BBC Survey app";
    public static final String INSTRUCTIONMESSAGE = "Use this app to check and submit surveys assigned to your account";
    private Handler mHandler = new Handler();
    public SurveySectionFragment() {
    }
    Context c;
    EditText UID;
    EditText Password;
    ProgressBar mProgress; 
    TextView mLoading;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
        View rootView = inflater.inflate(R.layout.fragment_main_survey, container, false);
        
       
        
        TextView surveyTextViewWelcome = (TextView) rootView.findViewById(R.id.survey_section_Welcomelabel);
        surveyTextViewWelcome.setText(WELCOMEMESSAGE);
        
        TextView surveyTextViewInstruction = (TextView) rootView.findViewById(R.id.survey_section_Instructionlabel);
        surveyTextViewInstruction.setText(INSTRUCTIONMESSAGE);
        
        UID = (EditText) rootView.findViewById(R.id.txtUserId);
        Password = (EditText) rootView.findViewById(R.id.txtPassword);
        
        mProgress = (ProgressBar)rootView.findViewById(R.id.mainpageprogressBar);
        mProgress.setIndeterminate(true);
        mProgress.setVisibility(View.INVISIBLE);
        mLoading = (TextView) rootView.findViewById(R.id.progressText);
        mLoading.setVisibility(View.INVISIBLE);
        
        c = getActivity();
        LocalCache cache = ((LocalCache)c.getApplicationContext());
        //cache.StoreAuthentication("joseph");
        if(!cache.CheckAuthenticationInPreference()){
        	// If user is already logged in then send them in
        	LoadSurveyFromLocalDatabase();
        	Intent intent = new Intent(c, SurveyListsActivity.class);
	    	
	    	startActivity(intent);
        }
        
        Button btnlogin =(Button) rootView.findViewById(R.id.btnlogin);
        btnlogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				// Check if we have access to the server on specified port, if so authenticate user.
				//Here i am getting the context from the activity this fragment is running on as activity is extends context
				
				 String UiD = "caxtoj02";//UID.getText().toString();
				 String PassWord = "letmein2";//Password.getText().toString();
				 
				 
				 // Check is user types in both username and password
				 if (UiD.matches("") || PassWord.matches("")){
					
					 clearUserNamePasswordBox();
					 LocalCache cache = ((LocalCache)c.getApplicationContext());
					 cache.ShowMessage(c, "Authentication failed","The uid or password you entered is invalid");
					 	
				 }
				 else
				 {
					 mProgress.setVisibility(View.VISIBLE);
						mLoading.setVisibility(View.VISIBLE);
				Thread SecondThread = new Thread(){
					public void run(){
						
						Authenticate();
							
						
					}
				};
				SecondThread.start();
				
				
				}}});
        
        
        return rootView;
    }
    
    private void Authenticate(){
    	// Check if we have access to the service first
    	// I will be using Httpclient object for better performance
    	LocalCache cache = ((LocalCache)c.getApplicationContext());
    	HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(cache.getServerURL() + "login");
		
		InputStream inputStream = null;
		String result = null;
		 try {
			 Inet4Address IPAddress = null;
			 IPAddress = (Inet4Address) InetAddress.getByName(cache.getReachabilityServerAddress());
			 int port = Integer.parseInt(cache.getReachabilityPort());
			 boolean isNetworkReachable = cache.GetReachability(IPAddress, port);
			 
			 if(!isNetworkReachable){
				 //___________________________________________________________________
				 // For what ever connection reason we cannot contact the service
				 ((Activity)c).runOnUiThread(new Runnable() {
				     public void run() {
				    	 LocalCache cache = ((LocalCache)c.getApplicationContext());
				    	 clearUserNamePasswordBox();
				    	 mProgress.setVisibility(View.INVISIBLE);
						 mLoading.setVisibility(View.INVISIBLE);
				    	 cache.ShowMessage(c, "Connection problem", "Server not reachable check your internet connection"); 
				     }
				});
				 throw new SocketException("Server not reachable check your internet connection");
			 }
			 //_____________________________________________________________________
			 // Login to the server here
			 String uuid = "caxtoj02"; //UID.getText().toString();
			 String uPassword = "letmein2";//Password.getText().toString();
			
			 List<NameValuePair> params = new ArrayList<NameValuePair>();
			 params.add(new BasicNameValuePair("uid", uuid));
			 params.add(new BasicNameValuePair("password", uPassword));
			
				httppost.setEntity(new UrlEncodedFormEntity(params));
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				
				 inputStream = entity.getContent();
				    // json is UTF-8 by default
				    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
				    StringBuilder sb = new StringBuilder();
				
				    String line = null;
				    while ((line = reader.readLine()) != null)
				    {
				        sb.append(line + "\n");
				    }
				    result = sb.toString();
				   boolean allowIn = ParseAthentication(result);
				    if (allowIn){
				    	//__________________________________________________________________________
				    	// Succesfully logged in now Get all surveys assigned to this user.
				    	//HttpClient SurveyListhttpclient = new DefaultHttpClient();
				    	cache.setLoggedinUID(uuid); // Store uid in memory
				    	cache.StoreAuthentication(uuid); // Store the time user sucessfully logged into preference
						HttpGet httpGetSurveyList = new HttpGet(cache.getServerURL() + "userSurveyList?uid=" + uuid);
						
						 httpGetSurveyList.addHeader("accept", "application/json");
						
						 HttpResponse SurveyListresponse = httpclient.execute(httpGetSurveyList);
						 HttpEntity SurveyListentity = SurveyListresponse.getEntity();
						 inputStream = null;
						 result = null;
						 inputStream = SurveyListentity.getContent();
						 
						 // json is UTF-8 by default
						    BufferedReader SurveyListreader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
						    StringBuilder SurveyListsb = new StringBuilder();
						
						    String SurveyListline = null;
						    
						    while ((SurveyListline = SurveyListreader.readLine()) != null)
						    {
						    	SurveyListsb.append(SurveyListline + "\n");
						    }
						    result = SurveyListsb.toString();
						    // ParseUserSurveyList and get all surveyItems -- Store into local Database
						  //___________________________________________________________________________
						    ParseUserSurveyList(result);
						     
						  //Load all Surveys from local database to cache
						  //___________________________________________________________________________
						    LoadSurveyFromLocalDatabase();
						    
				    	// Fire off the next activity to load SurveyList
					   //___________________________________________________________________
				    	 ((Activity)c).runOnUiThread(new Runnable() {
						     public void run() {
						 clearUserNamePasswordBox();
						 mProgress.setVisibility(View.INVISIBLE);
						 mLoading.setVisibility(View.INVISIBLE);
						 //Bundle basket = new Bundle();
						 //basket.putInt("HowManySurveysToList", counter);
				    	Intent intent = new Intent(c, SurveyListsActivity.class);
				    	//intent.putExtras(basket);
				    	startActivity(intent);
						     }
					});
				    }
				    else{
				    	// User has been denied after authentication
				    	 ((Activity)c).runOnUiThread(new Runnable() {
						     public void run() {
						    	 LocalCache cache = ((LocalCache)c.getApplicationContext());
						    	 clearUserNamePasswordBox();
						    	 mProgress.setVisibility(View.INVISIBLE);
								 mLoading.setVisibility(View.INVISIBLE);
						    	 cache.ShowMessage(c, "Access Denied", "Check your uid and password"); 
						     }
						});
				    }
				}
		 catch (final SocketException e) {
			
			
			 	
		    	}
			 catch (UnsupportedEncodingException e) {
		        e.printStackTrace();
		        } 
		 catch (ClientProtocolException e) {
		        e.printStackTrace();
		        } 
		 catch (IOException e) {
		        e.printStackTrace();
		    	}
		 finally {
			    try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
			}
			 

    	
    }
    private boolean ParseAthentication(String result){
    	try {
			JSONObject jObject = new JSONObject(result);
			String aJsonString = jObject.getString("status");
			if(aJsonString.equalsIgnoreCase("true")){
			return true;
			}
			else{
				return false;
			}
    	} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    	
    }
    private void ParseUserSurveyList(String result){
    	 try {
    	JSONObject jObject = new JSONObject(result);
    	JSONArray jArray = jObject.getJSONArray("userSurveyList");
    	
    	for (int i=0; i < jArray.length(); i++)
    	{
    	   
    	        JSONObject oneObject = jArray.getJSONObject(i);
    	        // Pulling items from the array
    	        String SurveyNameObject = DatabaseUtils.sqlEscapeString(oneObject.getString("surveyName"));
    	        int SurveyIdObject = oneObject.getInt("surveyId");
    	        // Check is the data is already in the database
    	        String CheckQuery = "SELECT SurveyId from Survey where Surveyid = " + SurveyIdObject + " and Submitted = " + 0 ;
    	        DataBaseHelper myDbHelper = new DataBaseHelper(c);
    	        Cursor cursor = myDbHelper.Rawquery(CheckQuery, null);
    	        if (cursor.getCount() > 0){
    	        	
    	        	// This survey is already on the system and it is not submitted so do nothing
    	        	cursor.close();
					myDbHelper.close();
    	        }
    	        else{
    	        	
    	        
    	       // Insert record to database local
    	        String Query = "INSERT INTO SURVEY " + " (Name, SurveyId, Submitted)" + " Values ("
    	        		+ SurveyNameObject +  ", "	+ SurveyIdObject + ", " + 0 + " ); ";
    	    	
    	        myDbHelper.InsertToDatabase(Query);
    	        
    	        cursor.close();
				myDbHelper.close();
    	        // ______________________________________________________
    	       // Get the SurveyItems and insert To DataBase
    	        LocalCache cache = ((LocalCache)c.getApplicationContext());
    	        HttpClient httpclient = new DefaultHttpClient();
    	        HttpGet httpGetSurveyItems = new HttpGet(cache.getServerURL() + "userSurvey?surveyID=" + SurveyIdObject);
    	        httpGetSurveyItems.addHeader("accept", "application/json");
    	        try {
    	        		HttpResponse SurveyItemsresponse = httpclient.execute(httpGetSurveyItems);
    	        		HttpEntity SurveyItemsentity = SurveyItemsresponse.getEntity();
    	        		InputStream inputStream = null;
    	        		String Itemsresult = null;
    	        		inputStream = SurveyItemsentity.getContent();
    	        		
    	        		BufferedReader SurveyItemsreader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
						    StringBuilder SurveyItemssb = new StringBuilder();
						
						    String SurveyItemsline = null;
						    
						    while ((SurveyItemsline = SurveyItemsreader.readLine()) != null)
						    {
						    	SurveyItemssb.append(SurveyItemsline + "\n");
						    }
						    Itemsresult = SurveyItemssb.toString();
						    
						    JSONObject ItemsObject = new JSONObject(Itemsresult);
					    	JSONArray ItemsArray = ItemsObject.getJSONArray("userSurvey");
					    	
					    	for (int Items=0; Items < ItemsArray.length(); Items++)
					    	{
					    		 JSONObject oneItemsObject = ItemsArray.getJSONObject(Items);
					    		// Pulling items from the array
					    		 int Questionid = oneItemsObject.getInt("questionId");
					    	     String Questiontype = DatabaseUtils.sqlEscapeString(oneItemsObject.getString("questionType"));
					    	     String Question = DatabaseUtils.sqlEscapeString(oneItemsObject.getString("question"));
					    	     String Surveytype = DatabaseUtils.sqlEscapeString(oneItemsObject.getString("surveyType"));
					    	     int Surveyid = oneItemsObject.getInt("surveyId");
					    	     String Options = DatabaseUtils.sqlEscapeString(oneItemsObject.getString("options"));
					    	     
					    	  // Insert this survey item to database local
					    	        String QueryItems = "INSERT INTO SURVEYITEMS " + " (Questionid, QuestionType, Question, SurveyType, SurveyId, Options, Responded)" + 
					    	        					" Values ("  + Questionid + ", "  + Questiontype + ", " +  Question  +  ", " +
					    	        					 Surveytype +  ", " + Surveyid + ", " +   Options  + ", " + 0 + " ); ";
					    	    	
					    	        DataBaseHelper myDbHelperForItems = new DataBaseHelper(c);
					    	        myDbHelperForItems.InsertToDatabase(QueryItems);
					    	        myDbHelperForItems.close();
					    	        
					    		
					    	}
    	        		
    	        	}
    	        catch (ClientProtocolException e) {
    		        	e.printStackTrace();
    		        } 
    	        catch (IOException e) {
    		        e.printStackTrace();
    		    	}
    	        
    	        }
    	     
    	}
    	 } 
    	 catch (JSONException e) {
 	        // Oops
 	    }
    	 
    	
    }
    private void clearUserNamePasswordBox(){
    	UID.setText("");
		 Password.setText("");
    	
    }
    
    private void LoadSurveyFromLocalDatabase(){
    	 
    	 LocalCache cache = ((LocalCache)c.getApplicationContext());
    	String Query = "Select * from Survey where submitted = 0 ";
    	 DataBaseHelper myDbHelper = new DataBaseHelper(c);
    	 Cursor c = myDbHelper.Rawquery(Query, null);
    	
    	 if (c.getCount() > 0){
    		 
    		 List<Survey> mylist = new ArrayList<Survey>();
    		 if (c.moveToFirst()) {
    			 
    			 do {
		    			
		    			Survey item = new Survey();
		    			item.set_id(c.getInt(c.getColumnIndex("_id")));
		    			item.setSurveyId(c.getInt(c.getColumnIndex("SurveyId")));
		    			item.setSurveyName(c.getString(c.getColumnIndex("Name")));
		    			item.setSubmitted(c.getInt(c.getColumnIndex("Submitted")));
		    			
		    			mylist.add(item);
		    			
		    			

		            } while (c.moveToNext());
		    		
		    		
		    		c.close();
					myDbHelper.close();
					cache.setSurveyLists(mylist);
					
    		 }
    	 }
    	
    	
    }
   
}

