import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.tools.ant.DirectoryScanner;
import org.junit.rules.Timeout;

import com.app.HandlerTree;
import com.app.tree;
import com.app.treeNode;

import gr.spinellis.ckjm.ClassVisitor;
import gr.spinellis.ckjm.MetricsFilter;
import gr.spinellis.ckjm.PrintPlainResults;



public class TestOne {

	public TestOne() {
		// TODO Auto-generated constructor stub
	}
	
	
	public static void parseMetriscs(String classesDir){
		String classDir = classesDir;
		File file= new File(classDir);
		String outFile="output/a.txt";
		File out= new File(outFile);
		DirectoryScanner scanner = new DirectoryScanner();
		String[] includes = {"**//*.class"};
//		String[] includes = {"**//*.jar"};
		scanner.setIncludes(includes);
		scanner.setBasedir(file);
		scanner.setCaseSensitive(true);
		scanner.scan();
		String[] files = scanner.getIncludedFiles();

		
		for(int ioe=0;ioe<files.length;ioe++){
			files[ioe]= file.getPath()+file.separatorChar+files[ioe];
		}
		
		ArrayList<tree<String>> result = new ArrayList<>();
		try {
			FileOutputStream var6 = new FileOutputStream(out);
			PrintPlainResults var7 = new PrintPlainResults(new PrintStream(var6));
			MetricsFilter.runMetrics(files, var7);
			result = new  HandlerTree().process(ClassVisitor.getresult().toString());
			var6.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(result);
		//查找修改的class影响范围
//		String varchange="com.fengdai.base.constants.CommonConstants$ErrorCode";
		ArrayList<String> varchange=new ArrayList<>();
		HashSet<String> changerange=new HashSet();
		varchange.add("com.fengdai.base.constants.CommonConstants$ErrorCode");
//		varchange.add("org.testng.Assert");
		
		for(Iterator<tree<String>> it = result.iterator(); it.hasNext();){
			tree<String> temp= it.next();
			temp.showNode(temp.root);
			for(String varunit:varchange){
				System.out.println("修改的类"+varunit);
				treeNode node = temp.search(temp.root, varunit);
//				System.out.println(node.t.toString());
				if(!(node == null)){
					changerange.add(temp.root.t.toString());
				}
			}
		}
		
		for(String var:changerange){
			System.out.println("此处修改影响范围");
			System.out.println(var);
		}
		
		
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		parseMetriscs("F:/github/接口测试Demo/apptest/target");
//		parseMetriscs("F:/开发源码/core-base/target");
		parseMetriscs("E:/开发源码class/dubbo-riskcontrol-0.0.1.M1-SNAPSHOT-distribution/dubbo-riskcontrol-0.0.1.M1-SNAPSHOT/lib");
//		parseMetriscs("F:/开发源码/core-base/target/classes");
	}

}
