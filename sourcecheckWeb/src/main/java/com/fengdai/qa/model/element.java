package com.fengdai.qa.model;

/**
 * 
 * 左侧是被调用方 右侧是调用方
 * @author hzweisc
 *
 */
public class element {

	
	@Override
	public String toString() {
		return "element [left=" + left + ", right=" + right + "]";
	}
	
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
