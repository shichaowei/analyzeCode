import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.tools.ant.DirectoryScanner;

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
//			System.out.println(files[ioe]);
		}
		
		
		try {
			FileOutputStream var6 = new FileOutputStream(out);
			PrintPlainResults var7 = new PrintPlainResults(new PrintStream(var6));
			MetricsFilter.runMetrics(files, var7, false);
			var6.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
