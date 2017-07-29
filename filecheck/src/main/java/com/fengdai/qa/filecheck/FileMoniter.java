package com.fengdai.qa.filecheck;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tools.ant.DirectoryScanner;

import com.fengdai.qa.util.WriteToFile;


/**
 *文件监听器 观察者模式，该监听器是一个被观察者
 *即主题
 * @author dWX207527
 *
 */
public class FileMoniter  extends Observable implements Runnable
{
	/**
	 * 是否启动监听
	 */
	private boolean monitorFlag = true;

	/**
	 * 30*60秒监听一次
	 */
	private int interval = 60000;

	/**
	 * 文件容器 key放文件的名称，value放文件的最后修改时间
	 */
	private Map<String,Long> fileMap = new ConcurrentHashMap<String, Long>();
	private Map<String,Long> xmlfileMap = new ConcurrentHashMap<String, Long>();
	private Map<String,String> classfileMd5 = new ConcurrentHashMap<String, String>();
	private String FileDirName;

	/**
	 * 线程方法
	 */
	@Override
	public void run()
	{
		while(monitorFlag)
		{
			try
			{
				Thread.sleep(interval);
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
				System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
				scanFileDir(FileDirName);
				System.out.println("process over");
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	//    /**
	//     * 检查java文件是否更新(包括修改 增加 删除)
	//     */
	//    private void checkFile()
	//    {
	//
	//
	//        Set<String> fileNames = fileMap.keySet();
	//        HashSet<String> filesChanged = new HashSet<String>();
	//        for (String fileName : fileNames)
	//        {
	//            // 如果被修改过则通知被观察者
	//            if (isModifide(fileName,fileMap.get(fileName)))
	//            {
	//                setChanged();
	//                filesChanged.add(fileName);
	//            }
	//
	//        }
	//        notifyObservers(filesChanged);
	//    }


	//    /**
	//     * 判断是被修改过
	//     * @param fileName
	//     * @param lastModifyTime
	//     * @flag modify delete add
	//     * @return
	//     */
	//    private boolean isModifide(String fileName,Long lastModifyTime,String flag)
	//    {
	//    	boolean result = false;
	//        File file = new File(fileName);
	//        if (file ==null)
	//        {
	//            return false;
	//        }
	//        long time = lastModifyTime;
	//        long lastTime = file.lastModified();
	//        switch (flag) {
	//		case "modify":
	//			 if (lastTime != time)
	//		        {
	//		            // 然后将文件map放置最新的时间
	//		            fileMap.put(fileName, lastTime);
	//		             result= true;
	//		        }
	//			break;
	//		case "delete":
	//
	//			break;
	//		case "add":
	//
	//			break;
	//
	//		default:
	//			break;
	//		}
	//
	//        return result;
	//    }
	//

	private String[] scanFiles(String classesDir,String type) {

		String classDir = classesDir;
		File file= new File(classDir);
		DirectoryScanner scanner = new DirectoryScanner();
		ArrayList<String> includeType = new ArrayList<>();
		switch (type) {
		case "java":
			includeType.add("**//*.java");
			break;
		case "class":
			includeType.add("**//*.class");
			break;
		case "xml":
			includeType.add("**//*.xml");
			break;
		default:
			throw new RuntimeException("scan files type is wrong");
		}


		scanner.setIncludes(includeType.toArray(new String[includeType.size()]));
		scanner.setBasedir(file);
		scanner.setCaseSensitive(true);
		scanner.scan();

		String[] files = scanner.getIncludedFiles();


		for(int ioe=0;ioe<files.length;ioe++){
			files[ioe]= file.getPath()+file.separatorChar+files[ioe];
		}
		return files;
	}




	//    /**
	//     * 新增文件
	//     * 如果文件名不对返回null
	//     * 返回的是文件最后一次的标签
	//     * @param fileName
	//     */
	//    public Long addJavaFile(String fileName)
	//    {
	//        // 如果文件为空则直接返回
	//        if (StringUtils.isEmpty(fileName))
	//        {
	//            return null;
	//        }
	//
	//        Long lastModifyTime = getLastModifyTime(fileName);
	//        return lastModifyTime;
	//    }

	//    /**
	//     * 新增文件
	//     * @param fileName
	//     */
	//    public void addclassFile(String fileName)
	//    {
	//    	// 如果文件为空则直接返回
	//    	if (StringUtils.isEmpty(fileName))
	//    	{
	//    		return;
	//    	}
	//
	//    	String fileMd5 = getFilemd5(fileName);
	//    	classfileMd5.put(fileName, fileMd5);
	//    }

	private void xmlcheckFiles(Map<String, Long> xmlfileMapTemp){
		HashSet<String> filesChangedTemp = new HashSet<String>();
		HashSet<String> filesAddTemp = new HashSet<String>();
		HashSet<String> filesDeleteTemp = new HashSet<String>();
		StringBuffer resultToFile= new StringBuffer();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		resultToFile.append(df.format(new Date())+"\n");
		if(xmlfileMap.equals(xmlfileMapTemp))
			;
		else{
			Set<String> origenKeySet = xmlfileMap.keySet();
			Set<String> nowKeySet =  xmlfileMapTemp.keySet();
			HashSet<String> origenSet = new HashSet<>();
			HashSet<String> nowSet = new HashSet<>();
			for(String var:origenKeySet){
				origenSet.add(var);
			}
			for(String var:nowKeySet){
				nowSet.add(var);
			}

			if(!origenSet.equals(nowSet)){
				HashSet<String> origenSetTemp = (HashSet<String>) origenSet.clone();
				HashSet<String> nowSetTemp = (HashSet<String>) nowSet.clone();
				//判断增加的文件
				for(String temp : origenSetTemp){
					nowSetTemp.remove(temp);
				}
				for(String temp : nowSetTemp){
					filesAddTemp.add(temp);
				}
				origenSetTemp = (HashSet<String>) origenSet.clone();
				nowSetTemp = (HashSet<String>) nowSet.clone();
				//判断删除的文件
				for(String temp : nowSetTemp){
					origenSetTemp.remove(temp);
				}
				for(String temp : origenSetTemp){
					filesDeleteTemp.add(temp);
				}
			}else{
				//是否文件有修改 扫描所有的文件，时间戳不一样的话说明文件有修改
				//key为文件名 value为时间戳
				for(Map.Entry<String, Long> entry:fileMap.entrySet()){
					if(xmlfileMapTemp.get(entry.getKey()) != null && !xmlfileMapTemp.get(entry.getKey()).equals(entry.getValue())){
						filesChangedTemp.add(entry.getKey());
					}
				}
			}
			resultToFile.append("增加的xml文件"+filesAddTemp.toString()+"\n");
			resultToFile.append("减少的xml文件"+filesDeleteTemp.toString()+"\n");
			resultToFile.append("修改的xml文件"+filesChangedTemp.toString()+"\n");
		}
		WriteToFile.appendFile(resultToFile.toString(),"output/xml相关变动文件.txt");

	}

	private void checkFiles(Map<String, Long> fileMapTemp,Map<String, String> classfileMd5Temp) {
		HashSet<String> filesChangedTemp = new HashSet<String>();
		HashSet<String> filesAddTemp = new HashSet<String>();
		HashSet<String> filesDeleteTemp = new HashSet<String>();
		HashSet<String> result = new HashSet<String>();
		if(fileMap.equals(fileMapTemp))
			;
		else{
			Set<String> origenKeySet = fileMap.keySet();
			Set<String> nowKeySet =  fileMapTemp.keySet();
			HashSet<String> origenSet = new HashSet<>();
			HashSet<String> nowSet = new HashSet<>();
			for(String var:origenKeySet){
				origenSet.add(var);
			}
			for(String var:nowKeySet){
				nowSet.add(var);
			}

			if(!origenSet.equals(nowSet)){
				HashSet<String> origenSetTemp = (HashSet<String>) origenSet.clone();
				HashSet<String> nowSetTemp = (HashSet<String>) nowSet.clone();
				//判断增加的文件
				for(String temp : origenSetTemp){
					nowSetTemp.remove(temp);
				}
				for(String temp : nowSetTemp){
					filesAddTemp.add(temp);
					result.add(temp);
				}
				origenSetTemp = (HashSet<String>) origenSet.clone();
				nowSetTemp = (HashSet<String>) nowSet.clone();
				//判断删除的文件
				for(String temp : nowSetTemp){
					origenSetTemp.remove(temp);
				}
				for(String temp : origenSetTemp){
					filesDeleteTemp.add(temp);
					result.add(temp);
				}
			}else{
				//是否文件有修改 扫描所有的文件，时间戳不一样的话说明文件有修改 再校验class的md5值，md5有变动 说明修改的是代码非注释等于代码无关东西
				//key为文件名 value为时间戳
				for(Map.Entry<String, Long> entry:fileMap.entrySet()){
					if(fileMapTemp.get(entry.getKey()) != null && !fileMapTemp.get(entry.getKey()).equals(entry.getValue())){
						filesChangedTemp.add(entry.getKey());
					}
				}
			}
		}

		//把相关的java文件变动情况写入文件
		StringBuffer resultJavaToFile= new StringBuffer();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		resultJavaToFile.append(df.format(new Date())+"\n");
		resultJavaToFile.append("增加的java文件"+filesAddTemp.toString()+"\n");
		resultJavaToFile.append("减少的java文件"+filesDeleteTemp.toString()+"\n");
		resultJavaToFile.append("修改的java文件"+filesChangedTemp.toString()+"\n");
		WriteToFile.appendFile(resultJavaToFile.toString(),"output/java相关变动文件.txt");



		//K:V 类名 类路径
		HashMap<String, String> classToClassPath = new HashMap<>();
		for(Map.Entry<String, String> entry:classfileMd5Temp.entrySet()){
			String[] var= entry.getKey().split("com");
			//			System.out.println(("com"+var[var.length-1]).replace("\\", ".").replace(".class", ""));
			//			System.out.println("test");
			classToClassPath.put(("com"+var[var.length-1]).replace("\\", ".").replace(".class", ""),entry.getKey());
		}

		//K:V 类名 类路径
		HashMap<String, String> classToJavaPath = new HashMap<>();
		//校验对应的md5有无变动
		for(String filetemp:filesChangedTemp){
			String[] var= filetemp.split("com");
			classToJavaPath.put(("com"+var[var.length-1]).replace("\\", ".").replace(".java", ""),filetemp);
		}

		for(Map.Entry<String, String> classEntry:classToClassPath.entrySet()){
			for(Map.Entry<String, String> javaEntry:classToJavaPath.entrySet()){
				if(classEntry.getKey().contains(javaEntry.getKey())){
					//					System.out.println(classEntry.getKey());
					//					System.out.println(classfileMd5.get(classEntry.getValue()));
					//					System.out.println(classfileMd5Temp.get(classEntry.getValue()));
					if(!classfileMd5.get(classEntry.getValue()).equals(classfileMd5Temp.get(classEntry.getValue()))){
						result.add(javaEntry.getKey());
					}
				}
			}
		}

		fileMap=fileMapTemp;
		classfileMd5=classfileMd5Temp;

		if(!result.isEmpty()){
			setChanged();
			notifyObservers(result);

		}


	}



	public void scanFileDir(String filedir){

		FileDirName=filedir;
		Map<String,Long> fileMapTemp = new ConcurrentHashMap<String, Long>();
		Map<String,Long> xmlfileMapTemp = new ConcurrentHashMap<String, Long>();
		Map<String,String> classfileMd5Temp = new ConcurrentHashMap<String, String>();
		for(String javafile:scanFiles(filedir, "java")){
			fileMapTemp.put(javafile, getLastModifyTime(javafile));
		}
		for(String classfile:scanFiles(filedir, "class")){
			classfileMd5Temp.put(classfile, getFilemd5(classfile));
		}
		for(String xmlfile:scanFiles(filedir, "xml")){
			xmlfileMapTemp.put(xmlfile, getLastModifyTime(xmlfile));
		}
		if(fileMap.isEmpty())
			fileMap=fileMapTemp;
		else{
			checkFiles(fileMapTemp,classfileMd5Temp);
		}
		if(classfileMd5.isEmpty()){
			classfileMd5=classfileMd5Temp;
		}
		if(xmlfileMap.isEmpty()){
			xmlfileMap=xmlfileMapTemp;
		}
	}

	/**
	 * 得到文件的最后修改时间
	 * @param fileName
	 * @return
	 */
	private Long getLastModifyTime(String fileName)
	{
		File file = new File(fileName);
		if (null == file)
		{
			return 0L;
		}
		return file.lastModified();
	}

	private String getFilemd5(String fileName) {
		String result="";
		File file = new File(fileName);
		try {
			FileInputStream fis = new FileInputStream(file);
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[1024];
			int length = -1;
			while ((length = fis.read(buffer, 0, 1024)) != -1) {
				md.update(buffer, 0, length);
			}
			BigInteger bigInt = new BigInteger(1, md.digest());
			//			System.out.println(fileName+":文件md5值：" + bigInt.toString(16));
			result=bigInt.toString(16);
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}


}