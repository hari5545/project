package models.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TabMembers implements Serializable
{

@SerializedName("statusCode")
@Expose
private String statusCode;
@SerializedName("statusMessage")
@Expose
private String statusMessage;
@SerializedName("members")
@Expose
private List<Member> members = new ArrayList<Member>();
private final static long serialVersionUID = -2875165974367581966L;

/**
* No args constructor for use in serialization
*
*/
public TabMembers() {
}

/**
*
* @param members
* @param statusMessage
* @param statusCode
*/
public TabMembers(String statusCode, String statusMessage, List<Member> members) {
super();
this.statusCode = statusCode;
this.statusMessage = statusMessage;
this.members = members;
}

public String getStatusCode() {
return statusCode;
}

public void setStatusCode(String statusCode) {
this.statusCode = statusCode;
}

public TabMembers withStatusCode(String statusCode) {
this.statusCode = statusCode;
return this;
}

public String getStatusMessage() {
return statusMessage;
}

public void setStatusMessage(String statusMessage) {
this.statusMessage = statusMessage;
}

public TabMembers withStatusMessage(String statusMessage) {
this.statusMessage = statusMessage;
return this;
}

public List<Member> getMembers() {
return members;
}

public void setMembers(List<Member> members) {
this.members = members;
}

public TabMembers withMembers(List<Member> members) {
this.members = members;
return this;
}

}