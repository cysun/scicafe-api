package springrest.api.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import springrest.api.error.RestException;
import springrest.model.Event;
import springrest.model.Program;
import springrest.model.User;
import springrest.model.dao.ProgramDao;
import springrest.model.dao.UserDao;
import springrest.model.service.EventImageService;
import springrest.model.service.ProgramImageService;
import springrest.util.JwtUtils;
import springrest.util.Utils;
import javax.persistence.Entity;

@CrossOrigin
@RestController
public class ProgramController {
	
	@Value("${scicafe.api.url}")
	private String APIURL;
	
	@Autowired
    private ProgramDao programDao;
	
	@Autowired
    private UserDao userDao;
	
	@Autowired
	private ProgramImageService programImageService;
	
	@Autowired
	private JwtUtils jwt;

    // Get an program by id
    @RequestMapping(value = "/program/{id}", method = RequestMethod.GET)
    public ResponseEntity<Program> getProgram( @PathVariable Long id ,HttpServletRequest request)
    {
    	try {
      		System.out.println("Fetching Program with id " + id);
        	Program program = programDao.getProgram(id);
        	if (program == null) {
                System.out.println("Program with id " + id + " not found");
                return new ResponseEntity<Program>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<Program>(program, HttpStatus.OK);
    	} catch (Exception e) {
    		throw new RestException(400, e.getMessage());
    	}
    }

    // Get all programs
    @RequestMapping(value = "/programs", method = RequestMethod.GET)
    public ResponseEntity<List<Program>> getPrograms(HttpServletRequest request)
    {
         try {
      		List<Program> programs = programDao.getPrograms();
      		if(programs.isEmpty()){
                return new ResponseEntity<List<Program>>(HttpStatus.NO_CONTENT);//You many decide to return HttpStatus.NOT_FOUND
            }
            return new ResponseEntity<List<Program>>(programs, HttpStatus.OK);
     	 }  catch (Exception e) {
     		 throw new RestException(400, e.getMessage());
     	 }
    }
    
    // create a new program
    @RequestMapping(value = "/programs", method = RequestMethod.POST)
	public ResponseEntity<Program> createProgram(HttpServletRequest request,@RequestParam(value = "image",required=false) MultipartFile image,@RequestParam("name") String name,@RequestParam("fullName") String fullName,@RequestParam("description") String description) {
    		

        	try {

        	    Program program = new Program();

         		String token = request.getHeader("Authorization");
          		jwt.decode(token).getClaim("userId").asLong();
          		User requestUser = userDao.getUser(jwt.decode(token).getClaim("userId").asLong());
          		if (!Utils.proceedOnlyIfAdmin(requestUser))
          			throw new RestException(400, "Invalid Authorization");
          		program.setName(name);
          		program.setFullName(fullName);
          		program.setDescription(description);
          		if (program.getName() == null || program.getFullName() == null || program.getDescription() == null)
            		throw new RestException( 400, "missing required field(s)." );
          		System.out.println("Creating Program " + program.getName());
            	if (program.getName() == null || program.getFullName() == null || program.getDescription() == null )
            		throw new RestException( 400, "missing required field(s)." );
            	if (image == null||image.isEmpty()) {
            		System.out.println("xxxxxxxxxxxxxxxx");
            		program.setImageUrl("assets/images/program/default.png");
            	} else {
            		program = programDao.saveProgram(program);
                	String fileName = image.getOriginalFilename();
              		String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
              		program.setImageUrl(APIURL+"/program-image/program"+program.getId()+"."+fileType);
                	this.programImageService.store(image, "program"+program.getId()+"."+fileType);
            	}
            	return new ResponseEntity<Program>(programDao.saveProgram(program),HttpStatus.CREATED);
        	} catch (Exception e) {
   	 		 throw new RestException(400, e.getMessage());
   	 		}
    	
	}
    
    // edit a program
   	@RequestMapping(value = "/program/{id}", method = RequestMethod.PUT)
   	public ResponseEntity<Program> updateProgram(@PathVariable Long id, HttpServletRequest request,@RequestParam(value = "image",required=false) MultipartFile image,@RequestParam("name") String name,@RequestParam("fullName") String fullName,@RequestParam("description") String description) {
   		try {
   			String token = request.getHeader("Authorization");
      		jwt.decode(token).getClaim("userId").asLong();
      		User requestUser = userDao.getUser(jwt.decode(token).getClaim("userId").asLong());
      		if (!Utils.proceedOnlyIfAdmin(requestUser))
      			throw new RestException(400, "Invalid Authorization");
      		System.out.println("Updating Program " + id);
       		Program program = programDao.getProgram(id);
    		if (program == null) {
    			System.out.println("xxx");
    			return new ResponseEntity<Program>(HttpStatus.NOT_FOUND);
    		}
    		if (image != null && !image.isEmpty()) {
            	String fileName = image.getOriginalFilename();
          		String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
          		program.setImageUrl(APIURL+"/program-image/program"+program.getId()+"."+fileType);
            	this.programImageService.store(image, "program"+program.getId()+"."+fileType);
        	}
    		program.setName(name);
   			program.setFullName(fullName);
   			program.setDescription(description);
   			return new ResponseEntity<Program>(programDao.saveProgram(program), HttpStatus.OK);
   		} catch (Exception e) {
   			throw new RestException(400, e.getMessage());
   		}
   	}
   	
    // delete a program
 	@RequestMapping(value = "/program/{id}", method = RequestMethod.DELETE)
 	public ResponseEntity<Program> deleteProgram(@PathVariable("id") long id,HttpServletRequest request) {
        try {
        	String token = request.getHeader("Authorization");
      		jwt.decode(token).getClaim("userId").asLong();
      		User requestUser = userDao.getUser(jwt.decode(token).getClaim("userId").asLong());
      		if (!Utils.proceedOnlyIfAdmin(requestUser))
      			throw new RestException(400, "Invalid Authorization");
        	System.out.println("Fetching & Deleting Program with id " + id);
            Program program = programDao.getProgram(id);
            if (program == null) {
                System.out.println("Unable to delete. Program with id " + id + " not found");
                return new ResponseEntity<Program>(HttpStatus.NOT_FOUND);
            }
            this.programImageService.deleteFile(program.getImageUrl().substring(program.getImageUrl().lastIndexOf('/')+1));
        	programDao.deleteProgram(program);
        	return new ResponseEntity<Program>(HttpStatus.OK);
        } catch (Exception e) {
        	throw new RestException(400, e.getMessage());
        }
        
    }
 	
 	@RequestMapping(value = "/program-image/{imageName}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String imageName) {
      Resource file = this.programImageService.loadFile(imageName);
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
          .body(file);
    }
	
}
