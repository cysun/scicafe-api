package springrest.model.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import springrest.model.Reward;
import springrest.model.dao.RewardDao;

@Repository
public class RewardDaoImpl implements RewardDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Reward getReward( Long id )
    {
        return entityManager.find( Reward.class, id );
    }

    @Override
    public List<Reward> getRewards()
    {
        return entityManager.createQuery( "from Reward order by id", Reward.class )
            .getResultList();
    }
    
    @Override
    public List<Reward> getApprovedRewards() {
    	return entityManager.createQuery( "select r from Reward r where r.status=1 and r.endDate >= CURDATE() and r.startDate <= CURDATE() order by id ", Reward.class )
                .getResultList();
    }
    
    @Override
    @Transactional
    public Reward saveReward( Reward reward )
    {
        return entityManager.merge( reward );
    }
    
    @Override
   	@Transactional
   	public boolean deleteReward(Reward reward)
   	{
       	try {
       		entityManager.remove(reward);
       		return true;
       	} catch (Exception e) {
       		return false;
       	}
   	}

   	@Override
   	public boolean isRewardExists(Reward reward) {
   		return entityManager.contains(reward);
   	}

   	public Reward getPotentialRewards(Long id) {
   		//return entityManager.createNativeQuery(" ", Reward.class);
   		return null;
   	}

	@Override
	public List<Reward> getOwnRewards(Long id) {
		return entityManager.createQuery( "select r from Reward r where r.submitter.id = " + id + "order by id", Reward.class )
	            .getResultList();
	}
}