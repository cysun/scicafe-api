package springrest.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;

public class Progress implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public Progress (Reward reward, Set<Event> events) {
		this.setReward(reward);
		this.setAttendedEvents(events);
		this.percentage = events.size() / this.reward.getCriteria() >= 1 ? 100 : (int)(events.size() * 100 / this.reward.getCriteria());
		this.bar = percentage / 5 * 5;
	}
	
	public Reward getReward() {
		return reward;
	}

	public void setReward(Reward reward) {
		this.reward = reward;
	}

	public Set<Event> getAttendedEvents() {
		return attendedEvents;
	}

	public void setAttendedEvents(Set<Event> attenedEvents) {
		this.attendedEvents = attenedEvents;
	}

	private Reward reward;
	
	private Set<Event> attendedEvents;
	
	public int getPercentage() {
		return percentage;
	}

	public void setPercentage(int percentage) {
		percentage = percentage;
	}

	public int getBar() {
		return bar;
	}

	public void setBar(int bar) {
		this.bar = bar;
	}

	private int percentage; 
	
	private int bar;
	
}