package com.fengdai.qa.parsrctest;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import javax.management.RuntimeErrorException;

import org.junit.Assert;

public class TestThread {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		final ArrayList<String> themelist = new ArrayList<>();
		for (int i = 0; i < 5000000; i++) {
			themelist.add("a$1的方法<init>:");
		}
		final ArrayList<String> result = new ArrayList<>();
//		final ThreadPoolExecutor fixedThreadPool = new ThreadPoolExecutor(5, 20, 200, TimeUnit.MILLISECONDS,new ArrayBlockingQueue<Runnable>(6000));
		final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
		final long nowtime = System.currentTimeMillis();
		for (int index = 0; index < 500000; index++) {
			final int i =index;
			fixedThreadPool.submit(new Runnable() {
				
				final String var2 = themelist.get(i);
				@Override
				public void run() {
			
					final String pattern = ".*\\$[0-9]+的方法<init>.*[^lambda].*";

					
						if (Pattern.matches(pattern, var2)) {
							final String processTemp = var2.substring(0, var2.indexOf(":")).replace("<init>", "process")
									+ ":";
							Assert.assertNotNull(processTemp);
							result.add(processTemp);

						}
					

				}
			}).get();
		}
		fixedThreadPool.shutdown();
		while (true) {
			if (fixedThreadPool.isTerminated()) {
				System.out.println("finish");
				break;
			}
			System.out.println("not finish");
			Thread.sleep(500) ;  
		}

		System.out.println(System.currentTimeMillis()-nowtime);
		for (final String temp : result) {
//			System.out.println(temp);
			Assert.assertNotNull(temp);
			if (temp.contains("init")) {
				System.out.println(temp);
				throw new RuntimeErrorException(null, "sfdffdf");
			}
		}

	}

}
