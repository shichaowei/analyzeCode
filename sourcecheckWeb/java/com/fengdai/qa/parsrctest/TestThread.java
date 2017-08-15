package com.fengdai.qa.parsrctest;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;


import com.fengdai.qa.model.element;


public class TestThread {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(20);
		String temp ="com.fengdai.shop.service.impl.ShopOrderHandleServiceImpl$1的方法<init>:";
		ArrayList<String> vArrayList= new ArrayList<>();
		for(int i=0;i<20000;i++)
			vArrayList.add(temp);
		ArrayList<String> result = new ArrayList<>();
		for(int i=0;i<20000;i++) {
			final int Index = i;
			try {
				fixedThreadPool.submit(new Runnable() {
					final String var2= vArrayList.get(Index);
					@Override
					public void run() {
						String pattern = ".*\\$[0-9]+的方法<init>.*[^lambda].*";
						
							if (Pattern.matches(pattern, var2)) {
								String processTemp=var2.substring(0, var2.indexOf(":")).replace("<init>","process")+":";
								
									result.add(processTemp);
								
							}
					}
					
					
				}).get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		
		fixedThreadPool.shutdown();

		while (true) {
			if (fixedThreadPool.isTerminated()) {
				for(String temp1:result) {
					System.out.println(temp1);
				}
				break;
			}

		}
		

	

	}
}
