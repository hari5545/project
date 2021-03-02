package models.entity;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
@Table(name="Podcast")
public class Podcast implements Serializable {

private final static long serialVersionUID = -7521568180447714398L;

@Id
@GeneratedValue(strategy = GenerationType.AUTO)
protected int id;

@Column(name = "podcastName")
private String podcastName;

@Column(name = "podcastTitle")
private String podcastTitle;

@Column(name = "podcastDescription")
private String podcastDescription;

@Column(name = "podcast")
private String podcast;

@Column(name = "podcastCoverArt")
private String podcastCoverArt;

@Column(name = "podcastDuration")
private String podcastDuration;

@Column(name = "podcastAuthor")
private String podcastAuthor;

@Column(name = "podcastPublicationDate")
private Timestamp podcastPublicationDate;

@Column(name = "podcastActiveStatus")
private String podcastActiveStatus;

public int getId() {
return id;
}

public void setId(int id) {
this.id = id;
}

public Podcast withId(int id) {
this.id = id;
return this;
}

public String getPodcastName() {
return podcastName;
}

public void setPodcastName(String podcastName) {
this.podcastName = podcastName;
}

public Podcast withPodcastName(String podcastName) {
this.podcastName = podcastName;
return this;
}

public String getPodcastTitle() {
return podcastTitle;
}

public void setPodcastTitle(String podcastTitle) {
this.podcastTitle = podcastTitle;
}

public Podcast withPodcastTitle(String podcastTitle) {
this.podcastTitle = podcastTitle;
return this;
}

public String getPodcastDescription() {
return podcastDescription;
}

public void setPodcastDescription(String podcastDescription) {
this.podcastDescription = podcastDescription;
}

public Podcast withPodcastDescription(String podcastDescription) {
this.podcastDescription = podcastDescription;
return this;
}

public String getPodcast() {
return podcast;
}

public void setPodcast(String podcast) {
this.podcast = podcast;
}

public Podcast withPodcast(String podcast) {
this.podcast = podcast;
return this;
}

public String getPodcastCoverArt() {
return podcastCoverArt;
}

public void setPodcastCoverArt(String podcastCoverArt) {
this.podcastCoverArt = podcastCoverArt;
}

public Podcast withPodcastCoverArt(String podcastCoverArt) {
this.podcastCoverArt = podcastCoverArt;
return this;
}

public String getPodcastDuration() {
return podcastDuration;
}

public void setPodcastDuration(String podcastDuration) {
this.podcastDuration = podcastDuration;
}

public Podcast withPodcastDuration(String podcastDuration) {
this.podcastDuration = podcastDuration;
return this;
}

public String getPodcastAuthor() {
return podcastAuthor;
}

public void setPodcastAuthor(String podcastAuthor) {
this.podcastAuthor = podcastAuthor;
}

public Podcast withPodcastAuthor(String podcastAuthor) {
this.podcastAuthor = podcastAuthor;
return this;
}

public Timestamp getPodcastPublicationDate() {
return podcastPublicationDate;
}

public void setPodcastPublicationDate(Timestamp podcastPublicationDate) {
this.podcastPublicationDate = podcastPublicationDate;
}

public Podcast withPodcastPublicationDate(Timestamp podcastPublicationDate) {
this.podcastPublicationDate = podcastPublicationDate;
return this;
}

public String isPodcastActiveStatus() {
return podcastActiveStatus;
}

public void setPodcastActiveStatus(String podcastActiveStatus) {
this.podcastActiveStatus = podcastActiveStatus;
}

public Podcast withPodcastActiveStatus(String podcastActiveStatus) {
this.podcastActiveStatus = podcastActiveStatus;
return this;
}

}