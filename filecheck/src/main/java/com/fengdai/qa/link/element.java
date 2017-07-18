package com.fengdai.qa.link;

/**
 * 
 * 左侧是被调用方 右侧是调用方
 * @author hzweisc
 *
 */
public class element {

	String left;
	String right;
	public element(String left,String right) {
		this.left=left;
		this.right=right;
	}
	
	public String getleft(){
		return left;
	}
	public String getright(){
		return right;
	}
	
	

}
