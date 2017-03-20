package com.app;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class HandlerTree {

	public  ArrayList<tree<String>> process(String temp) {
//		System.out.println(temp); //由于tmep太大，不能直接读取到屏幕 会导致IO傻逼
		try {
			FileWriter fw = new FileWriter("output/调用关系.txt");
			fw.write(temp);  
			fw.close();  
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		
		
		
		ArrayList<String> lefteles = new ArrayList<>();
		ArrayList<String> righteles = new ArrayList<>();
		HashSet<String> rooteles = new HashSet<String>();
		ArrayList<element> varlist = new ArrayList<>();
		String[] temps = temp.split(",");
		for (String var1 : temps) {
			String[] var2 = var1.split("被调用于");
			element ele1 = new element(var2[0], var2[1]);
			varlist.add(ele1);
			lefteles.add(var2[0]);
			righteles.add(var2[1]);
		}
		// 拿到根节点
		for (String righttest : righteles) {
			// System.out.println(righttest);
			if (!lefteles.contains(righttest)) {
				rooteles.add(righttest);
			}
		}

	

		ArrayList<tree<String>> result = new ArrayList<>();
		// 生成一颗颗树
		for (String root : rooteles) {
			System.out.println("root is:"+root);
			tree<String> tree = new tree<String>();
			tree.addNode(null, root);
			HashSet<String> checklist = new HashSet<>();
			checklist.add(root);

			while (!checklist.isEmpty()) {
				//寻找到check节点最近的子节点，存储子节点即为下次要check的根节点list
				HashSet<String> checklistB = new HashSet<>();
				for (String check : checklist) {
					for (element var : varlist) {
						if (var.getright().equals(check)) {
							tree.addNode(tree.getNode(var.getright()), var.getleft());
							System.out.println("tree is:");
							tree.showNode(tree.root);
							checklistB.add(var.getleft());
						}
					}
				}
				
				for(String var:checklistB){
					System.out.println("要check的节点为："+var);
				}
				//如果要check的节点不在右边list节点列表，即表示check的节点是叶子节点，没有子节点
				checklist = checklistB;
				System.out.println("总check的节点数目"+checklist.size());
				for (Iterator<String> it = checklist.iterator(); it.hasNext();) {
					String var = it.next();
					if (!righteles.contains(var)) {
						it.remove();
						System.out.println("去除"+var);
					}	
				}
				//比如A调用B B调用A 那么要check总是A B
				//还有一些蛋疼的A调用B，C调用B D调用B 基础的B永远从checklist里面踢不出来
				for (Iterator<String> it = checklist.iterator(); it.hasNext();) {
					String var = it.next();
					//解决坑爹的循环调用
					if(tree.search(tree.root, var)!=null){
						it.remove();
						System.out.println("因为坑爹的循环调用去除"+var);
					}
				}
				
				System.out.println("要check的数目"+checklist.size());
				if(checklist.size() >varlist.size()){
					throw new RuntimeException("有个坑");
				}
			}
			result.add(tree);
			System.out.println("tree is");
			tree.showNode(tree.root);
		}
		
		return result;

	}
	
	
	
	public static void main(String argv[]){
		
	}

}
