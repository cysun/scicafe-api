package springrest.model.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class NewsImageService {
	

	  Logger log = LoggerFactory.getLogger(this.getClass().getName());
	  private final Path rootLocation = Paths.get("news-images");
	 
	  public void store(MultipartFile file,String imageName) {
		 
		if (Files.notExists(this.rootLocation)) {
			init();
		}
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
	  
	  public void deleteFile(String filename) {
		  
		    try {
		      Path path = rootLocation.resolve(filename);
		      Files.deleteIfExists(path);
		      System.out.println("File deleted successfully"); 
		    } catch(NoSuchFileException e) 
	        { 
	            System.out.println("No such file/directory exists"); 
	        } 
	        catch(DirectoryNotEmptyException e) 
	        { 
	            System.out.println("Directory is not empty."); 
	        } 
	        catch(IOException e) 
	        { 
	            System.out.println("Invalid permissions."); 
	        } 
	  }
	 
	  public void deleteAll() {
	    FileSystemUtils.deleteRecursively(rootLocation.toFile());
	  }
	 
	  public void init() {
	    try {
	      Files.createDirectory(rootLocation);
	    } catch (IOException e) {
	      throw new RuntimeException("Could not initialize storage!");
	    }
	  }
	
}
