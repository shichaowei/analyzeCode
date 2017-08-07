package com.fengdai.qa.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.PathConvert.MapEntry;

import com.fengdai.ckjm.MetricsFilter;
import com.fengdai.ckjm.PrintPlainResults;
import com.fengdai.qa.link.tree;
import com.fengdai.qa.link.treeNode;



public class parseSrcUtil {

	final static LogUtil logger=new LogUtil(parseSrcUtil.class);

	public static String[] scanClasses(String classesDir){
		final String classDir = classesDir;
		final File file= new File(classDir);

		final DirectoryScanner scanner = new DirectoryScanner();
		final String[] includes = {"**//*.class"};
		//		String[] includes = {"**//*.jar"};
		scanner.setIncludes(includes);
		scanner.setBasedir(file);
		scanner.setCaseSensitive(true);
		scanner.scan();
		final String[] filesTemp = scanner.getIncludedFiles();
		final ArrayList<String> files =new ArrayList<>();

		logger.logInfo("要测试的类路径为：");
		for(int ioe=0;ioe<filesTemp.length;ioe++){
			//剔除掉测试的class类
			if(!filesTemp[ioe].contains("test-classes")){
				files.add(file.getPath()+file.separatorChar+filesTemp[ioe]);
				logger.logInfo(file.getPath()+file.separatorChar+filesTemp[ioe]);
			}
		}
		return files.toArray(new String[files.size()]);

	}

	public static void parseMetriscs(String classesDir,HashSet<String> varchange){
		final String outFile="output/a.txt";
		final StringBuffer resultToFile= new StringBuffer();
		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		resultToFile.append(df.format(new Date())+"/n");
		final File out= new File(outFile);
		ArrayList<tree<String>> result = new ArrayList<>();
		HashMap<String, String> annotParis = new HashMap<>();
		try {
			final FileOutputStream var6 = new FileOutputStream(out);
			final PrintPlainResults var7 = new PrintPlainResults(new PrintStream(var6));
			HashMap<String, String> parismap=MetricsFilter.runMetrics(scanClasses(classesDir), var7);
			result = new  LinkToTreeUtil().createTree(parismap.get("callParis"));
			var6.close();
			for (String var8:parismap.get("annotParis").split(",")) {
				annotParis.put(var8.split("的注解")[0],var8.split("的注解")[1]);
			}
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}


		final HashSet<String> changerange=new HashSet<String>();
		//		String linktemp=ReadFromFile.readFileByLines("output/调用关系.txt");
		//		result = new  LinkToTreeUtil().createTree(linktemp);

		System.out.println("修改的类的方法"+varchange);
		resultToFile.append("修改的类的方法"+varchange+"/n");
		
			for(final String varunit:varchange){
				boolean changeflag=false;
				for (final tree<String> temp : result) {
					final treeNode<String> node = temp.search(temp.root, varunit);
					if(!(node == null)){
						changerange.add(temp.root.t.toString());
						changeflag=true;
					}
				}
				if(!changeflag) {
					changerange.add(varunit);
				}
			}
			
		System.out.println("此处修改影响范围");
		resultToFile.append("此处修改影响范围\n");
		//如果没有找到即把修改的类打印出来
		if(changerange.isEmpty()) {
			for(final String temp:varchange){
				System.out.println(temp);
				resultToFile.append(temp+"\n");
			}
		}else {
			for(String temp:changerange){
				
				resultToFile.append(temp+"\n");
				for(Map.Entry<String, String> entry:annotParis.entrySet()) {
					if(temp.contains(entry.getKey())) {
						temp=temp+":的注解为"+entry.getValue();
					}
				}
				System.out.println(temp);
			}
			
		}
		
		WriteToFile.appendFile(resultToFile.toString(),"output/影响范围.txt");
	}
	
	
	



	public static void main(String[] args) {
		final String filedir="D:\\jenkins\\workspace\\fengdai";
//		final String filedir="D:\\jenkins\\workspace\\test";
//		final String filedir="D:\\jenkins\\workspace\\fengdai\\fengdai-core-shop-test";
//		final String filedir="F:\\test\\代码分析\\analyzeCode\\testbcel\\target";
//		final String filedir="D:\\jenkins\\workspace\\fengdai\\fengdai-core-activity-test";
		// TODO Auto-generated method stub
		//		parseMetriscs("F:/github/接口测试Demo/apptest/target");
		//		parseMetriscs("F:/开发源码/core-base/target");
		//		parseMetriscs("F:/开发源码/core-base/target/classes");

		final HashSet<String> varchange=new HashSet();
		varchange.add("com.fengdai.shop.service.impl.ShopOrderHandleServiceImpl");
//		varchange.add("com.fengdai.activity.dao.ActivityBannerDao的方法deleteByPrimaryKey:java.lang.String");
		//		varchange.add("com.wsc.testbcel.testbcel.Programmer");
		//		parseMetriscs("E:/开发源码class/dubbo-riskcontrol-0.0.1.M1-SNAPSHOT-distribution/dubbo-riskcontrol-0.0.1.M1-SNAPSHOT/lib",varchange);
		parseMetriscs(filedir,varchange);
		//		parseMetriscs("F:/开发源码/fengdai-core-channel-test/workspace/core-channel/target/classes/com/fengdai/channel/service/impl/temp",varchange);
		//		parseMetriscs("F:/test/testbcel/target/classes/org/wsc/testbcel/testbcel/test",varchange);
	}

}
