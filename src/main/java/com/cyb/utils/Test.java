package com.cyb.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Test {

	public static void main(String[] args) {
		FileOutputStream fop = null;
		File file;
		StringBuffer content = new StringBuffer("This is the text content 中文"+"\n");
		content.append("This is the text content 中文是");
		try {
			file = new File("D:/newfile.txt");
			fop = new FileOutputStream(file);
			if (!file.exists()) {
				file.createNewFile();
			}
			byte[] contentInBytes = content.toString().getBytes();
			fop.write(contentInBytes);
			fop.flush();
			fop.close();
			System.out.println("Done");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}