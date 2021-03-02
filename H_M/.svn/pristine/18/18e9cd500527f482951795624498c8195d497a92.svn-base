package models.client;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Department implements Serializable
{

@SerializedName("departmentName")
@Expose
private String departmentName;
@SerializedName("channels")
@Expose
private List<Channel> channels = null;
private final static long serialVersionUID = -329640388436530164L;

/**
* No args constructor for use in serialization
*
*/
public Department() {
}

/**
*
* @param departmentName
* @param channels
*/
public Department(String departmentName, List<Channel> channels) {
super();
this.departmentName = departmentName;
this.channels = channels;
}

public String getDepartmentName() {
return departmentName;
}

public void setDepartmentName(String departmentName) {
this.departmentName = departmentName;
}

public Department withDepartmentName(String departmentName) {
this.departmentName = departmentName;
return this;
}

public List<Channel> getChannels() {
return channels;
}

public void setChannels(List<Channel> channels) {
this.channels = channels;
}

public Department withChannels(List<Channel> channels) {
this.channels = channels;
return this;
}

}