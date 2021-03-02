package models.translate;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Data implements Serializable
{

@SerializedName("translations")
@Expose
private List<Translation> translations = null;
private final static long serialVersionUID = -8015145993481334472L;

/**
* No args constructor for use in serialization
*
*/
public Data() {
}

/**
*
* @param translations
*/
public Data(List<Translation> translations) {
super();
this.translations = translations;
}

public List<Translation> getTranslations() {
return translations;
}

public void setTranslations(List<Translation> translations) {
this.translations = translations;
}

public Data withTranslations(List<Translation> translations) {
this.translations = translations;
return this;
}

@Override
public String toString() {
return new ToStringBuilder(this).append("translations", translations).toString();
}

@Override
public int hashCode() {
return new HashCodeBuilder().append(translations).toHashCode();
}

@Override
public boolean equals(Object other) {
if (other == this) {
return true;
}
if ((other instanceof Data) == false) {
return false;
}
Data rhs = ((Data) other);
return new EqualsBuilder().append(translations, rhs.translations).isEquals();
}

}