package springrest.api.controller;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import springrest.api.error.RestException;
import springrest.model.Reward;
import springrest.model.Event;
import springrest.model.User;
import springrest.model.UserReward;
import springrest.model.Tag;
import springrest.model.dao.EventDao;
import springrest.model.dao.RewardDao;
import springrest.model.dao.TagDao;
import springrest.model.dao.UserDao;
import springrest.util.JwtUtils;
import springrest.util.MailUtils;
import springrest.util.Utils;

@CrossOrigin
@RestController
public class RewardController {

	@Autowired
    private RewardDao rewardDao;
	
	@Autowired
    private UserDao userDao;
	
	@Autowired
    private TagDao tagDao;
	
	@Autowired
    private EventDao eventDao;
	
	@Autowired
	private JwtUtils jwt;
	
	@Autowired
	private MailUtils mailUtils;
	
	//get reward by id
	@RequestMapping(value = "/reward/{id}", method = RequestMethod.GET)
    public ResponseEntity<Reward> getReward( @PathVariable Long id,HttpServletRequest request)
    {
    	try {
     		System.out.println("Fetching Reward with id " + id);
        	Reward reward = rewardDao.getReward(id);
        	if (reward == null) {
                System.out.println("Reward with id " + id + " not found");
                return new ResponseEntity<Reward>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<Reward>(reward, HttpStatus.OK);
    	}  catch (Exception e) {
   		 throw new RestException(400, e.getMessage());
   		}
    }
	
	// Get all rewards
    @RequestMapping(value = "/rewards", method = RequestMethod.GET)
    public ResponseEntity<List<Reward>> getRewards(HttpServletRequest request)
    {
    	 try {
     		List<Reward> rewards = rewardDao.getRewards();
     		if(rewards.isEmpty()){
                return new ResponseEntity<List<Reward>>(HttpStatus.NO_CONTENT);//You many decide to return HttpStatus.NOT_FOUND
            }
            return new ResponseEntity<List<Reward>>(rewards, HttpStatus.OK);
    	 }  catch (Exception e) {
    		 throw new RestException(400, e.getMessage());
    	 }
    }
    
	// Get all own rewards
    @RequestMapping(value = "/ownrewards", method = RequestMethod.GET)
    public ResponseEntity<List<Reward>> getOwnRewards(HttpServletRequest request)
    {
    	 try {
    		String token = request.getHeader("Authorization");
     		List<Reward> rewards = rewardDao.getOwnRewards(jwt.decode(token).getClaim("userId").asLong());
     		if(rewards.isEmpty()){
                return new ResponseEntity<List<Reward>>(HttpStatus.NO_CONTENT);//You many decide to return HttpStatus.NOT_FOUND
            }
            return new ResponseEntity<List<Reward>>(rewards, HttpStatus.OK);
    	 }  catch (Exception e) {
    		 throw new RestException(400, e.getMessage());
    	 }
    }
    
    
    @RequestMapping(value = "/addRewardTag/{id}/{tid}", method = RequestMethod.PUT)
   	public ResponseEntity<Reward> addRewardTag(@PathVariable Long id, @PathVariable Long tid,HttpServletRequest request) {
   		try {
       		Reward reward = rewardDao.getReward(id);
    		if (reward == null)
    			return new ResponseEntity<Reward>(HttpStatus.NOT_FOUND);
    		Tag tag = tagDao.getTag(tid);
    		if (tag == null)
    			return new ResponseEntity<Reward>(HttpStatus.NOT_FOUND);
    		reward.getTags().add(tag);
    		for (Event e : tag.getEvents()) {
    			if (e.getStatus() == 1 && e.getEventDate().compareTo(reward.getStartDate()) >= 0 && e.getEventDate().compareTo(reward.getEndDate()) <= 0) {
    				reward.getEvents().add(e);
    			}
    		}
   			return new ResponseEntity<Reward>(rewardDao.saveReward(reward), HttpStatus.OK);
   		} catch (Exception e) {
   			throw new RestException(400, e.getMessage());
   		}
   	}
    
    @RequestMapping(value = "/deleteRewardTag/{id}/{tid}", method = RequestMethod.PUT)
   	public ResponseEntity<Reward> deleteRewardTag(@PathVariable Long id, @PathVariable Long tid,HttpServletRequest request) {
   		try {
       		Reward reward = rewardDao.getReward(id);
    		if (reward == null)
    			return new ResponseEntity<Reward>(HttpStatus.NOT_FOUND);
    		Tag tag = tagDao.getTag(tid);
    		if (tag == null)
    			return new ResponseEntity<Reward>(HttpStatus.NOT_FOUND);
    		reward.getTags().remove(tag);
   			return new ResponseEntity<Reward>(rewardDao.saveReward(reward), HttpStatus.OK);
   		} catch (Exception e) {
   			throw new RestException(400, e.getMessage());
   		}
   	}
    
    @RequestMapping(value = "/addRewardEvent/{id}/{eid}", method = RequestMethod.PUT)
   	public ResponseEntity<Reward> addRewardEvent(@PathVariable Long id, @PathVariable Long eid,HttpServletRequest request) {
   		try {
       		Reward reward = rewardDao.getReward(id);
    		if (reward == null)
    			return new ResponseEntity<Reward>(HttpStatus.NOT_FOUND);
    		Event event = eventDao.getEvent(eid);
    		if (event == null)
    			return new ResponseEntity<Reward>(HttpStatus.NOT_FOUND);
    		reward.getEvents().add(event);
   			return new ResponseEntity<Reward>(rewardDao.saveReward(reward), HttpStatus.OK);
   		} catch (Exception e) {
   			throw new RestException(400, e.getMessage());
   		}
   	}
    

    @RequestMapping(value = "/approvedRewards", method = RequestMethod.GET)
    public ResponseEntity<List<Reward>> getApprovedRewards(HttpServletRequest request)
    {
    	 try {
     		List<Reward> rewards = rewardDao.getApprovedRewards();
     		if(rewards.isEmpty()){
                return new ResponseEntity<List<Reward>>(HttpStatus.NO_CONTENT);//You many decide to return HttpStatus.NOT_FOUND
            }
            return new ResponseEntity<List<Reward>>(rewards, HttpStatus.OK);
    	 }  catch (Exception e) {
    		 throw new RestException(400, e.getMessage());
    	 }
    }
    
    // create a new reward
    @RequestMapping(value = "/rewards", method = RequestMethod.POST)
	public ResponseEntity<Reward> createReward(@RequestBody Reward reward,HttpServletRequest request) {
    	System.out.println("Creating Reward " + reward.getName());
    	try {
    		String token = request.getHeader("Authorization");
     		jwt.decode(token).getClaim("userId").asLong();
     		User requestUser = userDao.getUser(jwt.decode(token).getClaim("userId").asLong());
     		if (reward.getName() == null || reward.getEndDate() == null || reward.getEndDate() == null || reward.getDescription() == null || reward.getCriteria() == null)
        		throw new RestException( 400, "missing required field(s)." );
     		if (Utils.providedByRewardProvider(requestUser))
     			reward.setStatus(1);
     		reward.setSubmitter(requestUser);
     		return new ResponseEntity<Reward>(rewardDao.saveReward(reward),HttpStatus.CREATED);
    	}  catch (Exception e) {
   		 	throw new RestException(400, e.getMessage());
   		}
	}
    
 	   
    // edit a reward
   	@RequestMapping(value = "/reward/{id}", method = RequestMethod.PUT)
   	public ResponseEntity<Reward> updateReward(@PathVariable Long id, @RequestParam("name") String name,@RequestParam("description") String description,@RequestParam("criteria") String criteria,@RequestParam("startDate") String startDate,@RequestParam("endDate") String endDate,HttpServletRequest request) {
   		System.out.println("Updating Reward " + id);
   		try {
    		String token = request.getHeader("Authorization");
     		jwt.decode(token).getClaim("userId").asLong();
     		User requestUser = userDao.getUser(jwt.decode(token).getClaim("userId").asLong());
     		Reward reward = rewardDao.getReward(id);
     		if (!Utils.proceedOnlyIfAdmin (requestUser) && ! reward.getSubmitter().getId().equals(requestUser.getId()))
     			throw new RestException(400, "Invalid Authorization");
    		if (reward == null)
    			return new ResponseEntity<Reward>(HttpStatus.NOT_FOUND);
    		reward.setName(name);
    		reward.setDescription(description);
    		SimpleDateFormat sdf;
    		sdf = new SimpleDateFormat("yyyy-MM-dd");
    		if (startDate != null && !startDate.equals("Start Date")) {
         		sdf.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
         		reward.setStartDate(sdf.parse(startDate));
     		}
    		if (endDate != null && !endDate.equals("End Date")) {
         		sdf.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
         		reward.setEndDate(sdf.parse(endDate));
     		}
    		reward.setCriteria(Integer.valueOf(criteria));
    		if (!Utils.providedByRewardProvider(requestUser)) {
    			reward.setStatus(0);
    		}
   			return new ResponseEntity<Reward>(rewardDao.saveReward(reward), HttpStatus.OK);
    	}  catch (Exception e) {
   		 	throw new RestException(400, e.getMessage());
   		}
   	}
   	
    // delete a reward
 	@RequestMapping(value = "/reward/{id}", method = RequestMethod.DELETE)
 	public ResponseEntity<Reward> deleteReward(@PathVariable("id") long id,HttpServletRequest request) {
        System.out.println("Fetching & Deleting Reward with id " + id);
        try {
    		String token = request.getHeader("Authorization");
     		jwt.decode(token).getClaim("userId").asLong();
     		User requestUser = userDao.getUser(jwt.decode(token).getClaim("userId").asLong());
     		Reward reward = rewardDao.getReward(id);
     		if (!Utils.proceedOnlyIfAdmin (requestUser) && !reward.getSubmitter().getId().equals(requestUser.getId()))
     			throw new RestException(400, "Invalid Authorization");            if (reward == null) {
                System.out.println("Unable to delete. Reward with id " + id + " not found");
                return new ResponseEntity<Reward>(HttpStatus.NOT_FOUND);
            }
            rewardDao.deleteReward(reward);
        	return new ResponseEntity<Reward>(HttpStatus.NO_CONTENT);
    	}  catch (Exception e) {
   		 	throw new RestException(400, e.getMessage());
   		}
    }
 	
 	// approve a reward
	@RequestMapping(value = "/reward/approve/{id}", method = RequestMethod.PUT)
   	public ResponseEntity<Reward> approveReward(@PathVariable Long id,HttpServletRequest request) {
		try {
    		String token = request.getHeader("Authorization");
     		jwt.decode(token).getClaim("userId").asLong();
     		User requestUser = userDao.getUser(jwt.decode(token).getClaim("userId").asLong());
     		if (!Utils.proceedOnlyIfAdmin (requestUser))
     			throw new RestException(400, "Invalid Authorization");
    	   	Reward reward = rewardDao.getReward(id);
    		if (reward == null)
    			return new ResponseEntity<Reward>(HttpStatus.NOT_FOUND);
    		reward.setStatus(1);
    		rewardDao.saveReward(reward);
    		mailUtils.sendMail(reward.getSubmitter().getEmail(), "Your reward " + reward.getName() + " has been approved", "Congratulations!Your reward application has been approved.");
       		return new ResponseEntity<Reward>(reward, HttpStatus.OK);
    	}  catch (Exception e) {
   		 	throw new RestException(400, e.getMessage());
   		}
   	}
	
	// reject a reward
	@RequestMapping(value = "/reward/reject/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Reward> rejectReward(@PathVariable Long id,HttpServletRequest request) {
	   	System.out.println("Rejecting Reward " + id);
	   	try {
    		String token = request.getHeader("Authorization");
     		jwt.decode(token).getClaim("userId").asLong();
     		User requestUser = userDao.getUser(jwt.decode(token).getClaim("userId").asLong());
     		if (!Utils.proceedOnlyIfAdmin (requestUser))
     			throw new RestException(400, "Invalid Authorization");
    	   	Reward reward = rewardDao.getReward(id);
    		System.out.println(reward.getId());
    		if (reward == null)
    			return new ResponseEntity<Reward>(HttpStatus.NOT_FOUND);
    		reward.setStatus(2);
    		rewardDao.saveReward(reward);
    		mailUtils.sendMail(reward.getSubmitter().getEmail(), "Your reward " + reward.getName() + " has been rejected", "Sorry.Your reward application has been rejected.");
       		return new ResponseEntity<Reward>(reward, HttpStatus.OK);
    	}  catch (Exception e) {
   		 	throw new RestException(400, e.getMessage());
   		}
	}

	//get reward by id
	@RequestMapping(value = "/reward/{id}/events", method = RequestMethod.GET)
	public ResponseEntity<Set<Event>> getQualifiedEvents( @PathVariable Long id,HttpServletRequest request)
    {
		System.out.println("xxxx");
    	try {
     		System.out.println("Fetching Reward with id " + id);
        	Reward reward = rewardDao.getReward(id);
        	if (reward == null) {
                System.out.println("Reward with id " + id + " not found");
                return new ResponseEntity<Set<Event>>(HttpStatus.NOT_FOUND);
            }
        	Set<Event> events = new HashSet<Event>();
        	events.addAll(reward.getEvents());
            return new ResponseEntity<Set<Event>>(events, HttpStatus.OK);
    	}  catch (Exception e) {
   		 throw new RestException(400, e.getMessage());
   		}
    }
	
	@RequestMapping(value = "/reward/{id}/users", method = RequestMethod.GET)
	public ResponseEntity<Set<User>> getQualifiedUsers( @PathVariable Long id,HttpServletRequest request)
    {
    	try {
     		System.out.println("Fetching Reward with id " + id);
        	Reward reward = rewardDao.getReward(id);
        	if (reward == null) {
                System.out.println("Reward with id " + id + " not found");
                return new ResponseEntity<Set<User>>(HttpStatus.NOT_FOUND);
            }
        	Set<Event> events = new HashSet<Event>();
        	Set<User> users = new HashSet<User>();
        	Set<User> qualifiedUsers = new HashSet<User>();
        	Set<Event> result = new HashSet<Event>();
         	events.addAll(reward.getEvents());
        	for (Event event:events) {
        		users.addAll(event.getAttendees());
        	}
        	for (User user:users) {
        		result.clear();
        		result.addAll(events);
        		result.retainAll(user.getEvents());
        		if (result.size() >= reward.getCriteria()) {
        			qualifiedUsers.add(user);
        		}
        	}
            return new ResponseEntity<Set<User>>(qualifiedUsers, HttpStatus.OK);
    	}  catch (Exception e) {
   		 throw new RestException(400, e.getMessage());
   		}
    }
	
	@RequestMapping(value = "/potentialRewards", method = RequestMethod.GET)
 	public ResponseEntity<Set<UserReward>> getPotentialRewards( HttpServletRequest request) {
 		try {
    		String token = request.getHeader("Authorization");
    		jwt.decode(token).getClaim("userId").asLong();
    		User requestUser = userDao.getUser(jwt.decode(token).getClaim("userId").asLong());
    		Set<Event> events = new HashSet<Event>();
    		events.addAll(requestUser.getEvents());
    		Set<Tag> tags = new HashSet<Tag>();
    		Set<Reward> rewards = new HashSet<Reward>();
    		Set<UserReward> userReward = new HashSet<UserReward>();
    		for (Event event:events) {
    			tags.addAll(event.getTags());
    			rewards.addAll(event.getRewards());
    		}
    		for (Tag tag:tags) {
    			rewards.addAll(tag.getRewards());
    		}
    		for (Reward reward:rewards) {
    			for (Tag tag:reward.getTags())
    				reward.getEvents().addAll(tag.getEvents());
    			Set<Event> result = new HashSet<Event>();
    			result.clear();
    			result.addAll(reward.getEvents());
    			result.retainAll(events);
    			if (result.size() > 0) {
    				userReward.add(new UserReward(requestUser.getId(),reward.getId(),reward.getName(),reward.getCriteria(),result.size()));
    			}
    		}
    		return new ResponseEntity<Set<UserReward>>(userReward, HttpStatus.OK);
    	} catch (Exception e) {
    		throw new RestException(400, e.getMessage());
    	}
 	}
}
