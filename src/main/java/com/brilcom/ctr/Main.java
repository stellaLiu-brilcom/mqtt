package com.brilcom.ctr;

import java.io.File;
import java.io.IOException;

public class Main {
	
	
	public AwsS3 awsS3 = AwsS3.getInstance();

	
	/*
	 * public static void main(String[] args) {
	 * 
	 * Main main = new Main(); 
	 * File file = new File("C:\\img\\log.jfif");
	 * 
	 * String key = "testupload/mainlogo.png"; //String copyKey
	 * ="img/my-img-copy.img";
	 * 
	 * //upload 실행하기. main.upload(file,key);
	 * 
	 * }
	 */
	 
	public static void main(String[] args) throws IOException {
		Main main = new Main();
		String file = "3.csv";
		
	
		
		main.read(file);
		
        //return "csv_read"; // csv_read.html 있어야 에러 발생 안함 
	}
	

	   //업로드
	   public void upload(File file, String key) {
	      awsS3.upload(file, key);
	   }
	 //복사 메서드
	   public void read(String file) throws IOException {
	      awsS3.readObject(file);
	      //readObject(file, copyKey);
	   }

	   //복사 메서드
	   public void copy(String orgkey, String copyKey) {
	      awsS3.copy(orgkey, copyKey);
	   }

	   //삭제 메서드
	   public void delete(String key) {
	      awsS3.delete(key);
	   }

}
