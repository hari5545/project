package models.translate;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class TranslationRequest implements Serializable
{

@SerializedName("q")
@Expose
private List<String> q = null;
@SerializedName("source")
@Expose
private String source;
@SerializedName("target")
@Expose
private String target;
private final static long serialVersionUID = -6101719274905983872L;

/**
* No args constructor for use in serialization
*
*/
public TranslationRequest() {
}

/**
*
* @param q
* @param source
* @param target
*/
public TranslationRequest(List<String> q, String source, String target) {
super();
this.q = q;
this.source = source;
this.target = target;
}

public List<String> getQ() {
return q;
}

public void setQ(List<String> q) {
this.q = q;
}

public TranslationRequest withQ(List<String> q) {
this.q = q;
return this;
}

public String getSource() {
return source;
}

public void setSource(String source) {
this.source = source;
}

public TranslationRequest withSource(String source) {
this.source = source;
return this;
}

public String getTarget() {
return target;
}

public void setTarget(String target) {
this.target = target;
}

public TranslationRequest withTarget(String target) {
this.target = target;
return this;
}

@Override
public String toString() {
return new ToStringBuilder(this).append("q", q).append("source", source).append("target", target).toString();
}

@Override
public int hashCode() {
return new HashCodeBuilder().append(q).append(source).append(target).toHashCode();
}

@Override
public boolean equals(Object other) {
if (other == this) {
return true;
}
if ((other instanceof TranslationRequest) == false) {
return false;
}
TranslationRequest rhs = ((TranslationRequest) other);
return new EqualsBuilder().append(q, rhs.q).append(source, rhs.source).append(target, rhs.target).isEquals();
}

}