package springrest.api.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
import springrest.model.Reward;
import springrest.model.Reward;
import springrest.model.User;
import springrest.model.dao.RewardDao;
import springrest.model.dao.UserDao;
import springrest.util.Utils;
import javax.persistence.Entity;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class RewardController {

	@Autowired
    private RewardDao rewardDao;
	
	@Autowired
    private UserDao userDao;
	
	//get reward by id
	@RequestMapping(value = "/reward/{id}", method = RequestMethod.GET)
    public ResponseEntity<Reward> getReward( @PathVariable Long id,HttpServletRequest request)
    {
    	try {
//    		String token = request.getHeader("Authorization");
//     		Utils.decode(token).getClaim("userId").asLong();
//     		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
//     		if (!Utils.proceedOnlyIfAdminOrRegular(requestUser))
//     			throw new RestException(400, "Invalid Authorization");
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
//    		String token = request.getHeader("Authorization");
//     		Utils.decode(token).getClaim("userId").asLong();
//     		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
//     		if (!Utils.proceedOnlyIfAdminOrRegular(requestUser))
//     			throw new RestException(400, "Invalid Authorization");
     		List<Reward> rewards = rewardDao.getRewards();
     		if(rewards.isEmpty()){
                return new ResponseEntity<List<Reward>>(HttpStatus.NO_CONTENT);//You many decide to return HttpStatus.NOT_FOUND
            }
            return new ResponseEntity<List<Reward>>(rewards, HttpStatus.OK);
    	 }  catch (Exception e) {
    		 throw new RestException(400, e.getMessage());
    	 }
    }
    
    @RequestMapping(value = "/approvedRewards", method = RequestMethod.GET)
    public ResponseEntity<List<Reward>> getApprovedRewards(HttpServletRequest request)
    {
    	 try {
//    		String token = request.getHeader("Authorization");
//     		Utils.decode(token).getClaim("userId").asLong();
//     		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
//     		if (!Utils.proceedOnlyIfAdminOrRegular(requestUser))
//     			throw new RestException(400, "Invalid Authorization");
     		List<Reward> rewards = rewardDao.getRewards();
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
     		Utils.decode(token).getClaim("userId").asLong();
     		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
//     		if (!Utils.proceedOnlyIfAdminOrRegular (requestUser))
//     			throw new RestException(400, "Invalid Authorization");
     		if (reward.getName() == null || reward.getDescription() == null || reward.getCriteria() == null)
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
   	public ResponseEntity<Reward> updateReward(@PathVariable Long id, @RequestBody Reward newReward,HttpServletRequest request) {
   		System.out.println("Updating Reward " + id);
   		try {
    		String token = request.getHeader("Authorization");
     		Utils.decode(token).getClaim("userId").asLong();
     		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
     		if (!Utils.proceedOnlyIfAdmin (requestUser))
     			throw new RestException(400, "Invalid Authorization");
     		Reward reward = rewardDao.getReward(id);
    		if (reward == null)
    			return new ResponseEntity<Reward>(HttpStatus.NOT_FOUND);
    		reward.setName(newReward.getName());
    		reward.setDescription(newReward.getDescription());
    		reward.setStartTime(newReward.getStartTime());
    		reward.setEndTime(newReward.getEndTime());
    		reward.setTags(newReward.getTags());
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
     		Utils.decode(token).getClaim("userId").asLong();
     		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
     		if (!Utils.proceedOnlyIfAdmin (requestUser))
     			throw new RestException(400, "Invalid Authorization");
     		Reward reward = rewardDao.getReward(id);
            if (reward == null) {
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
     		Utils.decode(token).getClaim("userId").asLong();
     		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
     		if (!Utils.proceedOnlyIfAdmin (requestUser))
     			throw new RestException(400, "Invalid Authorization");
    	   	Reward reward = rewardDao.getReward(id);
    		if (reward == null)
    			return new ResponseEntity<Reward>(HttpStatus.NOT_FOUND);
    		reward.setStatus(1);
       		return new ResponseEntity<Reward>(rewardDao.saveReward(reward), HttpStatus.OK);
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
     		Utils.decode(token).getClaim("userId").asLong();
     		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
     		if (!Utils.proceedOnlyIfAdmin (requestUser))
     			throw new RestException(400, "Invalid Authorization");
    	   	Reward reward = rewardDao.getReward(id);
    		reward.setStatus(2);
    		System.out.println(reward.getId());
    		if (reward == null)
    			return new ResponseEntity<Reward>(HttpStatus.NOT_FOUND);
       		return new ResponseEntity<Reward>(rewardDao.saveReward(reward), HttpStatus.OK);
    	}  catch (Exception e) {
   		 	throw new RestException(400, e.getMessage());
   		}
	}
}
