package models.mattermost;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Timezone implements Serializable
{

	@SerializedName("automaticTimezone")
	@Expose
	private String automaticTimezone;
	@SerializedName("manualTimezone")
	@Expose
	private String manualTimezone;
	@SerializedName("useAutomaticTimezone")
	@Expose
	private String useAutomaticTimezone;
	
	private final static long serialVersionUID = -2779919489835347825L;

	public Timezone() {
	}

	public String getAutomaticTimezone() {
		return automaticTimezone;
	}

	public void setAutomaticTimezone(String automaticTimezone) {
		this.automaticTimezone = automaticTimezone;
	}

	public Timezone withAutomaticTimezone(String automaticTimezone) {
		this.automaticTimezone = automaticTimezone;
		return this;
	}

	public String getManualTimezone() {
		return manualTimezone;
	}

	public void setManualTimezone(String manualTimezone) {
		this.manualTimezone = manualTimezone;
	}

	public Timezone withManualTimezone(String manualTimezone) {
		this.manualTimezone = manualTimezone;
		return this;
	}

	public String getUseAutomaticTimezone() {
		return useAutomaticTimezone;
	}

	public void setUseAutomaticTimezone(String useAutomaticTimezone) {
		this.useAutomaticTimezone = useAutomaticTimezone;
	}

	public Timezone withUseAutomaticTimezone(String useAutomaticTimezone) {
		this.useAutomaticTimezone = useAutomaticTimezone;
		return this;
	}

	public int describeContents() {
		return 0;
	}

}
