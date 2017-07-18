package com.fengdai.qa.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.tools.ant.DirectoryScanner;
import org.junit.experimental.theories.Theories;
import org.junit.rules.Timeout;

import com.fengdai.qa.link.tree;
import com.fengdai.qa.link.treeNode;

import ch.qos.logback.classic.Logger;
import gr.spinellis.ckjm.ClassVisitor;
import gr.spinellis.ckjm.MetricsFilter;
import gr.spinellis.ckjm.PrintPlainResults;



public class parseSrcUtil {

	final static LogUtil logger=new LogUtil(parseSrcUtil.class);
	
	public static String[] scanClasses(String classesDir){
		String classDir = classesDir;
		File file= new File(classDir);
		
		DirectoryScanner scanner = new DirectoryScanner();
		String[] includes = {"**//*.class"};
//		String[] includes = {"**//*.jar"};
		scanner.setIncludes(includes);
		scanner.setBasedir(file);
		scanner.setCaseSensitive(true);
		scanner.scan();
		String[] filesTemp = scanner.getIncludedFiles();
		ArrayList<String> files =new ArrayList<>();

		logger.logInfo("要测试的类路径为：");
		for(int ioe=0;ioe<filesTemp.length;ioe++){
			//剔除掉测试的class类
			if(!filesTemp[ioe].contains("test-classes")){
				files.add(file.getPath()+file.separatorChar+filesTemp[ioe]);
				logger.logInfo(file.getPath()+file.separatorChar+filesTemp[ioe]);
			}
		}
		return (String[])files.toArray(new String[files.size()]);
		
	}
	
	public static void parseMetriscs(String classesDir,HashSet<String> varchange){
		String outFile="output/a.txt";
		File out= new File(outFile);
		ArrayList<tree<String>> result = new ArrayList<>();
		try {
			FileOutputStream var6 = new FileOutputStream(out);
			PrintPlainResults var7 = new PrintPlainResults(new PrintStream(var6));
			result = new  LinkToTreeUtil().createTree(MetricsFilter.runMetrics(scanClasses(classesDir), var7));
			var6.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


		HashSet<String> changerange=new HashSet<String>();
//		String linktemp=ReadFromFile.readFileByLines("output/调用关系.txt");
//		result = new  LinkToTreeUtil().createTree(linktemp);

		System.out.println("修改的类的方法"+varchange);
		for(Iterator<tree<String>> it = result.iterator(); it.hasNext();){
			tree<String> temp= it.next();
			for(String varunit:varchange){
				treeNode<String> node = temp.search(temp.root, varunit);
				if(!(node == null)){
					changerange.add(temp.root.t.toString());
				}
			}
		}
		System.out.println("此处修改影响范围");
		for(String temp:changerange){
			System.out.println(temp);
		}
	}
	
	public static HashSet<String> calcEnv(HashSet<String> changerange,ArrayList<tree<String>> result){
		boolean Implflag=false;//是否还有实现类
		
		for(String var:changerange){
			if(var.contains("Impl")){
				Implflag=true;
			}else{
				Implflag=false;
			}
		}
		if(Implflag){
			HashSet<String> toRemove= new HashSet<>();
			HashSet<String> toAdd= new HashSet<>();
			for(String var:changerange){
				if(var.contains("Impl")){
//					changerange.remove(var);
					toRemove.add(var);
					String varImplToApi=var.replace(".service.impl.", ".service.").replace("Impl", "");
					toAdd.add(varImplToApi);
//					System.out.println(varImplToApi);
					for(Iterator<tree<String>> it = result.iterator(); it.hasNext();){
						tree<String> temp= it.next();
							treeNode<String> node = temp.search(temp.root, varImplToApi);
							if(!(node == null)){
//								changerange.add(temp.root.t.toString());
								toAdd.add(temp.root.t.toString());
						}
					}
				}
			}
			changerange.addAll(toAdd);
			changerange.removeAll(toRemove);
			return calcEnv(changerange, result);}
		else{
			return changerange;
		}
	}
	
	public static HashSet<String> calcEnvToClass(HashSet<String> changerange,ArrayList<tree<String>> result){
		boolean Implflag=false;//是否还有实现类
		
		for(String var:changerange){
			if(var.contains("Impl")){
				Implflag=true;
			}else{
				Implflag=false;
			}
		}
		if(Implflag){
			HashSet<String> toRemove= new HashSet<>();
			HashSet<String> toAdd= new HashSet<>();
			for(String var:changerange){
				if(var.contains("Impl")){
//					changerange.remove(var);
					toRemove.add(var);
					String varImplToApi=var.replace(".service.impl.", ".service.").replace("Impl", "");
					toAdd.add(varImplToApi);
//					System.out.println(varImplToApi);
					for(Iterator<tree<String>> it = result.iterator(); it.hasNext();){
						tree<String> temp= it.next();
						treeNode<String> node = temp.search(temp.root, varImplToApi);
						if(!(node == null)){
//								changerange.add(temp.root.t.toString());
							toAdd.add(temp.root.t.toString());
						}
					}
				}
			}
			changerange.addAll(toAdd);
			changerange.removeAll(toRemove);
			return calcEnv(changerange, result);}
		else{
			return changerange;
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		parseMetriscs("F:/github/接口测试Demo/apptest/target");
//		parseMetriscs("F:/开发源码/core-base/target");
//		parseMetriscs("F:/开发源码/core-base/target/classes");
		
		HashSet<String> varchange=new HashSet();
		varchange.add("com.fengdai.authority.dao.redis.ApiResourceDaoDecorator的方法delete");
//		parseMetriscs("E:/开发源码class/dubbo-riskcontrol-0.0.1.M1-SNAPSHOT-distribution/dubbo-riskcontrol-0.0.1.M1-SNAPSHOT/lib",varchange);
		parseMetriscs("F:/开发源码",varchange);
//		parseMetriscs("F:/开发源码/fengdai-core-channel-test/workspace/core-channel/target/classes/com/fengdai/channel/service/impl/temp",varchange);
//		parseMetriscs("F:/test/testbcel/target/classes/org/wsc/testbcel/testbcel/test",varchange);
	}

}