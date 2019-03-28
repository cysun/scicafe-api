package springrest.model.dao;

import java.util.List;

import springrest.model.Program;
import javax.persistence.Entity;;

public interface ProgramDao {

	public Program getProgram( Long id );

    public List<Program> getPrograms();

    public Program saveProgram( Program program );
    
    public boolean deleteProgram(Program program);
    
    public boolean isProgramExists(Program program);
	
}
