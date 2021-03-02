package models.client;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ClientUser implements Serializable
{

@SerializedName("username")
@Expose
private String username;
@SerializedName("password")
@Expose
private String password;
private final static long serialVersionUID = -5507332199722919106L;

/**
* No args constructor for use in serialization
*
*/
public ClientUser() {
}

/**
*
* @param password
* @param username
*/
public ClientUser(String username, String password) {
super();
this.username = username;
this.password = password;
}

public String getUsername() {
return username;
}

public void setUsername(String username) {
this.username = username;
}

public String getPassword() {
return password;
}

public void setPassword(String password) {
this.password = password;
}

@Override
public String toString() {
return new ToStringBuilder(this).append("username", username).append("password", password).toString();
}

@Override
public int hashCode() {
return new HashCodeBuilder().append(password).append(username).toHashCode();
}

@Override
public boolean equals(Object other) {
if (other == this) {
return true;
}
if ((other instanceof ClientUser) == false) {
return false;
}
ClientUser rhs = ((ClientUser) other);
return new EqualsBuilder().append(password, rhs.password).append(username, rhs.username).isEquals();
}

}