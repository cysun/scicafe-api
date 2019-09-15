package springrest.model.dao.jpa;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Repository;

import springrest.model.News;
import springrest.model.dao.NewsDao;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Repository
public class NewsDaoImpl implements NewsDao {
	
	Logger log = LoggerFactory.getLogger(this.getClass().getName());
	  private final Path rootLocation = Paths.get("news-images");
	 
	  public void store(MultipartFile file,String imageName) {
		System.out.println(rootLocation.toString());
	    try {
	      Files.copy(file.getInputStream(), this.rootLocation.resolve(imageName), StandardCopyOption.REPLACE_EXISTING);
	    } catch (Exception e) {
	      throw new RuntimeException("FAIL!");
	    }
	  }
	 
	  public Resource loadFile(String filename) {
	    try {
	      Path file = rootLocation.resolve(filename);
	      Resource resource = new UrlResource(file.toUri());
	      if (resource.exists() || resource.isReadable()) {
	        return resource;
	      } else {
	        throw new RuntimeException("FAIL!");
	      }
	    } catch (MalformedURLException e) {
	      throw new RuntimeException("FAIL!");
	    }
	  }
	 
	  public void init() {
	    try {
	      Files.createDirectory(rootLocation);
	    } catch (IOException e) {
	      throw new RuntimeException("Could not initialize storage!");
	    }
	  }

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
