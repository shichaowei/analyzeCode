package com.fengdai.qa.analyzesrc;

import java.io.File;

import org.apache.tools.ant.DirectoryScanner;

import com.fengdai.qa.filecheck.FileMoniter;
import com.fengdai.qa.filecheck.FileObserver;

public class AnalyzeProcess {

	public static String[] scanFiles(String classesDir) {

		String classDir = classesDir;
		File file= new File(classDir);
		DirectoryScanner scanner = new DirectoryScanner();
		String[] includes = {"**//*.java"};
		scanner.setIncludes(includes);
		scanner.setBasedir(file);
		scanner.setCaseSensitive(true);
		scanner.scan();
		String[] files = scanner.getIncludedFiles();

		
		for(int ioe=0;ioe<files.length;ioe++){
			files[ioe]= file.getPath()+file.separatorChar+files[ioe];
		}
		return files;
	}
	
	
	public static void main(String[] args) {
		FileMoniter subject = new FileMoniter();
		FileObserver observer = new FileObserver();
		subject.addObserver(observer);
//		for(String filename:scanFiles("F:/开发源码")){
////			System.out.println(filename)
//			subject.addFile(filename);
//		}
		subject.scanFileDir("F:/test/代码分析/testbcel");
		subject.run();
		
	}

}
