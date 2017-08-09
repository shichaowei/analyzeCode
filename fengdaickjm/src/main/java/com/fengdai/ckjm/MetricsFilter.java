/*
 * (C) Copyright 2005 Diomidis Spinellis
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.fengdai.ckjm;

import org.apache.bcel.classfile.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Convert a list of classes into their metrics. Process standard input lines or
 * command line arguments containing a class file name or a jar file name,
 * followed by a space and a class file name. Display on the standard output the
 * name of each class, followed by its six Chidamber Kemerer metrics: WMC, DIT,
 * NOC, CBO, RFC, LCOM
 *
 * @see ClassMetrics
 * @version $Revision: 1.9 $
 * @author <a href="http://www.spinellis.gr">Diomidis Spinellis</a>
 */
public class MetricsFilter {
	/**
	 * True if the measurements should include calls to the Java JDK into account
	 */
	private static boolean includeJdk = false;

	/** True if the reports should only include public classes */
	private static boolean onlyPublic = false;

	/**
	 * Return true if the measurements should include calls to the Java JDK into
	 * account
	 */
	public static boolean isJdkIncluded() {
		return includeJdk;
	}

	/** Return true if the measurements should include all classes */
	public static boolean includeAll() {
		return !onlyPublic;
	}

	/**
	 * Load and parse the specified class. The class specification can be either a
	 * class file name, or a jarfile, followed by space, followed by a class file
	 * name.
	 * 
	 * @param files
	 *            所有的class文件路径
	 * @param map<调用链：链式内容；方法与注解：链式内容>
	 * @return
	 */
	private HashMap<String, String> processClass(ClassMetricsContainer cm, String clspec, String[] files) {
		HashMap<String, String> result = new HashMap<String, String>();
		int spc;
		JavaClass jc = null;
		// System.out.println(clspec.indexOf(' '));
		// if ((spc = clspec.indexOf(' ')) != -1) {
		// String jar = clspec.substring(0, spc);
		// clspec = clspec.substring(spc + 1);
		// try {
		// jc = new ClassParser(jar, clspec).parse();
		//
		// } catch (IOException e) {
		// System.err.println("Error loading " + clspec + " from " + jar + ": " + e);
		// }
		// } else {
		try {
			jc = new ClassParser(clspec).parse();
			// System.out.println(jc.toString());
			// if(jc.toString().contains("com.fengdai.base.form.AbstractPageForm")) {
			// System.out.println("com.fengdai.base.form.AbstractPageForm");
			// }
		} catch (IOException e) {
			System.err.println("Error loading " + clspec + ": " + e);
		} catch (ClassFormatException e) {
			System.err.println("Error loading " + clspec + ": " + e);
		}
		// }
		if (jc != null) {
			ClassVisitor visitor = new ClassVisitor(jc, cm, files);
			visitor.start();
			visitor.end();
			// 打印调用关系
			// System.out.println(visitor.getresult());
			result.put("callParis", visitor.getresult());
			result.put("annotParis", visitor.getAnnoParisresult());
		}
		return result;
		// return clspec;
	}

	/**
	 * The interface for other Java based applications. Implement the outputhandler
	 * to catch the results
	 *
	 * @param files
	 *            Class files to be analyzed
	 * @param outputHandler
	 *            An implementation of the CkjmOutputHandler interface
	 */
	public HashMap<String, String> runMetrics(final String[] files, CkjmOutputHandler outputHandler) {

		final ClassMetricsContainer cm = new ClassMetricsContainer();
		final StringBuffer callPairsResultBuffer = new StringBuffer();
		final StringBuffer annotParisResultBuffer = new StringBuffer();
		HashMap<String, String> result = new HashMap<String, String>();
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(4);
		for (int index = 0; index < files.length; index++) {
			final int i = index;
			fixedThreadPool.execute(new Runnable() {
				public void run() {
					synchronized (this) {
						HashMap<String, String> resultmap = processClass(cm, files[i], files);
						String temp = resultmap.get("callParis");
						String annotParistemp = resultmap.get("annotParis");

						if (!annotParistemp.isEmpty()) {
							annotParisResultBuffer.append(annotParistemp);
						}
						if (!temp.isEmpty()) {
							callPairsResultBuffer.append(temp);
							callPairsResultBuffer.append(",");
						}
					}
				}
			});
		}
		fixedThreadPool.shutdown();
		while (true) {
			if (fixedThreadPool.isTerminated()) {

				result.put("annotParis", annotParisResultBuffer.toString());
				result.put("callParis", callPairsResultBuffer.toString());
				break;
			}

		}

		String tmp = result.toString();

		return result;
		// cm.printMetrics(outputHandler);
	}

}
