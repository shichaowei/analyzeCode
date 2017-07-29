package com.fengdai.qa.analyzesrc;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

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
		String fileDir="F:/test/代码分析/testbcel";
		FileMoniter subject = new FileMoniter();
		FileObserver observer = new FileObserver(fileDir);
		subject.addObserver(observer);
		//		for(String filename:scanFiles("F:/开发源码")){
		////			System.out.println(filename)
		//			subject.addFile(filename);
		//		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
		subject.scanFileDir(fileDir);
		//		subject.scanFileDir("F:/test/代码分析/testbcel");
		//		subject.scanFileDir("/trdata/jobs/蜂贷2.0/jobs");
		subject.run();

	}

}
