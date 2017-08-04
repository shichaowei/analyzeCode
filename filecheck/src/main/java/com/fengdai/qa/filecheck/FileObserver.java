package com.fengdai.qa.filecheck;

import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;

import com.fengdai.qa.util.parseSrcUtil;

/**
 * 文件观察者
 * @author dWX207527
 *
 */
public class FileObserver implements Observer
{

	private String FileDirName;
	public FileObserver(String filedir) {
		FileDirName=filedir;
	}

	@Override
	public void update(Observable o, Object arg)
	{
		if (!(o instanceof FileMoniter) || !(arg instanceof HashSet<?>))
		{
			return ;
		}

		HashSet<String> filesChanged  = (HashSet<String>)arg;
		HashSet<String> fileNameToClass  = new HashSet<String>();

		for(String fileName:filesChanged){

			System.out.println(fileName+"文件有改动");
			// 一旦有改动则通知了观察者 观察者进行重新配置文件
			//	        FileMap.getInstance().fillFileMap(fileName);
			String[] var= fileName.split("com");
			System.out.println(("com"+var[var.length-1]).replace("\\", ".").replace(".java", ""));
			fileNameToClass.add(("com"+var[var.length-1]).replace("\\", ".").replace(".java", ""));
		}

		parseSrcUtil.parseMetriscs(FileDirName,fileNameToClass);

	}

}