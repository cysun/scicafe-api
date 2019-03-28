package springrest.model.dao;

import java.util.List;

import springrest.model.News;

import javax.persistence.Entity;


public interface NewsDao {

	public News getNews( Long id );

    public List<News> getAllNews();

    public News saveNews( News news );
    
    public boolean deleteNews(News news);
    
    public List<News> getTopNews();
	
}

