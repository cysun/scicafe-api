package springrest.api.controller;


import java.sql.Timestamp;
import java.util.Date;
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
import springrest.model.News;
import springrest.model.Program;
import springrest.model.News;
import springrest.model.User;
import springrest.model.dao.NewsDao;
import springrest.model.dao.UserDao;
import springrest.util.Utils;
import javax.persistence.Entity;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class NewsController {

	@Autowired
    private UserDao userDao;
	
	@Autowired
    private NewsDao newsDao;
	
	@RequestMapping(value = "/news/{id}", method = RequestMethod.GET)
    public ResponseEntity<News> getNews( @PathVariable Long id)
    {
    	try {
      		System.out.println("Fetching News with id " + id);
        	News news = newsDao.getNews(id);
        	System.out.println(news.getContent());
        	if (news == null) {
                System.out.println("news with id " + id + " not found");
                return new ResponseEntity<News>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<News>(news, HttpStatus.OK);
    	} catch (Exception e) {
    		throw new RestException(400, e.getMessage());
    	}
    }
	
	// Get all news
    @RequestMapping(value = "/news", method = RequestMethod.GET)
    public ResponseEntity<List<News>> getAllNews(HttpServletRequest request)
    {
         try {
      		List<News> news = newsDao.getAllNews();
      		if(news.isEmpty()){
                return new ResponseEntity<List<News>>(HttpStatus.NO_CONTENT);//You many decide to return HttpStatus.NOT_FOUND
            }
            return new ResponseEntity<List<News>>(news, HttpStatus.OK);
     	 }  catch (Exception e) {
     		 throw new RestException(400, e.getMessage());
     	 }
    }
    
    // Get all top news
    @RequestMapping(value = "/topNews", method = RequestMethod.GET)
    public ResponseEntity<List<News>> getTopNews(HttpServletRequest request)
    {
         try {
      		List<News> news = newsDao.getTopNews();
      		if(news.isEmpty()){
                return new ResponseEntity<List<News>>(HttpStatus.NO_CONTENT);//You many decide to return HttpStatus.NOT_FOUND
            }
            return new ResponseEntity<List<News>>(news, HttpStatus.OK);
     	 }  catch (Exception e) {
     		 throw new RestException(400, e.getMessage());
     	 }
    }
    
 // create a new news
    @RequestMapping(value = "/news", method = RequestMethod.POST)
	public ResponseEntity<News> createNews(@RequestBody News news,HttpServletRequest request) {

     		String token = request.getHeader("Authorization");
      		Utils.decode(token).getClaim("userId").asLong();
      		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
      		if (!Utils.proceedOnlyIfAdmin(requestUser))
      			throw new RestException(400, "Invalid Authorization");
      		System.out.println("Creating News " + news.getTitle());
      		if (news.getIsTop()==null)
      			news.setIsTop("No");
      		news.setPostedDate(new Timestamp(System.currentTimeMillis()));
        	if (news.getTitle() == null || news.getAuthor() == null || news.getContent() == null)
        		throw new RestException( 400, "missing required field(s)." );
        	return new ResponseEntity<News>(newsDao.saveNews(news),HttpStatus.CREATED);
	}
    
 // edit a news
   	@RequestMapping(value = "/news/{id}", method = RequestMethod.PUT)
   	public ResponseEntity<News> updateNews(@PathVariable Long id, @RequestBody News newNews,HttpServletRequest request) {
   		try {
   			String token = request.getHeader("Authorization");
      		Utils.decode(token).getClaim("userId").asLong();
      		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
      		if (!Utils.proceedOnlyIfAdmin(requestUser))
      			throw new RestException(400, "Invalid Authorization");
      		System.out.println("Updating News " + id);
       		News news = newsDao.getNews(id);
    		if (news == null)
    			return new ResponseEntity<News>(HttpStatus.NOT_FOUND);
    		news.setAuthor(newNews.getAuthor());
   			news.setContent(newNews.getContent());
   			news.setTitle(newNews.getTitle());
   			news.setIsTop(newNews.getIsTop());
   			news.setImageUrl(newNews.getImageUrl());
      		news.setPostedDate(new Timestamp(System.currentTimeMillis()));
   			return new ResponseEntity<News>(newsDao.saveNews(news), HttpStatus.OK);
   		} catch (Exception e) {
   			throw new RestException(400, e.getMessage());
   		}
   	}
    
    // delete a news
  	@RequestMapping(value = "/news/{id}", method = RequestMethod.DELETE)
  	public ResponseEntity<News> deleteNews(@PathVariable("id") long id,HttpServletRequest request) {
         try {
         	String token = request.getHeader("Authorization");
       		Utils.decode(token).getClaim("userId").asLong();
       		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
       		if (!Utils.proceedOnlyIfAdmin(requestUser))
       			throw new RestException(400, "Invalid Authorization");
         	System.out.println("Fetching & Deleting News with id " + id);
            News news = newsDao.getNews(id);
            if (news == null) {
                System.out.println("Unable to delete. News with id " + id + " not found");
                return new ResponseEntity<News>(HttpStatus.NOT_FOUND);
            }
         	newsDao.deleteNews(news);
         	return new ResponseEntity<News>(HttpStatus.NO_CONTENT);
         } catch (Exception e) {
         	throw new RestException(400, e.getMessage());
         }
         
     }
	
}
