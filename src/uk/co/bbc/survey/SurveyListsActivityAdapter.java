package uk.co.bbc.survey;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SurveyListsActivityAdapter extends ArrayAdapter<Survey>{
	private final Context context;
	private ArrayList<Survey> values;
	int resource;
	
	public SurveyListsActivityAdapter(Context context, int resource, ArrayList<Survey> values) {
		super(context, resource, values);
		this.context = context;
		this.values = values;
		this.resource=resource;
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		 if (view == null) {
			 LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 view = inflater.inflate(resource, parent, false);
		 }
		 Survey su = values.get(position);
		 if (su!= null) {
			 
			 TextView txtLabel= (TextView) view.findViewById(R.id.listlabel);
			 txtLabel.setText(su.getSurveyName());
 
			
		 }
		 return view;
	}


}
