package com.fengdai.qa.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.tools.ant.DirectoryScanner;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.fengdai.ckjm.MetricsFilter;
import com.fengdai.ckjm.PrintPlainResults;
import com.fengdai.qa.model.tree;
import com.fengdai.qa.model.treeNode;
import com.fengdai.qa.service.ParseSrcService;
import com.fengdai.qa.util.LogUtil;
import com.fengdai.qa.util.WriteToFile;


@Service
public class ParseSrcServiceImpl implements ParseSrcService{

	final  LogUtil logger=new LogUtil(ParseSrcServiceImpl.class);
	private ArrayList<tree<String>> linkTrees = new ArrayList<>();
	private HashMap<String, String> annotParis = new HashMap<>();
	
	private  String[] scanClasses(String classesDir){
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
				files.add(file.getPath()+File.separatorChar+filesTemp[ioe]);
				logger.logInfo(file.getPath()+File.separatorChar+filesTemp[ioe]);
			}
		}
		return files.toArray(new String[files.size()]);

	}
	
	/**
	 * 获取所有的根节点
	 * @return
	 */
	public  ArrayList<String> getroots() {
		ArrayList< String > rootsList = new ArrayList<>();
		for(tree<String> item:linkTrees) {
			rootsList.add(item.getrootNode().toString());
		}
		return rootsList;
	}
	
	/**
	 * 
	 * 获取指定的树内容
	 * @param rootNode
	 * @return json字符串 
	 */
	public String getTreeJson(String rootNode) {
		ArrayList<JSONObject> treeDetail = new ArrayList<>();
		for(tree<String> item:linkTrees) {
			if(item.getrootNode().toString().equals(rootNode)) {
				item.getTreeString(item.root, 1, treeDetail);
			}
		}
		return treeDetail.toString();
	}
	
	
	


	public  void parseSources(String classesDir,HashSet<String> varchange){
		buildToLinkTrees(classesDir);
		final StringBuffer resultToFile= new StringBuffer();
		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		resultToFile.append(df.format(new Date())+"/n");
		final HashSet<String> changerange=new HashSet<String>();
		//		String linktemp=ReadFromFile.readFileByLines("output/调用关系.txt");
		//		result = new  LinkToTreeUtil().createTree(linktemp);

		System.out.println("修改的类的方法"+varchange);
		resultToFile.append("修改的类的方法"+varchange+"/n");
		
			for(final String varunit:varchange){
				boolean changeflag=false;
				for (final tree<String> temp : linkTrees) {
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
	
	
	
	
	public ArrayList<tree<String>> getLinkTrees() {
		return linkTrees;
	}

	public void setLinkTrees(ArrayList<tree<String>> linkTrees) {
		this.linkTrees = linkTrees;
	}



	public  void buildToLinkTrees(String classesDir){
		System.out.println(classesDir);
		final String outFile=System.getProperty("parse.webapp")+"output/a.txt";
		System.out.println(outFile);
		final File out= new File(outFile);
		
		
		try {
			final FileOutputStream var6 = new FileOutputStream(out);
			final PrintPlainResults var7 = new PrintPlainResults(new PrintStream(var6));
			HashMap<String, String> parismap=new MetricsFilter().runMetrics(scanClasses(classesDir), var7);
			linkTrees = new  LinkToTreeServiceImpl().createTree(parismap.get("callParis"));
			var6.close();
			WriteToFile.clearWriteFile(parismap.get("annotParis").toString(), System.getProperty("parse.webapp")+"output/注解对应关系.txt");
			for (String var8:parismap.get("annotParis").split("@@@")) {
				logger.logInfo(var8);
//				System.out.println(var8);
				annotParis.put(var8.split("的注解")[0],var8.split("的注解")[1]);
			}
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}

	}
	
	
	





}
