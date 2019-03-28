package springrest.model.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import springrest.model.Event;
import springrest.model.dao.EventDao;
import javax.persistence.Entity;

@Repository
public class EventDaoImpl implements EventDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Event getEvent( Long id )
    {
        return entityManager.find( Event.class, id );
    }

    @Override
    public List<Event> getEvents()
    {
        return entityManager.createQuery( "from Event order by id", Event.class )
            .getResultList();
    }
    
    @Override
    public List<Event> getApprovedEvents()
    {
        return entityManager.createQuery( "select  e from Event e where e.status = 1 order by eventDate", Event.class )
            .getResultList();
    }

    @Override
    @Transactional
    public Event saveEvent( Event event )
    {
        return entityManager.merge( event );
    }
    
    @Override
   	@Transactional
   	public boolean deleteEvent(Event event)
   	{
       	try {
       		entityManager.remove(event);
       		return true;
       	} catch (Exception e) {
       		return false;
       	}
   	}

   	@Override
   	public boolean isEventExists(Event event) {
   		return entityManager.contains(event);
   	}

}