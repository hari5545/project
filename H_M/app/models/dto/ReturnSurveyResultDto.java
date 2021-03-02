package models.dto;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

public class ReturnSurveyResultDto {

	protected String question;
	protected String answer;
	protected String userName;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	protected Date   resultDate;
	
	
	public ReturnSurveyResultDto() {
		super();
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Date getResultDate() {
		return resultDate;
	}
	public void setResultDate(Date resultDate) {
		this.resultDate = resultDate;
	}
	@Override
	public String toString() {
		return "ReturnSurveyResultDto [question=" + question + ", answer=" + answer + ", userName=" + userName
				+ ", resultDate=" + resultDate + "]";
	}
	
	
}
