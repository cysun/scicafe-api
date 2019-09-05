package springrest.api.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import springrest.api.error.RestException;
import springrest.model.Event;
import springrest.model.Tag;
import springrest.model.User;
import springrest.model.dao.TagDao;
import springrest.model.dao.UserDao;
import springrest.util.Utils;
import javax.persistence.Entity;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class TagController {
	
	@Autowired
    private TagDao tagDao;
	
	@Autowired
    private UserDao userDao;

    // Get an tag by id
    @RequestMapping(value = "/tag/{id}", method = RequestMethod.GET)
    public ResponseEntity<Tag> getTag( @PathVariable Long id ,HttpServletRequest request)
    {
    	try {
    		String token = request.getHeader("Authorization");
      		Utils.decode(token).getClaim("userId").asLong();
      		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
      		if (!Utils.proceedOnlyIfAdminOrRegular(requestUser))
      			throw new RestException(400, "Invalid Authorization");
      		System.out.println("Fetching Tag with id " + id);
        	Tag tag = tagDao.getTag(id);
        	if (tag == null) {
                System.out.println("Tag with id " + id + " not found");
                return new ResponseEntity<Tag>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<Tag>(tag, HttpStatus.OK);
    	} catch (Exception e) {
    		throw new RestException(400, e.getMessage());
    	}
    }

    // Get all tags
    @RequestMapping(value = "/tags", method = RequestMethod.GET)
    public ResponseEntity<List<Tag>> getTags(HttpServletRequest request)
    {
         try {
//     		String token = request.getHeader("Authorization");
//      		Utils.decode(token).getClaim("userId").asLong();
//      		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
//      		if (!Utils.proceedOnlyIfAdmin(requestUser))
//      			throw new RestException(400, "Invalid Authorization");
      		List<Tag> tags = tagDao.getTags();
      		if(tags.isEmpty()){
                return new ResponseEntity<List<Tag>>(HttpStatus.NO_CONTENT);//You many decide to return HttpStatus.NOT_FOUND
            }
            return new ResponseEntity<List<Tag>>(tags, HttpStatus.OK);
     	 }  catch (Exception e) {
     		 throw new RestException(400, e.getMessage());
     	 }
    }
    
    // create a new tag
    @RequestMapping(value = "/tags", method = RequestMethod.POST)
	public ResponseEntity<Tag> createTag(@RequestBody Tag tag,HttpServletRequest request) {
    	try {
     		String token = request.getHeader("Authorization");
      		Utils.decode(token).getClaim("userId").asLong();
      		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
      		if (!Utils.proceedOnlyIfAdmin(requestUser))
      			throw new RestException(400, "Invalid Authorization");
      		System.out.println("Creating Tag " + tag.getName());
        	if (tag.getName() == null)
        		throw new RestException( 400, "missing required field(s)." );
        	if (tagDao.isTagExists(tag)) {
                System.out.println("A Tag with name " + tag.getName() + " already exist");
                return new ResponseEntity<Tag>(HttpStatus.CONFLICT);
            }
        	return new ResponseEntity<Tag>(tagDao.saveTag(tag),HttpStatus.CREATED);
     	 }  catch (Exception e) {
     		 throw new RestException(400, e.getMessage());
     	 }
    	
	}
    
    // edit a tag
   	@RequestMapping(value = "/tag/{id}", method = RequestMethod.PUT)
   	public ResponseEntity<Tag> updateTag(@PathVariable Long id, @RequestBody Tag newTag,HttpServletRequest request) {
   		try {
   			String token = request.getHeader("Authorization");
      		Utils.decode(token).getClaim("userId").asLong();
      		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
      		if (!Utils.proceedOnlyIfAdmin(requestUser))
      			throw new RestException(400, "Invalid Authorization");
      		System.out.println("Updating Tag " + id);
       		Tag tag = tagDao.getTag(id);
    		if (tag == null)
    			return new ResponseEntity<Tag>(HttpStatus.NOT_FOUND);
    		tag.setName(newTag.getName());
   			return new ResponseEntity<Tag>(tagDao.saveTag(tag), HttpStatus.OK);
   		} catch (Exception e) {
   			throw new RestException(400, e.getMessage());
   		}
   	}
   	
    // delete a tag
 	@RequestMapping(value = "/tag/{id}", method = RequestMethod.DELETE)
 	public ResponseEntity<Tag> deleteTag(@PathVariable("id") long id,HttpServletRequest request) {
        try {
        	String token = request.getHeader("Authorization");
      		Utils.decode(token).getClaim("userId").asLong();
      		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
      		if (!Utils.proceedOnlyIfAdmin(requestUser))
      			throw new RestException(400, "Invalid Authorization");
        	System.out.println("Fetching & Deleting Tag with id " + id);
            Tag tag = tagDao.getTag(id);
            if (tag == null) {
                System.out.println("Unable to delete. Tag with id " + id + " not found");
                return new ResponseEntity<Tag>(HttpStatus.NOT_FOUND);
            }
        	tagDao.deleteTag(tag);
        	return new ResponseEntity<Tag>(HttpStatus.OK);
        } catch (Exception e) {
        	throw new RestException(400, e.getMessage());
        }
        
    }
	
}
