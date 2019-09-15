package springrest.model.dao;

import java.util.List;

import springrest.model.Reward;

public interface RewardDao {
	
	public Reward getReward( Long id );
	
	public List<Reward> getOwnRewards(Long id);
	
	public List<Reward> getRewards();
    
    public List<Reward> getApprovedRewards();

    public Reward saveReward ( Reward  reward);

    public boolean deleteReward(Reward reward);
    
    public boolean isRewardExists(Reward reward);

}
