package uk.co.bbc.survey;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;

public class LocalCache extends Application{
	
	
	// Store a collection of Surveys assigned to the user in this context
	private List<Survey> ListofSurveys;

	public List<Survey> getSurveyLists() {
		return ListofSurveys;
	}

	public void setSurveyLists(List<Survey> Surveys) {
		ListofSurveys = Surveys;
	} 
	
	//Store collection of SurveyItems
	private List<SurveyItem> ListofSurveyItems;
	
	
	public List<SurveyItem> getListofSurveyItems() {
		return ListofSurveyItems;
	}

	public void setListofSurveyItems(List<SurveyItem> listofSurveyItems) {
		ListofSurveyItems = listofSurveyItems;
	}

	// Store Server URL Here 
	private String ServerURL;

	public String getServerURL() {
		return ServerURL;
	}

	public void setServerURL(String serverURL) {
		ServerURL = serverURL;
	}
	
	// Reachability 
	private String ReachabilityServerAddress;

	public String getReachabilityServerAddress() {
		return ReachabilityServerAddress;
	}

	public void setReachabilityServerAddress(String ReachabilityServerAddresssURL) {
		ReachabilityServerAddress = ReachabilityServerAddresssURL;
	}
	private String ReachabilityPort;
	
	
	public String getReachabilityPort() {
		return ReachabilityPort;
	}

	public void setReachabilityPort(String reachabilityPort) {
		ReachabilityPort = reachabilityPort;
	}
	// Login UID
	private String LoggedinUID;

	public String getLoggedinUID() {
		return LoggedinUID;
	}

	public void setLoggedinUID(String uid) {
		LoggedinUID = uid;
	}
	
	public void StoreAuthentication(String UID ){
		
		SharedPreferences sharedPrefs = getSharedPreferences("BBCUID", MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		// Get the time now
		Calendar c = Calendar.getInstance(); 
		String TodaysDate = c.getTime().toString();
	
		editor.putString("BBCUID",TodaysDate);
		editor.commit();
		
	}
	public boolean CheckAuthenticationInPreference(){
		
		SharedPreferences sharedPrefs = getSharedPreferences("BBCUID", MODE_PRIVATE);
		String retTime = sharedPrefs.getString("BBCUID","1/1/1970");
		Calendar c = Calendar.getInstance();
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
			c.setTime(sdf.parse(retTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		c.add(Calendar.DATE, 2);// Check if the User has had the password store for 2 days
		
		Calendar newCal = Calendar.getInstance(); 
		
		//String Oldtime = c.getTime().toGMTString();
		//String Newtime = newCal.getTime().toGMTString(); 
	
		if (c.getTime().after(newCal.getTime()) ){  // true means don't allow this user via preference
			return false;
		}
		else{
		
			return true;
		}
	}
	
	private int  YourCorrectAnswerResult;

	public int getYourCorrectAnswerResult() {
		return YourCorrectAnswerResult;
	}

	public void setYourCorrectAnswerResult(int val) {
		YourCorrectAnswerResult+= val; // Add one to the result
	}
	public void setYourCorrectAnswerResultToZero(){
		YourCorrectAnswerResult = 0;
	}

	private int ScorableQuestions;

	public int getScorableQuestions() {
		return ScorableQuestions;
	}

	public void setScorableQuestions(int scorableQuestions) {
		ScorableQuestions+= scorableQuestions;
	}
	public void setScorableQuestionsToZero(){
		ScorableQuestions = 0;
	}
	
	public void PlaySound(Context c, int ResourceId){
		
		
		
		MediaPlayer SplashSound = MediaPlayer.create(c, ResourceId);
		SplashSound.start();
		
		SplashSound.setOnCompletionListener(new OnCompletionListener() {

           
            public void onCompletion(MediaPlayer mp) {
                
            
        		mp.release();
        		mp = null;
            }

        });

		//SplashSound = null;
	}
	// this is to check the internet connection available
	// 0 = no connection
	// 1 = Mobile connection
	// 2 Wifi connection
	private int HaveNetworkConnection()
	{
		
	    int connectionType = 0;
	    

	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
	    for (NetworkInfo ni : netInfo)
	    {
	        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
	            if (ni.isConnected())
	            	connectionType = 3;
	        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
	            if (ni.isConnected())
	            	connectionType = 1;
	    }
	    return connectionType;
	}
	
	public int GetConnectionType()
	{
		return HaveNetworkConnection();
	}
	
	private boolean IsNetworkReachable(Inet4Address ip, int port){
		
		boolean exists = false;

		try {
		    SocketAddress sockaddr = new InetSocketAddress(ip, port);
		    // Create an unbound socket
		    Socket sock = new Socket();

		    // This method will block no more than timeoutMs.
		    // If the timeout occurs, SocketTimeoutException is thrown.
		    int timeoutMs = 2000;   // 2 seconds
		    sock.connect(sockaddr, timeoutMs);
		    exists = true;
		}catch(Exception e){
			Log.d("Joseph",  "No connection " + e.getMessage());
		}
		return exists;
	}
	public boolean GetReachability(Inet4Address address, int PortNumber){
		
		return IsNetworkReachable(address,PortNumber);
	}
	
	
	
	
	public void ShowMessage(Context c,String title, String Message){
		
		 AlertDialog AuthenticateFaliureAlert;
		 AuthenticateFaliureAlert = new AlertDialog.Builder(c,2).create();
		 AuthenticateFaliureAlert.setTitle(title);
		 AuthenticateFaliureAlert.setMessage(Message);
		 AuthenticateFaliureAlert.show();
	}
	
}
