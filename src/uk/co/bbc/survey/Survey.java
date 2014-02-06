package uk.co.bbc.survey;

public class Survey extends Object{
	
	private int SurveyId;
	private String SurveyName;
	private int Submitted;
	private int _id;
	
	
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public int getSurveyId() {
		return SurveyId;
	}
	public void setSurveyId(int surveyId) {
		SurveyId = surveyId;
	}
	public String getSurveyName() {
		return SurveyName;
	}
	public void setSurveyName(String surveyName) {
		SurveyName = surveyName;
	}
	public int getSubmitted() {
		return Submitted;
	}
	public void setSubmitted(int submitted) {
		Submitted = submitted;
	}
	
	

}
