package springrest.model.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import springrest.model.Tag;
import springrest.model.dao.TagDao;
import javax.persistence.Entity;

@Repository
public class TagDaoImpl implements TagDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Tag getTag( Long id )
    {
        return entityManager.find( Tag.class, id );
    }

    @Override
    public List<Tag> getTags()
    {
        return entityManager.createQuery( "from Tag order by id", Tag.class )
            .getResultList();
    }

    @Override
    @Transactional
    public Tag saveTag( Tag tag )
    {
        return entityManager.merge( tag );
    }
    
    @Override
   	@Transactional
   	public boolean deleteTag(Tag tag)
   	{
       	try {
       		entityManager.remove(tag);
       		return true;
       	} catch (Exception e) {
       		return false;
       	}
   	}

   	@Override
   	public boolean isTagExists(Tag tag) {
   		return entityManager.contains(tag);
   	}

}