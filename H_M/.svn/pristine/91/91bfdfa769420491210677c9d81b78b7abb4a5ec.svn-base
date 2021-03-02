package models.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.*;


@Entity
@Table(name="survey_result") 
public class SurveyResult implements Serializable{

	private static final long serialVersionUID = 1L;


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "channel_id") 
	private String channelId;

	@Column(name = "user_id") 
	private String userId;

	@Column(name = "survey_question_id") 
	private String surveyQuestionId;

	@Column(name = "result")
	private String result;

	@Column(name = "created_dt")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;


	public SurveyResult() {
	}

	public SurveyResult(int id, String channelId, String userId, String surveyQuestionId, String result,
			Date createdDate) {
		this.id = id;
		this.channelId = channelId;
		this.userId = userId;
		this.surveyQuestionId = surveyQuestionId;
		this.result = result;
		this.createdDate = createdDate;
	}

	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
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


	public String getSurveyQuestionId() {
		return surveyQuestionId;
	}


	public void setSurveyQuestionId(String surveyQuestionId) {
		this.surveyQuestionId = surveyQuestionId;
	}


	public String getResult() {
		return result;
	}


	public void setResult(String result) {
		this.result = result;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	@Override
	public String toString() {
		return "SurveyResult [id=" + id + ", channelId=" + channelId + ", userId=" + userId + ", surveyQuestionId="
				+ surveyQuestionId + ", result=" + result + ", createdDate=" + createdDate + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(channelId, createdDate, id, result, surveyQuestionId, userId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SurveyResult other = (SurveyResult) obj;
		return Objects.equals(channelId, other.channelId) && Objects.equals(createdDate, other.createdDate)
				&& id == other.id && Objects.equals(result, other.result)
				&& Objects.equals(surveyQuestionId, other.surveyQuestionId) && Objects.equals(userId, other.userId);
	}
}