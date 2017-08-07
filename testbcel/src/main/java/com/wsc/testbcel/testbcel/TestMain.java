package com.wsc.testbcel.testbcel;

import java.util.Arrays;
import java.util.List;

import javax.swing.plaf.synth.SynthStyle;

public class TestMain {

	
//	public void test() {
//		List<Integer> list = Arrays.asList(1,2,3,4,5,6,7);
//		list.stream().map((x) -> x*x).forEach(System.out::println);
//	}
	
	public static void main(String argv[]) {
//		new AProgrammerTest().doBcelPlan("temp");
//		new AProgrammerTest("sddfdfd").doBcelPlan("temp");
//		String var1="com.fengdai.activity.service.impl.ActivityUserGroupListServiceImpl$5的方法<init>:com.fengdai.activity.service.impl.ActivityUserGroupListServiceImpljava.lang.Objectjava.lang.String被调用于com.fengdai.activity.service.impl.ActivityUserGroupListServiceImpl的方法clean:java.lang.String";
//		String[] var2 = var1.split("被调用于");
//		//处理内部对象
//		//默认发现process对应的是init process都没有参数 init参数有 我们做去参处理
//		for(int i=0;i<2;i++){
//			if (var2[i].contains("$") && var2[i].contains("<init>")) {
//				var2[i]=var2[i].substring(0, var2[i].indexOf(":")).replace("<init>", "process")+":";
//			}
//		}
//		for(String temp:var2)
//			System.out.println(temp.toString());
//		new TestMain().test();
		
		List<Integer> list = Arrays.asList(1,2,3,4,5,6,7);
		list.stream().map((x) -> x*x).forEach(System.out::println);
	}
}
