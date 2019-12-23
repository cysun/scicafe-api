package springrest.api.controller;

import java.util.UUID;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import springrest.model.Event;
import springrest.model.service.ImageService;

@CrossOrigin
@RestController
public class ImageController {
	
	@Value("${scicafe.api.url}")
	private String APIURL;
	
	@Autowired
	private ImageService imageService; 

	@RequestMapping(value = "/images/{imageName}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String imageName) {
      Resource file = this.imageService.loadFile(imageName);
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
          .body(file);
    }
	
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject uploadFile(@RequestParam(value = "image",required=false) MultipartFile image) {
		String fileName = image.getOriginalFilename();
  		String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
  		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
  		String url = APIURL+"/images/image"+uuid+"."+fileType;
    	this.imageService.store(image, "image"+uuid+"."+fileType);
    	JSONObject json = new JSONObject();
    	json.put("url", url);
    	return json;
    }
	
}
