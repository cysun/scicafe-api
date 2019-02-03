package springrest.model.dao;

import java.util.List;

import springrest.model.Reward;;

public interface RewardDao {
	
	Reward getReward( Long id );

    List<Reward> getRewards();

    Reward saveReward ( Reward  event);

    public boolean deleteReward(Reward event);
    
    public boolean isRewardExists(Reward event);

}
