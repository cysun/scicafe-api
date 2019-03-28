package springrest.model.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import springrest.model.News;
import springrest.model.dao.NewsDao;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;

@Repository
public class NewsDaoImpl implements NewsDao {

	@PersistenceContext
    private EntityManager entityManager;
	
	@Override
	public News getNews(Long id) {

		return entityManager.find( News.class, id );
	}

	@Override
	public List<News> getAllNews() {

		return entityManager.createQuery( "from News order by postedDate", News.class )
	            .getResultList();
	}

	@Override
	@Transactional
	public News saveNews(News news) {
		
		return entityManager.merge( news );
		
	}

	@Override
	@Transactional
	public boolean deleteNews(News news) {
		
		try {
    		entityManager.remove(news);
    		return true;
    	} catch (Exception e) {
    		return false;
    	}
		
	}

	@Override
	public List<News> getTopNews() {
		
		return entityManager.createQuery( "select n from News n where n.isTop='Yes' order by postedDate asc", News.class )
	            .getResultList();
	}

}
