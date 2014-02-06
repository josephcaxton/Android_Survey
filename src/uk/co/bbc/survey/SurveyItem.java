package uk.co.bbc.survey;

public class SurveyItem extends Object {
	
	private int SurveyID;
	private int QuestionID;
	private String QuestionType;
	private String Question;
	private String SurveyType;
	private String Options;
	private int _id;
	
	
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public int getSurveyID() {
		return SurveyID;
	}
	public void setSurveyID(int surveyID) {
		SurveyID = surveyID;
	}
	public int getQuestionID() {
		return QuestionID;
	}
	public void setQuestionID(int questionID) {
		QuestionID = questionID;
	}
	public String getQuestionType() {
		return QuestionType;
	}
	public void setQuestionType(String questionType) {
		QuestionType = questionType;
	}
	public String getQuestion() {
		return Question;
	}
	public void setQuestion(String question) {
		Question = question;
	}
	public String getSurveyType() {
		return SurveyType;
	}
	public void setSurveyType(String surveyType) {
		SurveyType = surveyType;
	}
	public String getOptions() {
		return Options;
	}
	public void setOptions(String options) {
		Options = options;
	}
	
	
	

}
