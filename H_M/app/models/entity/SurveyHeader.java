package models.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;


@Entity
@Table(name="Survey_header") 
public class SurveyHeader implements Serializable{


	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected int id;
	
	@Column(name = "organization") 
	protected String organizationName;

	@Column(name = "organization_unit") 
	protected String organizationUnitName;

	@Column(name = "channel") 
	protected String channel;
	
	@Column(name="channel_id")
	protected String channelId;

	@Column(name = "start_dt") 
	@Temporal(TemporalType.DATE)
	@JsonFormat(pattern = "yyyy/MM/dd")
	protected Date  startDate;

	@Column(name = "end_dt")
	@Temporal(TemporalType.DATE)
	@JsonFormat(pattern = "yyyy/MM/dd")
	protected Date  endDate;
	
	@Column(name = "survey_name", unique=true,nullable = false)
	protected String surveyName;

	@OneToMany(targetEntity = SurveyDetail.class,cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
	@JoinColumn(name ="survey_header_id",referencedColumnName = "id")
	protected List<SurveyDetail> surveyDetails;	

	public SurveyHeader() {
		super();
	}

	

	public SurveyHeader(int id, String organizationName, String organizationUnitName, String channel, String channelId,
			Date startDate, Date endDate, String surveyName, List<SurveyDetail> surveyDetails) {
		super();
		this.id = id;
		this.organizationName = organizationName;
		this.organizationUnitName = organizationUnitName;
		this.channel = channel;
		this.channelId = channelId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.surveyName = surveyName;
		this.surveyDetails = surveyDetails;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getOrganizationUnitName() {
		return organizationUnitName;
	}

	public void setOrganizationUnitName(String organizationUnitName) {
		this.organizationUnitName = organizationUnitName;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
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

	
	public String getSurveyName() {
		return surveyName;
	}



	public void setSurveyName(String surveyName) {
		this.surveyName = surveyName;
	}



	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public List<SurveyDetail> getSurveyDetails() {
		return surveyDetails;
	}

	public void setSurveyDetails(List<SurveyDetail> surveyDetails) {
		this.surveyDetails = surveyDetails;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((channel == null) ? 0 : channel.hashCode());
		result = prime * result + ((channelId == null) ? 0 : channelId.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + id;
		result = prime * result + ((organizationName == null) ? 0 : organizationName.hashCode());
		result = prime * result + ((organizationUnitName == null) ? 0 : organizationUnitName.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + ((surveyDetails == null) ? 0 : surveyDetails.hashCode());
		result = prime * result + ((surveyName == null) ? 0 : surveyName.hashCode());
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
		SurveyHeader other = (SurveyHeader) obj;
		if (channel == null) {
			if (other.channel != null)
				return false;
		} else if (!channel.equals(other.channel))
			return false;
		if (channelId == null) {
			if (other.channelId != null)
				return false;
		} else if (!channelId.equals(other.channelId))
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (id != other.id)
			return false;
		if (organizationName == null) {
			if (other.organizationName != null)
				return false;
		} else if (!organizationName.equals(other.organizationName))
			return false;
		if (organizationUnitName == null) {
			if (other.organizationUnitName != null)
				return false;
		} else if (!organizationUnitName.equals(other.organizationUnitName))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (surveyDetails == null) {
			if (other.surveyDetails != null)
				return false;
		} else if (!surveyDetails.equals(other.surveyDetails))
			return false;
		if (surveyName == null) {
			if (other.surveyName != null)
				return false;
		} else if (!surveyName.equals(other.surveyName))
			return false;
		return true;
	}



	@Override
	public String toString() {
		return "SurveyHeader [id=" + id + ", organizationName=" + organizationName + ", organizationUnitName="
				+ organizationUnitName + ", channel=" + channel + ", channelId=" + channelId + ", startDate="
				+ startDate + ", endDate=" + endDate + ", surveyName=" + surveyName + ", surveyDetails=" + surveyDetails
				+ "]";
	}


	

}