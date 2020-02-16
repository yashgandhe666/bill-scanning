package com.frauddetection.aws;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.ObjectListing;

@Service
public class FileStorage {

	@Autowired
	AmazonClientConfiguration amazonClient;

//	@Value("${app.awsServices.bucketName}")
//    private String bucketName;
	
	private String BucketName = "expense-faithplusone";

	private File convertMultiPartToFile(MultipartFile file) throws IOException {
		File convFile = new File(file.getOriginalFilename());
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();

		return convFile;
	}

	public List<String> uploadFile(MultipartFile multiPartfile)
			throws AmazonServiceException, SdkClientException, IOException {
		File file = convertMultiPartToFile(multiPartfile);
		this.amazonClient.s3Client().putObject(BucketName, file.getPath(), file);
		// ObjectListing
		ObjectListing objectListing = amazonClient.s3Client().listObjects(BucketName);
		return objectListing.getObjectSummaries().stream().map(object -> object.getKey()).collect(Collectors.toList());

	}

	public List<String> deleteFile(String filePath) {
		this.amazonClient.s3Client().deleteObject(BucketName, filePath);
		// ObjectListing
		ObjectListing objectListing = amazonClient.s3Client().listObjects(BucketName);
		return objectListing.getObjectSummaries().stream().map(object -> object.getKey()).collect(Collectors.toList());

	}


}
