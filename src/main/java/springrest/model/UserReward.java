package springrest.model;

public class UserReward {
	
	private Long uid;
	
	private Long rid;
	
	private String name;
	
	private Integer criteria;
	
	private Integer progress;
	
	public UserReward(Long uid,Long rid,String name,Integer criteria,Integer progress) {
		this.uid = uid;
		this.rid = rid;
		this.name = name;
		this.criteria = criteria;
		this.progress = progress < criteria?progress:criteria;
	}
	
	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCriteria() {
		return criteria;
	}

	public void setCriteria(Integer criteria) {
		this.criteria = criteria;
	}

	public Integer getProgress() {
		return progress;
	}

	public void setProgress(Integer progress) {
		this.progress = progress;
	}

}
