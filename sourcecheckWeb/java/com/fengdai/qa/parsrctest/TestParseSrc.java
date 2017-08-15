package com.fengdai.qa.parsrctest;

import java.util.HashSet;

import com.fengdai.qa.service.impl.ParseSrcServiceImpl;

public class TestParseSrc {
	
	public static void main(String[] args) {
//		final String filedir="D:\\jenkins\\workspace\\fengdai";
//		final String filedir="D:\\jenkins\\workspace\\test";
		final String filedir="D:\\jenkins\\workspace\\fengdai\\fengdai-core-shop-test";
//		final String filedir="F:\\test\\代码分析\\analyzeCode\\testbcel\\target";
//		final String filedir="D:\\jenkins\\workspace\\fengdai\\fengdai-core-activity-test";
		// TODO Auto-generated method stub
		//		parseMetriscs("F:/github/接口测试Demo/apptest/target");
		//		parseMetriscs("F:/开发源码/core-base/target");
		//		parseMetriscs("F:/开发源码/core-base/target/classes");

		final HashSet<String> varchange=new HashSet();
		varchange.add("com.fengdai.shop.service.impl.ShopOrderHandleServiceImpl");
//		varchange.add("com.fengdai.activity.dao.ActivityBannerDao的方法deleteByPrimaryKey:java.lang.String");
		//		varchange.add("com.wsc.testbcel.testbcel.Programmer");
		//		parseMetriscs("E:/开发源码class/dubbo-riskcontrol-0.0.1.M1-SNAPSHOT-distribution/dubbo-riskcontrol-0.0.1.M1-SNAPSHOT/lib",varchange);
		new ParseSrcServiceImpl().parseSources(filedir,varchange);
		//		parseMetriscs("F:/开发源码/fengdai-core-channel-test/workspace/core-channel/target/classes/com/fengdai/channel/service/impl/temp",varchange);
		//		parseMetriscs("F:/test/testbcel/target/classes/org/wsc/testbcel/testbcel/test",varchange);
	}

}
