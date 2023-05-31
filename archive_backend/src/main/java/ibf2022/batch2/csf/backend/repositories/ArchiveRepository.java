package ibf2022.batch2.csf.backend.repositories;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;

@Repository
public class ArchiveRepository {

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	AmazonS3 s3;


	//TODO: Task 4
	// You are free to change the parameter and the return type
	// Do not change the method's name
	// Write the native mongo query that you will be using in this method
	//
	//
	public Object recordBundle(String name, String title, String comments, String s3Bucket, List<String> s3Keys) {
		// Create a list to store the image URLs
        List<String> imageUrls = new ArrayList<>();

        // Iterate over the list of S3 keys
        for (String s3Key : s3Keys) {
            // Get the S3 object
            S3Object s3Object = s3.getObject(s3Bucket, s3Key);

            // Add the image URL to the list
            imageUrls.add(s3Object.getObjectContent().getHttpRequest().getURI().toString());
        }

        // Create a new document to store the image URLs
        Document document = new Document();
		document.append("name", name);
        document.append("title", title);
		document.append("comments", comments);
        document.append("imageUrls", imageUrls);
        // Add more fields as needed

        // Insert the document into the collection
        mongoTemplate.insert(document, "csf2");

		return null;
	}
	

	//TODO: Task 5
	// You are free to change the parameter and the return type
	// Do not change the method's name
	// Write the native mongo query that you will be using in this method
	//
	//
	public Object getBundleByBundleId(/* any number of parameters here */) {
		return null;
	}

	//TODO: Task 6
	// You are free to change the parameter and the return type
	// Do not change the method's name
	// Write the native mongo query that you will be using in this method
	//
	//
	public Object getBundles(/* any number of parameters here */) {
		return null;
	}


}
