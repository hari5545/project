package models.dto;



import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import models.entity.SurveyDetail;

public class SurveyDto {
	protected int id;
	protected String orgName;
	protected String orgUnitName;
	protected String  tabName;
	@JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy/MM/dd")
	protected Date  startDate;
	@JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy/MM/dd")
	protected Date  endDate;
	protected String  surveyName;
	protected List<SurveyDetail> question;
	
	
	public SurveyDto() {
		super();
	}
	
	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getOrgUnitName() {
		return orgUnitName;
	}
	public void setOrgUnitName(String orgUnitName) {
		this.orgUnitName = orgUnitName;
	}
	public String getTabName() {
		return tabName;
	}
	public void setTabName(String tabName) {
		this.tabName = tabName;
	}
	

	public Date getStartDate() {
		return startDate;
	}


	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}


	public Date getEndDate() {
		return endDate;
	}


	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}


	public String getSurveyName() {
		return surveyName;
	}
	public void setSurveyName(String surveyName) {
		this.surveyName = surveyName;
	}
	public List<SurveyDetail> getQuestion() {
		return question;
	}
	public void setQuestion(List<SurveyDetail> question) {
		this.question = question;
	}


	@Override
	public String toString() {
		return "SurveyDto [id=" + id + ", orgName=" + orgName + ", orgUnitName=" + orgUnitName + ", tabName=" + tabName
				+ ", startDate=" + startDate + ", endDate=" + endDate + ", surveyName=" + surveyName + ", question="
				+ question + "]";
	}
	
}
