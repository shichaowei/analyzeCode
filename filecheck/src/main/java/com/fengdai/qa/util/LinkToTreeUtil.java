package com.fengdai.qa.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.fengdai.qa.link.element;
import com.fengdai.qa.link.tree;


public class LinkToTreeUtil {

	final  LogUtil logger  =  new LogUtil(this.getClass());
	
	
	public  ArrayList<tree<String>> createTree(String temp) {
//		System.out.println(temp); //由于tmep太大，不能直接读取到屏幕 会导致IO傻逼
		WriteToFile.writeFile(temp.replace(",", "\n"), "output/调用关系.txt");
		
		ArrayList<String> lefteles = new ArrayList<>();
		ArrayList<String> righteles = new ArrayList<>();
		HashSet<String> rootelestemp = new HashSet<String>();
		HashSet<String> rooteles = new HashSet<String>();
		ArrayList<element> varlist = new ArrayList<>();
		String[] temps = temp.split(",");
		for (String var1 : temps) {
			String[] var2 = var1.split("被调用于");
			element ele1 = new element(var2[0], var2[1]);
			varlist.add(ele1);
			//处理lambda函数
			if(var2[0].contains("lambda")){
				throw new RuntimeException("被调用方有lambda，代码还没有做处理");
			}
			if(var2[1].contains("lambda")){
				var2[1]=var2[1].replace("lambda$", "").replace("$", "!!").replaceAll("!![0-9]*$", "");
			}
			//处理内部对象
			for(int i=0;i<2;i++){
				if (var2[i].contains("$") && var2[i].contains("<init>")) {
					var2[i]=var2[i].replace("<init>", "process");
				}
			}
			//处理映射关系
			//api层在左侧(调用api等价于调用实现类，故api和实现类要对应起来)
			if(var2[0].contains(".service.")&& !var2[0].contains(".service.impl.") && !var2[0].contains("Impl")){
				var2[0]=var2[0].replace(".service.", ".service.impl.").replace("Service", "ServiceImpl");
			}
			if(var2[1].contains(".service.")&& !var2[1].contains(".service.impl.") && !var2[1].contains("Impl")){
				System.out.println(var1);
				throw new RuntimeException("api层有调用其他代码，api不是接口，无法处理");
			}
			lefteles.add(var2[0]);
			righteles.add(var2[1]);
		}
		// 拿到根节点
		for (String righttest : righteles) {
			// System.out.println(righttest);
			if (!lefteles.contains(righttest)) {
				rootelestemp.add(righttest);
			}
		}

		//处理根节点只保留client曝露出来的dubbo接口以及rest接口
		try {
			//写入rootnodes文件
			String rootTemp="";
			logger.logInfo("根节点为:");
			FileWriter fw = new FileWriter("output/rootnodes.txt");
			for (String root : rootelestemp){
				rooteles.add(root);
				logger.logInfo(root);
				rootTemp+=root;
				rootTemp+="\n";
//				if(root.contains("api")||root.contains("resource")){
//					rooteles.add(root);
//					rootTemp+=root;
//					logger.logInfo(root);
//					rootTemp+="\n";
//				}
			}
			fw.write(rootTemp);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	
		
		ArrayList<tree<String>> result = new ArrayList<>();
		// 生成一颗颗树
		for (String root : rooteles) {
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
							checklistB.add(var.getleft());
						}
					}
				}
				
				for(String var:checklistB){
					logger.logInfo("要check的节点为："+var);
				}
				//如果要check的节点不在右边list节点列表，即表示check的节点是叶子节点，没有子节点
				checklist = checklistB;
				logger.logInfo("总check的节点数目"+checklist.size());
				for (Iterator<String> it = checklist.iterator(); it.hasNext();) {
					String var = it.next();
					if (!righteles.contains(var)) {
						it.remove();
						logger.logInfo("因为节点属于叶子节点，check中去除"+var);
					}	
				}
				//比如A调用B B调用A 那么要check总是A B
				//还有一些蛋疼的A调用B，C调用B D调用B 基础的B永远从checklist里面踢不出来
				for (Iterator<String> it = checklist.iterator(); it.hasNext();) {
					String var = it.next();
					//解决坑爹的循环调用
					if(tree.search(tree.root, var)!=null){
						it.remove();
						logger.logInfo("因为坑爹的循环调用去除"+var);
					}
				}
				
				logger.logInfo("真正要check的数目"+checklist.size());
				if(checklist.size() >varlist.size()){
					throw new RuntimeException("有个坑");
				}
			}
			result.add(tree);
			logger.logInfo("tree is:");
			tree.showNode(tree.root);
			logger.logInfo("tree is over");
			ArrayList<String> vartree= new ArrayList<>();
		}
		
		
		return result;

	}
	
	
	
	public static void main(String argv[]){
		List[] test = new List[50];
		ArrayList<String>[] lists = (ArrayList<String>[])new ArrayList[3];
		List<String>[] temp = new List[3];
		temp[0] = new ArrayList<String>();
		
		
		
	}

}
