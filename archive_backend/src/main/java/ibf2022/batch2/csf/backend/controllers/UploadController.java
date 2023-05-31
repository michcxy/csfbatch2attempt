package ibf2022.batch2.csf.backend.controllers;



import java.io.IOException;
import java.util.List;

import org.apache.catalina.WebResourceRoot.ArchiveIndexStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import ibf2022.batch2.csf.backend.repositories.ArchiveRepository;
import ibf2022.batch2.csf.backend.repositories.ImageRepository;

@Controller
@RequestMapping
@CrossOrigin(origins="*")
public class UploadController {

	@Autowired
	private ImageRepository imgRepo;
	@Autowired
	private ArchiveRepository arcRepo;

	String s3Bucket = "kickthe";


	// TODO: Task 2, Task 3, Task 4
	@PostMapping(path="/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	public ResponseEntity<String> postUpload(@RequestPart String name, @RequestPart String title, @RequestPart String comments, @RequestPart MultipartFile file) throws IOException {


		System.out.printf(">>> title: %s\n", title);
		System.out.printf(">>> filename: %s\n", file.getOriginalFilename());
		List<String> s3Keys = 
		imgRepo.upload(name, title, comments, file);
		arcRepo.recordBundle(name, title, comments, s3Bucket, s3Keys);

		//URL url = imgRepo.upload(name, title, comments, file);
		//System.out.printf(">>>> URL: %s\n", url.toString());

		return ResponseEntity.ok("{}");
	}

	// TODO: Task 5
	

	// TODO: Task 6

}
