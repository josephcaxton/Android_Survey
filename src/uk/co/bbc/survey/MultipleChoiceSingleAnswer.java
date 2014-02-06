package uk.co.bbc.survey;

import android.R.array;
import android.app.Activity;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TableLayout;

public class MultipleChoiceSingleAnswer extends Activity { //implements OnClickListener

	WebView QuestionHeaderBox;
	TableLayout Table;
	Button btnNext;
	Button btnStop;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.multiplechoicesingleanswer);
		
		QuestionHeaderBox =(WebView)findViewById(R.id.QuestionHeaderBox);
		Table =(TableLayout)findViewById(R.id.Qtable);
		btnNext= (Button)findViewById(R.id.btnSingleAnswerNext);
		btnStop = (Button)findViewById(R.id.btnSingleStop);
		
		
		//btnNext.setOnClickListener(this);
		btnNext.setTag(2); // I am using this to identify the Next button
		
		
		//btnStop.setOnClickListener(this);
		btnStop.setTag(3); // I am using this to identify the Stop Button
		
		
		WebSettings websettings = QuestionHeaderBox.getSettings();
        websettings.setSupportZoom(true);
        websettings.setBuiltInZoomControls(true);
        
		
		Bundle getBasket = getIntent().getExtras();
		Integer Questionid = getBasket.getInt("QuestionID");
		String  Question = getBasket.getString("Question");
		String  Options = getBasket.getString("Options");
		
		// Breakup Options
		String[] options = breakupOptions(Options);
		QuestionHeaderBox.loadData(Question, "text/html", "UTF-8");	
		
		
		
	}
	
 private String[] breakupOptions(String options){
	 
	 String[] OptionSplit = options.split("/");
	 return OptionSplit;
 }
	
}
