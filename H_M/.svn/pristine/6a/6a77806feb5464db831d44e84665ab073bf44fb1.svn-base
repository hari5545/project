package models.mattermost;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserAuthTokenResponse implements Serializable
{

@SerializedName("id")
@Expose
private String id;
@SerializedName("user_id")
@Expose
private String userId;
@SerializedName("description")
@Expose
private String description;
@SerializedName("is_active")
@Expose
private boolean isActive;
private final static long serialVersionUID = 8163746143678496507L;

/**
* No args constructor for use in serialization
*
*/
public UserAuthTokenResponse() {
}

/**
*
* @param description
* @param id
* @param isActive
* @param userId
*/
public UserAuthTokenResponse(String id, String userId, String description, boolean isActive) {
super();
this.id = id;
this.userId = userId;
this.description = description;
this.isActive = isActive;
}

public String getId() {
return id;
}

public void setId(String id) {
this.id = id;
}

public UserAuthTokenResponse withId(String id) {
this.id = id;
return this;
}

public String getUserId() {
return userId;
}

public void setUserId(String userId) {
this.userId = userId;
}

public UserAuthTokenResponse withUserId(String userId) {
this.userId = userId;
return this;
}

public String getDescription() {
return description;
}

public void setDescription(String description) {
this.description = description;
}

public UserAuthTokenResponse withDescription(String description) {
this.description = description;
return this;
}

public boolean isIsActive() {
return isActive;
}

public void setIsActive(boolean isActive) {
this.isActive = isActive;
}

public UserAuthTokenResponse withIsActive(boolean isActive) {
this.isActive = isActive;
return this;
}

}