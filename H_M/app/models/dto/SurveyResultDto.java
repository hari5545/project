package models.dto;



public class SurveyResultDto {
	protected String  channelId;
	protected String userId;
	protected String questionId;
	protected String result;
	protected String question;
	protected String type;
	protected String  resultDate;
	
	public SurveyResultDto() {
		super();
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getResultDate() {
		return resultDate;
	}

	public void setResultDate(String resultDate) {
		this.resultDate = resultDate;
	}

	@Override
	public String toString() {
		return "SurveyResultDto [channelId=" + channelId + ", userId=" + userId + ", questionId=" + questionId
				+ ", result=" + result + ", question=" + question + ", type=" + type + ", resultDate=" + resultDate
				+ "]";
	}

	
	
	
}
