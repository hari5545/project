package models.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;



@Entity
@Table(name="Survey_detail")
public class SurveyDetail implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected int id;

	@Column(name = "question") 
	protected String question;

	@Column(name = "type") 
	protected String type;

	@Column(name = "answer") 
	protected String answers;

	@Column(name = "is_active") 
	protected boolean isActive=true;



	public SurveyDetail() {
		super();
	}
	
	public SurveyDetail(int id, String question, String type, String answers, boolean isActive) {
		super();
		this.id = id;
		this.question = question;
		this.type = type;
		this.answers = answers;
		this.isActive = isActive;
	}

	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
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


	public String getAnswers() {
		return answers;
	}


	public void setAnswers(String answers) {
		this.answers = answers;
	}
	
	@JsonIgnore
	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((answers == null) ? 0 : answers.hashCode());
		result = prime * result + id;
		result = prime * result + (isActive ? 1231 : 1237);
		result = prime * result + ((question == null) ? 0 : question.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SurveyDetail other = (SurveyDetail) obj;
		if (answers == null) {
			if (other.answers != null)
				return false;
		} else if (!answers.equals(other.answers))
			return false;
		if (id != other.id)
			return false;
		if (isActive != other.isActive)
			return false;
		if (question == null) {
			if (other.question != null)
				return false;
		} else if (!question.equals(other.question))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SurveyDetail [id=" + id + ", question=" + question + ", type=" + type + ", answers=" + answers
				+ ", isActive=" + isActive + "]";
	}
		
}
