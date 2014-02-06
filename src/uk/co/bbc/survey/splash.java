package uk.co.bbc.survey;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

public class splash extends Activity{
	
	private ProgressBar mProgress;
	private int mProgressStatus = 0;
	private Handler mHandler = new Handler();
	private int mCounter = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		//change the background color to white
		View LLView = findViewById(R.id.LLMain);
		View root = LLView.getRootView();
		root.setBackgroundColor(Color.argb(0, 238, 244, 228));
		
		mProgress = (ProgressBar) findViewById(R.id.SplashprogressBar);
		
		//Play sound

		MediaPlayer SplashSound = MediaPlayer.create(this, R.raw.arrowwoodimpact);
		SplashSound.start();
		
		LocalCache cache = ((LocalCache)getApplicationContext());
		cache.setServerURL("http://10.184.193.152:8080/BBCHRAPI/");
		cache.setReachabilityServerAddress("10.184.193.152");
		cache.setReachabilityPort("8080");
		
		cache.PlaySound(this,R.raw.arrowwoodimpact);
		
		// Setup Prefs if they are not there. to turn on Sound and ShowAnswer
		OtherPreferences sharedPrefs = new OtherPreferences(this.getBaseContext());
		 if(sharedPrefs.getPreference("sound").equalsIgnoreCase("N/A")){
			
			sharedPrefs.saveToPref("sound", "on");
			sharedPrefs.saveToPref("ShowAnswer", "on");
			
		}
		
		
		Thread timer = new Thread(){
			public void run(){
				while (mProgressStatus < 1000) {
				     mProgressStatus = copyFileOrDir("videos");
				  // Update the progress bar
				     mHandler.post(new Runnable() {
				         public void run() {
				             mProgress.setProgress(mProgressStatus);
				         }
				     });
				}
				DataBaseHelper myDbHelper = new DataBaseHelper(getApplicationContext());
		 
		        try {
		        	
		        	myDbHelper.createDataBase();
		        	myDbHelper.close();
		 
		 	} catch (IOException ioe) {
		 
		 		throw new Error("Unable to create database");
		 		
		 
		 	}
		 
		 	/*try {
		 		
		 		myDbHelper.openDataBase();
		 		
		 
		 	}catch(SQLException sqle){
		 
		 		throw sqle;
		 
		 	}*/
				finally{
					
					
					
					Intent OpenStartPage = new Intent("uk.co.bbc.survey.MAINACTIVITY");
					startActivity(OpenStartPage);
				}
			}
		};
		
			
		
		timer.start();
		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);

		//SplashSound.stop();
		finish();
	}
	
	private int copyFileOrDir(String path) {
	    /*AssetManager assetManager = getAssets();
	    String[] assets = null;
	    try {
	    	assets = assetManager.list(path);
	    	
	    	
	    	if (assets.length == 0) {
	            copyFile(path);
	        } else {
	            String fullPath = "/data/data/" + this.getPackageName() + "/" + path;
	            File dir = new File(fullPath);
	            if (!dir.exists())
	                dir.mkdir();
	            for (int i = 0; i < assets.length; ++i) {
	                copyFileOrDir(path + "/" + assets[i]);
	            }
	        }
	    	
	    } catch (IOException ex) {
	        Log.e("tag", "I/O Exception", ex);
	    }*/
		return mCounter++;
	}

	@SuppressWarnings("unused")
	private void copyFile(String filename) {
	    AssetManager assetManager = this.getAssets();

	    InputStream in = null;
	    OutputStream out = null;
	    try {
	        in = assetManager.open(filename);
	        String newFileName = "/data/data/" + this.getPackageName() + "/" + filename;
	        out = new FileOutputStream(newFileName);

	        byte[] buffer = new byte[1024];
	        int read;
	        while ((read = in.read(buffer)) != -1) {
	            out.write(buffer, 0, read);
	        }
	        in.close();
	        in = null;
	        out.flush();
	        out.close();
	        out = null;
	    } catch (Exception e) {
	        Log.e("tag", e.getMessage());
	    }

	}


	
	

}

