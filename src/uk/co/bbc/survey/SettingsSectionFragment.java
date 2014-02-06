package uk.co.bbc.survey;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SettingsSectionFragment extends Fragment{
	
	public static final String ARG_SECTION_NUMBER = "section_number";
	
	 public SettingsSectionFragment() {
	    }
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	        View rootView = inflater.inflate(R.layout.fragment_main_settings, container, false);
	        TextView surveyTextView = (TextView) rootView.findViewById(R.id.setting_section_label);
	        surveyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
	        return rootView;
	    }
}
