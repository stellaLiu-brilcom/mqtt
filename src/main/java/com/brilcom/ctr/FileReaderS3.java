package com.brilcom.ctr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;


import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import org.apache.http.HttpResponse;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;

public class FileReaderS3 {
	
	public static void main(String[] args) throws AmazonS3Exception {
		
		String AWS_BUCKETNAME = "fileupload4mqtt";
		String AWS_ACCESS_KEY = "AKIATMVMLKSHXU64IFZS";
		String AWS_SECRET_KEY = "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";
		//String file_path = "";
		String file_name = "3.csv";
		
		AWSCredentials credentials = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);
		//AmazonS3 s3 = new AmazonS3Client(credentials);
		//AmazonS3 s3 = new AmazonS3Client(new ProfileCredentialsProvider());
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion("ap-northease-2")
                .build(); 
		
		try {
			//파일 업로드 부분 파일 이름과 경로를 동시에 넣어줌.
			PutObjectRequest putObjectRequest = new PutObjectRequest(AWS_BUCKETNAME, AWS_ACCESS_KEY, AWS_SECRET_KEY);
			putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead); // URL 접근시 권한 읽을수 있도록 설정.
			
			//파일 다운로드 경로와 파일이름 동시 필요.
			System.out.println("Downloading an Object");
			S3Object object = s3Client.getObject(new GetObjectRequest(AWS_BUCKETNAME, file_name));
			System.out.println("Content-Type: "+ object.getObjectMetadata().getContentType());
			displayTextInputStream(object.getObjectContent());
			
		}catch(AmazonServiceException ase){
			System.out.println("Caught an AmazonServiceException, which means your request made it "+
		"to Amazon S3, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    "+ase.getMessage());
			System.out.println("HTTP Status Code:    "+ase.getStatusCode());
			System.out.println("AWS Error Code:    "+ase.getErrorCode());
			System.out.println("Error Type:    "+ase.getErrorType());
			
		}catch(AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means the client encountered "
					+"a serious internal problem while trying to communicate with S3, "
					+"such as not being able to access the network.");
			System.out.println("Error Message: " +ace.getMessage());

			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	@SuppressWarnings("unused")
	private static void displayTextInputStream(InputStream input) throws IOException{
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		while(true) {
			String line = reader.readLine();
			if (line == null)
			break;
			
			System.out.println(" "+ line);
			
		}
		System.out.println();
	}
	
	
	


	

}
