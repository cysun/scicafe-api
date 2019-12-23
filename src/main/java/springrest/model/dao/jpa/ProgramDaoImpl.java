package springrest.model.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import springrest.model.Program;
import springrest.model.dao.ProgramDao;

@Repository
public class ProgramDaoImpl implements ProgramDao {
	
	@PersistenceContext
    private EntityManager entityManager;

    @Override
    public Program getProgram( Long id )
    {
        return entityManager.find( Program.class, id );
    }

    @Override
    public List<Program> getPrograms()
    {
        return entityManager.createQuery( "from Program order by id", Program.class )
            .getResultList();
    }

    @Override
    @Transactional
    public Program saveProgram( Program program )
    {
        return entityManager.merge( program );
    }
    
    @Override
	@Transactional
	public boolean deleteProgram(Program program)
	{
    	try {
    		entityManager.createNativeQuery("delete from users_programs where program_id = " + program.getId()).executeUpdate();
    		entityManager.remove(program);
    		return true;
    	} catch (Exception e) {
    		return false;
    	}
	}

	@Override
	public boolean isProgramExists(Program program) {
		return entityManager.contains(program);
	}

}
