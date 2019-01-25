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
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import springrest.model.Event.Status;

@Entity
@Table(name = "rewards")
public class Reward implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public enum Status {
    	submitted, 
    	approved,
    	rejected,
    	expired
    }

	@Id
    @GeneratedValue
    private Long id;
	
	private String name;
	
	private String providerName;
	
	private String description;
	
	private Timestamp startTime;
	
	private Timestamp endTime;
	
	private Integer criteria;
	
	@ManyToMany(cascade={CascadeType.PERSIST,CascadeType.MERGE,CascadeType.DETACH},fetch=FetchType.LAZY)
    @JoinTable(
        name = "rewards_tags", 
        joinColumns = { @JoinColumn(name = "reward_id") }, 
        inverseJoinColumns = { @JoinColumn(name = "tag_id") }
    )
	private Set<Tag> tags;
	
	@ManyToMany(cascade={CascadeType.PERSIST,CascadeType.MERGE,CascadeType.DETACH},fetch=FetchType.LAZY)
    @JoinTable(
        name = "rewards_events", 
        joinColumns = { @JoinColumn(name = "reward_id") }, 
        inverseJoinColumns = { @JoinColumn(name = "event_id") }
    )
    Set<Event> events;
	
	@JsonBackReference
	@ManyToOne(cascade={CascadeType.PERSIST,CascadeType.MERGE,CascadeType.DETACH},fetch=FetchType.LAZY)
	private User submitter;
	
	private Status status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public Set<Tag> getTags() {
		return tags;
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

	public Set<Event> getEvents() {
		return events;
	}

	public void setEvents(Set<Event> events) {
		this.events = events;
	}

	public User getSubmitter() {
		return submitter;
	}

	public void setSubmitter(User submitter) {
		this.submitter = submitter;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Integer getCriteria() {
		return criteria;
	}

	public void setCriteria(Integer criteria) {
		this.criteria = criteria;
	}
	
}
