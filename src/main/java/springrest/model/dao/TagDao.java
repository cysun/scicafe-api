package springrest.model.dao;

import java.util.List;

import springrest.model.Tag;

public interface TagDao {

	public Tag getTag( Long id );

    public List<Tag> getTags();

    public Tag saveTag( Tag tag );
    
    public boolean deleteTag(Tag tag);
    
    public boolean isTagExists(Tag tag);
	
}
