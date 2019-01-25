package springrest.api.controller;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import springrest.api.error.RestException;
import springrest.model.Event;
import springrest.model.User;
import springrest.model.dao.EventDao;
import springrest.model.dao.UserDao;
import springrest.util.Utils;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class EventController {
	
	@Autowired
    private EventDao eventDao;
	
	@Autowired
    private UserDao userDao;

	// Get an event by id
    @RequestMapping(value = "/event/{id}", method = RequestMethod.GET)
    public ResponseEntity<Event> getEvent( @PathVariable Long id,HttpServletRequest request)
    {
    	try {
    		String token = request.getHeader("Authorization");
     		Utils.decode(token).getClaim("userId").asLong();
     		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
     		if (!Utils.proceedOnlyIfAdminOrRegular(requestUser))
     			throw new RestException(400, "Invalid Authorization");
     		System.out.println("Fetching Event with id " + id);
        	Event event = eventDao.getEvent(id);
        	if (event == null) {
                System.out.println("Event with id " + id + " not found");
                return new ResponseEntity<Event>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<Event>(event, HttpStatus.OK);
    	}  catch (Exception e) {
   		 throw new RestException(400, e.getMessage());
   		}
    	
    }

    // Get all events
    @RequestMapping(value = "/events", method = RequestMethod.GET)
    public ResponseEntity<List<Event>> getEvents(HttpServletRequest request)
    {
    	 try {
    		String token = request.getHeader("Authorization");
     		Utils.decode(token).getClaim("userId").asLong();
     		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
     		if (!Utils.proceedOnlyIfAdminOrRegular(requestUser))
     			throw new RestException(400, "Invalid Authorization");
     		List<Event> events = eventDao.getEvents();
     		if(events.isEmpty()){
                return new ResponseEntity<List<Event>>(HttpStatus.NO_CONTENT);//You many decide to return HttpStatus.NOT_FOUND
            }
            return new ResponseEntity<List<Event>>(events, HttpStatus.OK);
    	 }  catch (Exception e) {
    		 throw new RestException(400, e.getMessage());
    	 }
    	 
    }
    
    // Get all attendees
    @RequestMapping(value = "/event/{id}/attendees", method = RequestMethod.GET)
    public ResponseEntity<Set<User>> getAttendees(@PathVariable Long id,HttpServletRequest request)
    {
    	try {
    		String token = request.getHeader("Authorization");
     		Utils.decode(token).getClaim("userId").asLong();
     		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
     		if (!Utils.proceedOnlyIfAdmin (requestUser))
     			throw new RestException(400, "Invalid Authorization");
     		System.out.println("Fetching Event with id " + id);
     		Set<User> attendees = (Set<User>) eventDao.getEvent(id).getAttendees();
     		System.out.println(attendees);
       	 	if(attendees.isEmpty()){
                return new ResponseEntity<Set<User>>(HttpStatus.NO_CONTENT);//You many decide to return HttpStatus.NOT_FOUND
            }
            return new ResponseEntity<Set<User>>(attendees, HttpStatus.OK);
    	}  catch (Exception e) {
   		 	throw new RestException(400, e.getMessage());
   		}
    	 
    }
    
    // add an attendees
    @RequestMapping(value = "/event/{id}/attendee", method = RequestMethod.POST)
    public ResponseEntity<Set<User>> addAttendee(@PathVariable Long id,@RequestBody JSONObject json_object,HttpServletRequest request)
    {
    	try {
    		String token = request.getHeader("Authorization");
     		Utils.decode(token).getClaim("userId").asLong();
     		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
     		if (!Utils.proceedOnlyIfAdmin (requestUser))
     			throw new RestException(400, "Invalid Authorization");
     		User user = userDao.getUser(Long.parseLong(String.valueOf(json_object.get("id"))));
        	Event event = eventDao.getEvent(id);
        	if (event == null)
    			return new ResponseEntity<Set<User>>(HttpStatus.NOT_FOUND);
        	event.getAttendees().add(user);
    		eventDao.saveEvent(event);
    		return new ResponseEntity<Set<User>>(event.getAttendees(), HttpStatus.OK);
    	}  catch (Exception e) {
   		 	throw new RestException(400, e.getMessage());
   		}
    }
    
    // create a new event
    @RequestMapping(value = "/events", method = RequestMethod.POST)
	public ResponseEntity<Event> createEvent(@RequestBody Event event,HttpServletRequest request) {
    	System.out.println("Creating Event " + event.getName());
    	try {
    		String token = request.getHeader("Authorization");
     		Utils.decode(token).getClaim("userId").asLong();
     		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
     		if (!Utils.proceedOnlyIfAdminOrRegular (requestUser))
     			throw new RestException(400, "Invalid Authorization");
     		if (event.getLocation() == null || event.getStartTime() == null || event.getEndTime() == null || event.getName() == null)
        		throw new RestException( 400, "missing required field(s)." );
     		if (Utils.orgnaziedByEventOrganizor(requestUser))
     			event.setStatus(springrest.model.Event.Status.approved);
     		else
     			event.setStatus(springrest.model.Event.Status.submitted);
     		return new ResponseEntity<Event>(eventDao.saveEvent(event),HttpStatus.CREATED);
    	}  catch (Exception e) {
   		 	throw new RestException(400, e.getMessage());
   		}
	}
    
    // edit a event
   	@RequestMapping(value = "/event/{id}", method = RequestMethod.PUT)
   	public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event newEvent,HttpServletRequest request) {
   		System.out.println("Updating Event " + id);
   		try {
    		String token = request.getHeader("Authorization");
     		Utils.decode(token).getClaim("userId").asLong();
     		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
     		if (!Utils.proceedOnlyIfAdmin (requestUser))
     			throw new RestException(400, "Invalid Authorization");
     		Event event = eventDao.getEvent(id);
    		if (event == null)
    			return new ResponseEntity<Event>(HttpStatus.NOT_FOUND);
    		event.setName(newEvent.getName());
    		event.setDescription(newEvent.getDescription());
    		event.setLocation(newEvent.getLocation());
    		event.setStartTime(newEvent.getStartTime());
    		event.setEndTime(newEvent.getEndTime());
    		event.setTags(newEvent.getTags());
   			return new ResponseEntity<Event>(eventDao.saveEvent(event), HttpStatus.OK);
    	}  catch (Exception e) {
   		 	throw new RestException(400, e.getMessage());
   		}
   	}
   	
    // delete a event
 	@RequestMapping(value = "/event/{id}", method = RequestMethod.DELETE)
 	public ResponseEntity<Event> deleteEvent(@PathVariable("id") long id,HttpServletRequest request) {
        System.out.println("Fetching & Deleting Event with id " + id);
        try {
    		String token = request.getHeader("Authorization");
     		Utils.decode(token).getClaim("userId").asLong();
     		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
     		if (!Utils.proceedOnlyIfAdmin (requestUser))
     			throw new RestException(400, "Invalid Authorization");
     		Event event = eventDao.getEvent(id);
            if (event == null) {
                System.out.println("Unable to delete. Event with id " + id + " not found");
                return new ResponseEntity<Event>(HttpStatus.NOT_FOUND);
            }
            eventDao.deleteEvent(event);
        	return new ResponseEntity<Event>(HttpStatus.NO_CONTENT);
    	}  catch (Exception e) {
   		 	throw new RestException(400, e.getMessage());
   		}
    }
 	
 	// approve a event
	@RequestMapping(value = "/event/approve/{id}", method = RequestMethod.PUT)
   	public ResponseEntity<Event> approveEvent(@PathVariable Long id,HttpServletRequest request) {
		try {
    		String token = request.getHeader("Authorization");
     		Utils.decode(token).getClaim("userId").asLong();
     		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
     		if (!Utils.proceedOnlyIfAdmin (requestUser))
     			throw new RestException(400, "Invalid Authorization");
    	   	Event event = eventDao.getEvent(id);
    		if (event == null)
    			return new ResponseEntity<Event>(HttpStatus.NOT_FOUND);
    		event.setStatus(springrest.model.Event.Status.approved);
       		return new ResponseEntity<Event>(eventDao.saveEvent(event), HttpStatus.OK);
    	}  catch (Exception e) {
   		 	throw new RestException(400, e.getMessage());
   		}
   	}
	
	// reject a event
	@RequestMapping(value = "/event/reject/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Event> rejectEvent(@PathVariable Long id,HttpServletRequest request) {
	   	System.out.println("Rejecting Event " + id);
	   	try {
    		String token = request.getHeader("Authorization");
     		Utils.decode(token).getClaim("userId").asLong();
     		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
     		if (!Utils.proceedOnlyIfAdmin (requestUser))
     			throw new RestException(400, "Invalid Authorization");
    	   	Event event = eventDao.getEvent(id);
    		event.setStatus(springrest.model.Event.Status.rejected);
    		System.out.println(event.getId());
    		if (event == null)
    			return new ResponseEntity<Event>(HttpStatus.NOT_FOUND);
       		return new ResponseEntity<Event>(eventDao.saveEvent(event), HttpStatus.OK);
    	}  catch (Exception e) {
   		 	throw new RestException(400, e.getMessage());
   		}
	}
}