package com.fengdai.qa.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import org.apache.tools.ant.DirectoryScanner;

import com.fengdai.qa.link.tree;
import com.fengdai.qa.link.treeNode;

import gr.spinellis.ckjm.MetricsFilter;
import gr.spinellis.ckjm.PrintPlainResults;



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
		try {
			final FileOutputStream var6 = new FileOutputStream(out);
			final PrintPlainResults var7 = new PrintPlainResults(new PrintStream(var6));
			result = new  LinkToTreeUtil().createTree(MetricsFilter.runMetrics(scanClasses(classesDir), var7));
			var6.close();

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
		for (final tree<String> temp : result) {
			for(final String varunit:varchange){
				final treeNode<String> node = temp.search(temp.root, varunit);
				if(!(node == null)){
					changerange.add(temp.root.t.toString());
				}
			}
		}
		System.out.println("此处修改影响范围");
		resultToFile.append("此处修改影响范围/n");
		for(final String temp:changerange){
			System.out.println(temp);
			resultToFile.append(temp+"/n");
		}
		WriteToFile.appendFile(resultToFile.toString(),"output/影响范围.txt");
	}

	public static HashSet<String> calcEnv(HashSet<String> changerange,ArrayList<tree<String>> result){
		boolean Implflag=false;//是否还有实现类

		for(final String var:changerange){
			if(var.contains("Impl")){
				Implflag=true;
			}else{
				Implflag=false;
			}
		}
		if(Implflag){
			final HashSet<String> toRemove= new HashSet<>();
			final HashSet<String> toAdd= new HashSet<>();
			for(final String var:changerange){
				if(var.contains("Impl")){
					//					changerange.remove(var);
					toRemove.add(var);
					final String varImplToApi=var.replace(".service.impl.", ".service.").replace("Impl", "");
					toAdd.add(varImplToApi);
					//					System.out.println(varImplToApi);
					for (final tree<String> temp : result) {
						final treeNode<String> node = temp.search(temp.root, varImplToApi);
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

		for(final String var:changerange){
			if(var.contains("Impl")){
				Implflag=true;
			}else{
				Implflag=false;
			}
		}
		if(Implflag){
			final HashSet<String> toRemove= new HashSet<>();
			final HashSet<String> toAdd= new HashSet<>();
			for(final String var:changerange){
				if(var.contains("Impl")){
					//					changerange.remove(var);
					toRemove.add(var);
					final String varImplToApi=var.replace(".service.impl.", ".service.").replace("Impl", "");
					toAdd.add(varImplToApi);
					//					System.out.println(varImplToApi);
					for (final tree<String> temp : result) {
						final treeNode<String> node = temp.search(temp.root, varImplToApi);
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
		final String filedir="E:\\测试代码\\analyzeCode\\testbcel\\target";
		// TODO Auto-generated method stub
		//		parseMetriscs("F:/github/接口测试Demo/apptest/target");
		//		parseMetriscs("F:/开发源码/core-base/target");
		//		parseMetriscs("F:/开发源码/core-base/target/classes");

		final HashSet<String> varchange=new HashSet();
		varchange.add("com.wsc.testbcel.testbcel.BaseProgrammer的方法docoding");
		//		varchange.add("com.wsc.testbcel.testbcel.Programmer");
		//		parseMetriscs("E:/开发源码class/dubbo-riskcontrol-0.0.1.M1-SNAPSHOT-distribution/dubbo-riskcontrol-0.0.1.M1-SNAPSHOT/lib",varchange);
		parseMetriscs(filedir,varchange);
		//		parseMetriscs("F:/开发源码/fengdai-core-channel-test/workspace/core-channel/target/classes/com/fengdai/channel/service/impl/temp",varchange);
		//		parseMetriscs("F:/test/testbcel/target/classes/org/wsc/testbcel/testbcel/test",varchange);
	}

}
