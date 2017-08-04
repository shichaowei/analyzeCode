package com.wsc.testbcel.testbcel;

public class AProgrammerTest extends BaseProgrammer{

	
	public AProgrammerTest() {
		super();
		System.out.println("init no argm");
		
	}
	public AProgrammerTest(String temp) {
		System.out.println("init has one argv:"+temp);
		
	}
	
	public void doBcelPlan(String temp) {
		System.out.println(temp);
	}

	public void doBcelPlan() {
		doBcelPlan("sfdfdfdfd");
	}



}
