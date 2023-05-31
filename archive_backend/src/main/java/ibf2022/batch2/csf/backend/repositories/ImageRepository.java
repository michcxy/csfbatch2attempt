package ibf2022.batch2.csf.backend.repositories;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import org.zeroturnaround.zip.ZipUtil;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Repository
public class ImageRepository {

	@Autowired
	private AmazonS3 s3;

	public List<String> s3Keys = new LinkedList<String>();
	public void FileUploadController(AmazonS3 s3) {
		this.s3 = s3;

	  }

	//TODO: Task 3
	// You are free to change the parameter and the return type
	// Do not change the method's name
	public List<String> upload(String name, String title, String comments, MultipartFile file) throws IOException {
		
		if (!file.getOriginalFilename().endsWith(".zip")) {
			throw new IOException("invalid file format");
		}

		// Add custom metadata
		 Map<String, String> userData = new HashMap<>();
		 userData.put("name", name);
		 userData.put("title", title);
		 userData.put("comments", comments);
		 userData.put("filename", file.getOriginalFilename());
		 userData.put("upload-date", (new Date()).toString());
   
		// //  // Add object's metadata 
		//  ObjectMetadata metadata = new ObjectMetadata();
		//  metadata.setContentType(file.getContentType());
		//  metadata.setContentLength(file.getSize());
		//  metadata.setUserMetadata(userData);
   
		 // Generate a random key name
		 //String key = "images/%s".formatted(UUID.randomUUID().toString().substring(0, 8));
   
		 Path tempDir = Files.createTempDirectory("unzip");
		 // Extract the contents of the zip file to the temporary directory
		 ZipUtil.unpack(file.getInputStream(), tempDir.toFile());
   
		 // Process the extracted files here
		 // Get the list of extracted file paths
		 List<String> extractedFiles = new ArrayList<>();
		 Files.walk(tempDir)
			 .filter(Files::isRegularFile)
			 .forEach((Path path) -> {
			 try {
				 // Upload each extracted file to S3
				 String key = "images/%s".formatted(UUID.randomUUID().toString().substring(0, 8));
				 s3Keys.add(key);
				 //s3.putObject(new PutObjectRequest("kickthe", key, path.toFile()));
				 // Set content type based on the file's suffix
				 PutObjectRequest putObjectRequest = new PutObjectRequest("kickthe", key, path.toFile());
				 putObjectRequest = putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
				 String contentType = getContentTypeFromSuffix(path.toString());
				 // Add object's metadata 
				ObjectMetadata metadata = new ObjectMetadata();
				metadata.setContentType(contentType);
				metadata.setContentLength(file.getSize());
				metadata.setUserMetadata(userData);
				putObjectRequest.setMetadata(metadata);
				 s3.putObject(putObjectRequest);

				 extractedFiles.add(key); // Add the S3 object key to the list
				 
			 } catch (AmazonS3Exception e) {
				 e.printStackTrace();
				 // Handle any upload errors
			 }
			 });

		return s3Keys;
   
	}

	private String getContentTypeFromSuffix(String filePath) {
		Map<String, String> contentTypes = new HashMap<>();
		contentTypes.put(".txt", "text/plain");
		contentTypes.put(".jpg", "image/jpeg");
		contentTypes.put(".png", "image/png");
		contentTypes.put(".gif", "image/gif");
		// Add more mappings as needed
	  
		String suffix = filePath.substring(filePath.lastIndexOf("."));
		return contentTypes.getOrDefault(suffix, "application/octet-stream");
	  }
}
