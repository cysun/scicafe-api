package springrest.model;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "events")
public class Event implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
//	status 
//	submitted  0 
//	approved   1
//	rejected   2
	
	@Id
    @GeneratedValue
    private Long id;
	
	private String name;
	
	private String description;
	
	private String location;
	
	private Date eventDate;
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm:ss")
	private Time startTime;
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm:ss")
	private Time endTime;
	
	private int status;
	
	@ManyToMany(cascade={CascadeType.PERSIST,CascadeType.MERGE,CascadeType.DETACH},fetch=FetchType.LAZY)
    @JoinTable(
        name = "events_tags", 
        joinColumns = { @JoinColumn(name = "event_id") }, 
        inverseJoinColumns = { @JoinColumn(name = "tag_id") }
    )
    Set<Tag> tags;
	
	@Column(nullable = true)
	private String imageUrl;
	

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@ManyToOne
	private User organizer;
	
	@ManyToMany(cascade={CascadeType.PERSIST,CascadeType.MERGE,CascadeType.DETACH},fetch=FetchType.LAZY)
    @JoinTable(
        name = "users_events", 
        joinColumns = { @JoinColumn(name = "event_id") }, 
        inverseJoinColumns = { @JoinColumn(name = "user_id") }
    )
	Set<User> attendees;
	
	public Set<User> getAttendees() {
		return attendees;
	}

	public void setAttendees(Set<User> attendees) {
		this.attendees = attendees;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Set<Tag> getTags() {
		return tags;
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

	public User getOrganizer() {
		return organizer;
	}

	public void setOrganizer(User organizer) {
		this.organizer = organizer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getEventDate() {
		return this.eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	public Time getStartTime() {
		return startTime;
	}

	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}

	public Time getEndTime() {
		return endTime;
	}

	public void setEndTime(Time endTime) {
		this.endTime = endTime;
	}
	

	
}
