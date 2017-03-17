package com.app;

import java.util.ArrayList;

public class Test {

	public Test() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		ArrayList<String> temp = new ArrayList<>();
		temp.add("a");
		temp.add("b");
		temp.add("c");
		for(String var:temp){
			if(var.equals("b")){
				temp.remove("b");
			}
		}
		for(String var:temp){
			System.out.println(var);
		}
		
	}

}
