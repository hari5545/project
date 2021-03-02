package models.entity;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
//import javax.persistence.Type;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

@Entity
@Table(name="ReportLog")
public class ReportLog {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected int id;

    @Column(name = "username")
    private String reportedUser;

    @Column(name = "error")
    private String error;

    @Column(name = "stackTrace", columnDefinition = "text")
    private String stackTrace;

    @ElementCollection
    private Map<String,String> deviceParameters;

    @ElementCollection
    private Map<String,String> applicationParameters;

    @ElementCollection
    private Map<String,String> customParameters;

    @Column(name = "dateTime")
    private Timestamp dateTime;



    @Column(name = "createdDateTime")
    private Timestamp createdDateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public Map<String, String> getDeviceParameters() {
        return deviceParameters;
    }

    public void setDeviceParameters(Map<String, String> deviceParameters) {
        this.deviceParameters = deviceParameters;
    }

    public Map<String, String> getApplicationParameters() {
        return applicationParameters;
    }

    public void setApplicationParameters(Map<String, String> applicationParameters) {
        this.applicationParameters = applicationParameters;
    }

    public Map<String, String> getCustomParameters() {
        return customParameters;
    }

    public void setCustomParameters(Map<String, String> customParameters) {
        this.customParameters = customParameters;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getReportedUser() {
        return reportedUser;
    }

    public void setReportedUser(String reportedUser) {
        this.reportedUser = reportedUser;
    }

    public Timestamp getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(Timestamp createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportLog reportLog = (ReportLog) o;
        return id == reportLog.id &&
                Objects.equals(reportedUser, reportLog.reportedUser) &&
                Objects.equals(error, reportLog.error) &&
                Objects.equals(stackTrace, reportLog.stackTrace) &&
                Objects.equals(deviceParameters, reportLog.deviceParameters) &&
                Objects.equals(applicationParameters, reportLog.applicationParameters) &&
                Objects.equals(customParameters, reportLog.customParameters) &&
                Objects.equals(dateTime, reportLog.dateTime) &&
                Objects.equals(createdDateTime, reportLog.createdDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reportedUser, error, stackTrace, deviceParameters, applicationParameters, customParameters, dateTime, createdDateTime);
    }

    @Override
    public String toString() {
        return "ReportLog{" +
                "id=" + id +
                ", reportedUser='" + reportedUser + '\'' +
                ", error='" + error + '\'' +
                ", stackTrace='" + stackTrace + '\'' +
                ", deviceParameters=" + deviceParameters +
                ", applicationParameters=" + applicationParameters +
                ", customParameters=" + customParameters +
                ", dateTime=" + dateTime +
                ", createdDateTime=" + createdDateTime +
                '}';
    }

    public String getStackTraceFormatted() {
        return "<small>"+stackTrace.replace("\n", "<br>")+"</small>";
    }

    public String getDeviceDataFormatted() {
        String text = "<small>";
        for (Entry<String, String> entry: deviceParameters.entrySet()) {
            text += "<b>"+entry.getKey()+"</b>:"+entry.getValue() +"<br>";
        }
        text += "</small>";
        return text;
    }
}
