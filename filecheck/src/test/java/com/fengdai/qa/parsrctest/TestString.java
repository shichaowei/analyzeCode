package com.fengdai.qa.parsrctest;

import java.util.regex.Pattern;

public class TestString {

	
	public static void test(String var1) {
		
		String[] var2 = var1.split("被调用于");
		//处理lambda函数,不包括内部对象有lambda函数的
		//处理com.fengdai.finance.service.impl.BizLoanBillServiceImpl的方法lambda$mergeBills$83----com.fengdai.finance.service.impl.BizLoanBillServiceImpl的方法mergeBills
		if(var2[0].contains("lambda")){
			throw new RuntimeException("被调用方有lambda，代码还没有做处理");
		}
		String pattern = ".*lambda\\$[0-9]+.*";
		if(var2[1].contains("lambda")&& !Pattern.matches(pattern, var2[1])){
			var2[1]=var2[1].replace("lambda", "").replace("$", "!!").replaceAll("!![0-9]*", "");
		}
		
		//处理内部对象有lambda函数 适度扩大范围
		//com.fengdai.activity.service.impl.ActivityUserGroupListServiceImpl$4的方法lambda$0:java.lang.Stringcom.fengdai.activity.model.ActivityInfo--
		//-com.fengdai.activity.service.impl.ActivityUserGroupListServiceImpl$4的方法process:
		if(var2[0].contains("lambda")){
			throw new RuntimeException("被调用方有lambda，代码还没有做处理");
		}
		pattern = ".*\\$[0-9]+的方法lambda\\$[0-9]+.*";
		if(Pattern.matches(pattern, var2[1])){
			var2[1]=var2[1].split("的方法")[0]+"的方法process:";
		}
		
		//处理内部对象
		//默认发现process对应的是init process都没有参数 init参数有 我们做去参处理
		for(int i=0;i<2;i++){
			if (var2[i].contains("$") && var2[i].contains("<init>")) {
				var2[i]=var2[i].substring(0, var2[i].indexOf(":")).replace("<init>", "process")+":";
			}
		}
		//处理映射关系
		//api层在左侧(调用api等价于调用实现类，故api和实现类要对应起来)
		if(var2[0].contains(".service.")&& !var2[0].contains(".service.impl.") && !var2[0].contains("Impl")){
			var2[0]=var2[0].replace(".service.", ".service.impl.").replace("Service", "ServiceImpl");
		}
		System.out.println(var2[0]+var2[1]);
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub


//		String temp="com.fengdai.finance.service.impl.BizLoanBillServiceImpl的方法lambda$mergeBills$83";
//		String temp = "com.fengdai.activity.service.impl.ActivityUserGroupListServiceImpl$4的方法lambda$0:java.lang.Stringcom.fengdai.activity.model.ActivityInfo";
//		for (String var : temp.split("lambda\\$")) {
//			System.out.println(var);
//		}
		
		
//		System.out.println(temp.replace("lambda", "").replace("$", "!!").replaceAll("!![0-9]*", ""));

		// String
		// varapi="com.fengdai.shop.service.ShopOrderInfoService的方法getShopOrderInfoByCode";
		// System.out.println(varapi.replace(".service.",
		// ".service.impl.").replace("Service", "ServiceImpl"));
		//
		//
		// String
		// varClasspath="F:\\开发源码\\fengdai-core-account-test\\workspace\\md-account-api\\target\\classes\\com\\tairanchina\\md\\account\\AccountCst$Cache.class";
		// String[] var= varClasspath.split("com");
		// System.out.println(("com"+var[var.length-1]).replace("\\",
		// ".").replace(".class", "").split("\\$")[0]);
		// System.out.println("com.wsc.testbcel.testbcel.Programmer".split("的方法").length);
		String content = "com.fengdai.base.service.impl.mq.LogPrepare的方法<init>:被调用于com.fengdai.activity.service.impl.CouponsDistributeServiceImpl$1的方法<init>:com.fengdai.activity.service.impl.CouponsDistributeServiceImpljava.lang.Object[]com.fengdai.activity.form.CardCouponsUserForm\r\n" + 
				"";
//		String content = "com.fengdai.finance.service.impl.BizLoanBillServiceImpl的方法lambda$mergeBills$83";
		test(content);
//		String pattern = ".*\\$[0-9]+的方法lambda\\$[0-9]+.*";
//		boolean isMatch = Pattern.matches(pattern, content);
//		System.out.println("字符串中是否包含了 'runoob' 子字符串? " + isMatch);
//		if(isMatch) {
//			System.out.println(content.split("的方法")[0]+"的方法process:");
//		}

	}

}
