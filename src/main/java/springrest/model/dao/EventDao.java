package springrest.model.dao;

import java.util.List;

import springrest.model.Event;
import javax.persistence.Entity;


public interface EventDao {

    Event getEvent( Long id );

    List<Event> getEvents();
    
    List<Event> getOwnEvents(Long id);
    
    List<Event> getApprovedEvents();

    Event saveEvent ( Event  event);

    public boolean deleteEvent(Event event);
    
    public boolean isEventExists(Event event);
}