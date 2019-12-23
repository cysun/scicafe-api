package springrest.api.controller;


import java.sql.Timestamp;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import springrest.api.error.RestException;
import springrest.model.News;
import springrest.model.User;
import springrest.model.dao.NewsDao;
import springrest.model.dao.UserDao;
import springrest.model.service.NewsImageService;
import springrest.util.JwtUtils;
import springrest.util.Utils;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.HttpHeaders;


@CrossOrigin
@RestController
@Component
public class NewsController {
	
	@Value("${scicafe.api.url}")
	private String APIURL;
	
	@Autowired
	private JwtUtils jwt;

	@Autowired
    private UserDao userDao;
	
	@Autowired
    private NewsDao newsDao;
	
	@Autowired
	private NewsImageService newsImageService;
	
	@RequestMapping(value = "/news/{id}", method = RequestMethod.GET)
    public ResponseEntity<News> getNews( @PathVariable Long id)
    {
    	try {
      		System.out.println("Fetching News with id " + id);
        	News news = newsDao.getNews(id);
        	System.out.println(news.getContent());
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
	public ResponseEntity<News> createNews(HttpServletRequest request,@RequestParam(value = "image",required=false) MultipartFile image,@RequestParam("author") String author,@RequestParam("title") String title,@RequestParam("content") String content,@RequestParam("isTop") String isTop) {
    	try {
    			News news = new News();
         		String token = request.getHeader("Authorization");
          		jwt.decode(token).getClaim("userId").asLong();
          		User requestUser = userDao.getUser(jwt.decode(token).getClaim("userId").asLong());
          		if (!Utils.proceedOnlyIfAdmin(requestUser))
          			throw new RestException(400, "Invalid Authorization");
          		news.setAuthor(author);
          		news.setContent(content);
          		news.setTitle(title);
          		news.setIsTop(isTop);
          		System.out.println("Creating News " + news.getTitle());
          		if (isTop==null)
          			news.setIsTop("No");
          		news.setPostedDate(new Timestamp(System.currentTimeMillis()));
            	if (news.getTitle() == null || news.getAuthor() == null || news.getContent() == null)
            		throw new RestException( 400, "missing required field(s)." );
            	if (image == null||image.isEmpty()) {
            		System.out.println("xxxxxxxxxxxxxxxx");
            		news.setImageUrl("assets/images/news/default.png");
            	} else {
            		news = newsDao.saveNews(news);
                	String fileName = image.getOriginalFilename();
              		String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
                	news.setImageUrl(APIURL+"/news-image/news"+news.getId()+"."+fileType);
                	this.newsImageService.store(image, "news"+news.getId()+"."+fileType);
            	}
            	return new ResponseEntity<News>(newsDao.saveNews(news),HttpStatus.CREATED);
    	 	} catch (Exception e) {
    	 		 throw new RestException(400, e.getMessage());
    	 	}
	}
    
    @RequestMapping(value = "/news-image/{imageName}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String imageName) {
      Resource file = this.newsImageService.loadFile(imageName);
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
          .body(file);
    }
    
 // edit a news
   	@RequestMapping(value = "/news/{id}", method = RequestMethod.PUT)
   	public ResponseEntity<News> updateNews(@PathVariable Long id, HttpServletRequest request,@RequestParam(value = "image",required=false) MultipartFile image,@RequestParam("author") String author,@RequestParam("title") String title,@RequestParam("content") String content,@RequestParam("isTop") String isTop) {
   		try {
   			String token = request.getHeader("Authorization");
      		jwt.decode(token).getClaim("userId").asLong();
      		User requestUser = userDao.getUser(jwt.decode(token).getClaim("userId").asLong());
      		if (!Utils.proceedOnlyIfAdmin(requestUser))
      			throw new RestException(400, "Invalid Authorization");
      		System.out.println("Updating News " + id);
       		News news = newsDao.getNews(id);
    		if (news == null)
    			return new ResponseEntity<News>(HttpStatus.NOT_FOUND);
    		if (image != null && !image.isEmpty()) {
            	String fileName = image.getOriginalFilename();
          		String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
            	news.setImageUrl(APIURL+"/news-image/news"+news.getId()+"."+fileType);
            	this.newsImageService.store(image, "news"+news.getId()+"."+fileType);
        	}
    		news.setAuthor(author);
   			news.setContent(content);
   			news.setTitle(title);
   			news.setIsTop(isTop);
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
       		jwt.decode(token).getClaim("userId").asLong();
       		User requestUser = userDao.getUser(jwt.decode(token).getClaim("userId").asLong());
       		if (!Utils.proceedOnlyIfAdmin(requestUser))
       			throw new RestException(400, "Invalid Authorization");
         	System.out.println("Fetching & Deleting News with id " + id);
            News news = newsDao.getNews(id);
            if (news == null) {
                System.out.println("Unable to delete. News with id " + id + " not found");
                return new ResponseEntity<News>(HttpStatus.NOT_FOUND);
            }
            this.newsImageService.deleteFile(news.getImageUrl().substring(news.getImageUrl().lastIndexOf('/')+1));
         	newsDao.deleteNews(news);
         	return new ResponseEntity<News>(HttpStatus.NO_CONTENT);
         } catch (Exception e) {
         	throw new RestException(400, e.getMessage());
         }
         
     }
	
}
