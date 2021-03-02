package models.translate;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Translation implements Serializable
{

@SerializedName("translatedText")
@Expose
private String translatedText;
private final static long serialVersionUID = -4828675848214134405L;

/**
* No args constructor for use in serialization
*
*/
public Translation() {
}

/**
*
* @param translatedText
*/
public Translation(String translatedText) {
super();
this.translatedText = translatedText;
}

public String getTranslatedText() {
return translatedText;
}

public void setTranslatedText(String translatedText) {
this.translatedText = translatedText;
}

public Translation withTranslatedText(String translatedText) {
this.translatedText = translatedText;
return this;
}

@Override
public String toString() {
return new ToStringBuilder(this).append("translatedText", translatedText).toString();
}

@Override
public int hashCode() {
return new HashCodeBuilder().append(translatedText).toHashCode();
}

@Override
public boolean equals(Object other) {
if (other == this) {
return true;
}
if ((other instanceof Translation) == false) {
return false;
}
Translation rhs = ((Translation) other);
return new EqualsBuilder().append(translatedText, rhs.translatedText).isEquals();
}

}