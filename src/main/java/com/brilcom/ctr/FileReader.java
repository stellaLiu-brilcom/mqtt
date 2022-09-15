package com.brilcom.ctr;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class FileReader {

	public static void main(String[] args) {
		BufferedReader br = null;
		String line;
		String path = "C:\\file\\c.csv";
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
			// br = new BufferedReader(new FileReader(path)); // 이렇게도 가능
			while ((line = br.readLine()) != null) {
				// String[] temp = line.split("\t"); // 탭으로 구분
				String[] temp = line.split(","); // 쉼표로 구분
				for (int i = 0; i < temp.length; i++) {
					System.out.print((i + 1) + "열: " + temp[i]);
					if (i != temp.length - 1)
						System.out.print(", ");
					else
						System.out.println();
				}
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			
		}
		
	}
}
