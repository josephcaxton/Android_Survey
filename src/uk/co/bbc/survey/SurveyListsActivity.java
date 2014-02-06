package uk.co.bbc.survey;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class SurveyListsActivity extends ListActivity {
	
	
	 public void onCreate(Bundle icicle) {
		    super.onCreate(icicle);

	        
	        loadAdapter();
	        
	    }
	 
	 private void  loadAdapter() {
			LocalCache cache = ((LocalCache)getApplicationContext());
			
			ArrayList<Survey> values = (ArrayList<Survey>) cache.getSurveyLists();
			
			if(values == null){
				
				Toast.makeText(this, "You don't have any survey to complete", Toast.LENGTH_SHORT).show();
			}
			else
			{
			
				SurveyListsActivityAdapter adapter = new SurveyListsActivityAdapter(this,R.layout.listlayout, values);
			setListAdapter(adapter);
			
			
			}
		}
	 
	 @Override
		protected void onListItemClick(ListView l, View v, int position, long id) {
	 
		 LocalCache cache = ((LocalCache)getApplicationContext());
			//get selected items
		 Survey selectedValue = (Survey)getListAdapter().getItem(position);
		 
		 int Surveyid = selectedValue.getSurveyId();
		 LoadSurveyItemsFromLocalDatabase(Surveyid);
		 //----------------------
		 // TODO If there is nothing in list do something.
		 List<SurveyItem> list = cache.getListofSurveyItems();
		 
		if(list.size() > 0){
			 
		LoadSurveyItemViaTemplate(list.get(1));
		 
		}
		 
			
		
		
		
	}
	 
	 private void LoadSurveyItemsFromLocalDatabase(int SurveyID){
		 
		 LocalCache cache = ((LocalCache)getApplicationContext());
	    	String Query = "Select * from SurveyItems where surveyid = " + Integer.toString(SurveyID) +
	    		" and responded != 1 " + " or responded IS NULL " 	+ " order by Questionid " ;
	    	 DataBaseHelper myDbHelper = new DataBaseHelper(this.getBaseContext());
	    	 Cursor c = myDbHelper.Rawquery(Query, null);
	    	
	    	 if (c.getCount() > 0){
	    		 
	    		 List<SurveyItem> mylist = new ArrayList<SurveyItem>();
	    		 if (c.moveToFirst()) {
	    			 
	    			 do {
			    			
			    			SurveyItem item = new SurveyItem();
			    			item.set_id(c.getInt(c.getColumnIndex("_id")));
			    			item.setSurveyID(c.getInt(c.getColumnIndex("SurveyID")));
			    			item.setQuestionID(c.getInt(c.getColumnIndex("QuestionID")));
			    			item.setQuestionType(c.getString(c.getColumnIndex("QuestionType")));
			    			item.setQuestion(c.getString(c.getColumnIndex("Question")));
			    			item.setSurveyType(c.getString(c.getColumnIndex("SurveyType")));
			    			item.setOptions(c.getString(c.getColumnIndex("Options")));
			    			
			    			mylist.add(item);
			    			
			    			

			            } while (c.moveToNext());
			    		
			    		
			    		c.close();
						myDbHelper.close();
						cache.setListofSurveyItems(mylist);
						
	    		 }
	    	 }
	    	
	    	
		 
	 }
	 
	 public void LoadSurveyItemViaTemplate(SurveyItem item) {
			
			String QType = item.getQuestionType();
			
			
			
			if (QType.equalsIgnoreCase("Radio") || QType.equalsIgnoreCase("Dropdown")){
				
				Bundle basket1 = new Bundle();
				basket1.putInt("QuestionID", item.getQuestionID());
				basket1.putString("Question", item.getQuestion());
				basket1.putString("Options", item.getOptions());
				Intent MultipleChoiceSingle = new Intent(this.getBaseContext(),MultipleChoiceSingleAnswer.class);
				MultipleChoiceSingle.putExtras(basket1);
				startActivity(MultipleChoiceSingle);
			}
			else if (QType.equalsIgnoreCase("Freetext") ){
				
				Bundle basket2 = new Bundle();
				basket2.putInt("QuestionID", item.getQuestionID());
				basket2.putString("Question", item.getQuestion());
				basket2.putString("Options", item.getOptions());
				
			}
			else if (QType.equalsIgnoreCase("Checkbox")){
				
				Bundle basket3 = new Bundle();
				basket3.putInt("QuestionID", item.getQuestionID());
				basket3.putString("Question", item.getQuestion());
				basket3.putString("Options", item.getOptions());
			}
			
			
		}




}