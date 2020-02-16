package com.frauddetection.aws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;


@Configuration
public class AmazonClientConfiguration {
	
	
	

   
//	@Value("${app.awsServices.bucketName}")
//	private  String bucketName;
//	@Value("${loud.aws.credentials.accessKey}")
//	private String AKey;
//	@Value("${loud.aws.credentials.secretKey}")
//	private  String SKey;
    
	private String AccessKey = "";
	private String SecretKey = "";
	private String BucketName = "";
  

	AWSCredentials credentials = new BasicAWSCredentials(
    		  AccessKey, 
    		  SecretKey    		);
    
    @Bean
    public AmazonS3 s3Client() {
    	return AmazonS3ClientBuilder
      		  .standard()
      		  .withCredentials(new AWSStaticCredentialsProvider(credentials))
      		  .withRegion(Regions.US_EAST_2)
      		  .build();
    }
    
   
  

}