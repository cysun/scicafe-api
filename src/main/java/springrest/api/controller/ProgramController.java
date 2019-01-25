package springrest.api.controller;

import java.util.List;

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
import springrest.model.Program;
import springrest.model.dao.ProgramDao;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class ProgramController {
	
	@Autowired
    private ProgramDao programDao;

    // Get an program by id
    @RequestMapping(value = "/program/{id}", method = RequestMethod.GET)
    public ResponseEntity<Program> getProgram( @PathVariable Long id )
    {
    	System.out.println("Fetching Program with id " + id);
    	Program program = programDao.getProgram(id);
    	if (program == null) {
            System.out.println("Program with id " + id + " not found");
            return new ResponseEntity<Program>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Program>(program, HttpStatus.OK);
    }

    // Get all programs
    @RequestMapping(value = "/programs", method = RequestMethod.GET)
    public ResponseEntity<List<Program>> getPrograms()
    {
    	 List<Program> programs = programDao.getPrograms();
    	 if(programs.isEmpty()){
             return new ResponseEntity<List<Program>>(HttpStatus.NO_CONTENT);//You many decide to return HttpStatus.NOT_FOUND
         }
         return new ResponseEntity<List<Program>>(programs, HttpStatus.OK);
    }
    
    // create a new program
    @RequestMapping(value = "/programs", method = RequestMethod.POST)
	public ResponseEntity<Program> createProgram(@RequestBody Program program) {
    	System.out.println("Creating Program " + program.getName());
    	if (program.getName() == null || program.getFullName() == null || program.getDescription() == null )
    		throw new RestException( 400, "missing required field(s)." );
    	if (programDao.isProgramExists(program)) {
            System.out.println("A Program with name " + program.getName() + " already exist");
            return new ResponseEntity<Program>(HttpStatus.CONFLICT);
        }
		try {	
			return new ResponseEntity<Program>(programDao.saveProgram(program),HttpStatus.CREATED);
		} catch (Exception e) {
			throw new RestException(400, e.getMessage());
		}
	}
    
    // edit a program
   	@RequestMapping(value = "/program/{id}", method = RequestMethod.PUT)
   	public ResponseEntity<Program> updateProgram(@PathVariable Long id, @RequestBody Program newProgram) {
   		System.out.println("Updating Program " + id);
   		Program program = programDao.getProgram(id);
		if (program == null)
			return new ResponseEntity<Program>(HttpStatus.NOT_FOUND);
   		try {
   			program.setName(newProgram.getName());
   			program.setFullName(newProgram.getFullName());
   			program.setDescription(newProgram.getDescription());
   			return new ResponseEntity<Program>(programDao.saveProgram(program), HttpStatus.OK);
   		} catch (Exception e) {
   			throw new RestException(400, e.getMessage());
   		}
   	}
   	
    // delete a program
 	@RequestMapping(value = "/program/{id}", method = RequestMethod.DELETE)
 	public ResponseEntity<Program> deleteProgram(@PathVariable("id") long id) {
        System.out.println("Fetching & Deleting Program with id " + id);
 
        Program program = programDao.getProgram(id);
        if (program == null) {
            System.out.println("Unable to delete. Program with id " + id + " not found");
            return new ResponseEntity<Program>(HttpStatus.NOT_FOUND);
        }
        try {
        	programDao.deleteProgram(program);
        	return new ResponseEntity<Program>(HttpStatus.OK);
        } catch (Exception e) {
        	throw new RestException(400, e.getMessage());
        }
        
    }
	
}
