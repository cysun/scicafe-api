package springrest.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.CascadeType;
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

@Entity
@Table(name = "events")
public class Event implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public enum Status {
    	submitted, 
    	approved,
    	rejected
    }
	
	@Id
    @GeneratedValue
    private Long id;
	
	private String eventName;
	
	private String description;
	
	private String location;
	
	private Timestamp startTime;
	
	private Timestamp endTime;
	
	private Status status;
	
	@ManyToMany
    @JoinTable(
        name = "events_tags", 
        joinColumns = { @JoinColumn(name = "tag_id") }, 
        inverseJoinColumns = { @JoinColumn(name = "event_id") }
    )
    Set<Tag> tags;
	
	@ManyToOne
	@JoinColumn(name="organizerId") 
	private User organizer;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
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

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
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
	
}
