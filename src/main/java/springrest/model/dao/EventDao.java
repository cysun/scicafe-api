package springrest.model.dao;

import java.util.List;
import java.util.Set;

import springrest.model.Event;
import springrest.model.Tag;


public interface EventDao {

    Event getEvent( Long id );

    List<Event> getEvents();
    
    List<Event> getOwnEvents(Long id);
    
    List<Event> getOwnApprovedEvents(Long id);
    
    List<Event> getOwnPendingEvents(Long id);
    
    List<Event> getOwnRejectedEvents(Long id);
    
    List<Event> getApprovedEvents();
    
    List<Event> getPendingEvents();
    
    List<Event> getRejectedEvents();

    Event saveEvent ( Event  event);

    public boolean deleteEvent(Event event);
    
    public boolean isEventExists(Event event);
}