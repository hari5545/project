package models.translate;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class TranslationResponse implements Serializable
{

@SerializedName("data")
@Expose
private Data data;
private final static long serialVersionUID = 6140883812971822746L;

/**
* No args constructor for use in serialization
*
*/
public TranslationResponse() {
}

/**
*
* @param data
*/
public TranslationResponse(Data data) {
super();
this.data = data;
}

public Data getData() {
return data;
}

public void setData(Data data) {
this.data = data;
}

public TranslationResponse withData(Data data) {
this.data = data;
return this;
}

@Override
public String toString() {
return new ToStringBuilder(this).append("data", data).toString();
}

@Override
public int hashCode() {
return new HashCodeBuilder().append(data).toHashCode();
}

@Override
public boolean equals(Object other) {
if (other == this) {
return true;
}
if ((other instanceof TranslationResponse) == false) {
return false;
}
TranslationResponse rhs = ((TranslationResponse) other);
return new EqualsBuilder().append(data, rhs.data).isEquals();
}

}