package com.fengdai.qa.parsrctest;

public class TestString {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		String temp="com.fengdai.finance.service.impl.BizLoanBillServiceImpl的方法lambda$mergeBills$83";
//		for(String var:temp.split("lambda$")){
//			System.out.println(var);
//		}
//		System.out.println(temp.replace("lambda$", "").replace("$", "!!").replaceAll("!![0-9]*$", ""));
		String varapi="com.fengdai.shop.service.ShopOrderInfoService的方法getShopOrderInfoByCode";
		System.out.println(varapi.replace(".service.", ".service.impl.").replace("Service", "ServiceImpl"));
		
		
		String varClasspath="F:\\开发源码\\fengdai-core-account-test\\workspace\\md-account-api\\target\\classes\\com\\tairanchina\\md\\account\\AccountCst$Cache.class";
		String[] var= varClasspath.split("com");
		System.out.println(("com"+var[var.length-1]).replace("\\", ".").replace(".class", "").split("\\$")[0]);
		
	}

}
