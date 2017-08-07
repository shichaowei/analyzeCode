package com.fengdai.qa.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import com.fengdai.qa.link.element;
import com.fengdai.qa.link.tree;


public class LinkToTreeUtil {

	final  LogUtil logger  =  new LogUtil(this.getClass());


	public  ArrayList<tree<String>> createTree(String temp) {
		//		System.out.println(temp); //由于tmep太大，不能直接读取到屏幕 会导致IO傻逼
		logger.logDebug("调用关系");
//		logger.logDebug(temp);
		WriteToFile.clearWriteFile(temp.replace(",", "\n"), "output/原始调用关系.txt");

		ArrayList<String> lefteles = new ArrayList<>();
		ArrayList<String> righteles = new ArrayList<>();
		HashSet<String> rootelestemp = new HashSet<String>();
		HashSet<String> rooteles = new HashSet<String>();
		ArrayList<element> varlist = new ArrayList<>();
		String[] temps = temp.split(",");
		for (String var1 : temps) {
			//不做处理
			//内部类  com.fengdai.activity.constant.ActivityBizConstant$DicKey
			//内部对象 com.fengdai.activity.service.impl.CouponsDistributeServiceImpl$1
			//内部类访问外部使用access$iii(Outer) com.fengdai.activity.service.impl.CouponsDistributeServiceImpl的方法access$0
			//类的初始化方法<clinit> 静态变量初始化语句和静态块的执行 com.fengdai.shop.service.impl.ShopOrderHandleServiceImpl的方法<clinit>: 不进入调用链
			
			String[] var2 = var1.split("被调用于");
			//处理lambda函数(不包括直接lambda$0，只处理lambda$test$236)
			//处理com.fengdai.finance.service.impl.BizLoanBillServiceImpl的方法lambda$mergeBills$83----com.fengdai.finance.service.impl.BizLoanBillServiceImpl的方法mergeBills
//			if(var2[0].contains("lambda")){
//				throw new RuntimeException("被调用方有lambda，代码还没有做处理");
//			}
			String pattern = ".*的方法lambda\\$[a-zA-Z]+.*";
			if(var2[1].contains("lambda")&& Pattern.matches(pattern, var2[1])){
				var2[1]=var2[1].replace("lambda", "").replace("$", "!!").replaceAll("!![0-9]*", "");
			}
			
			//处理内部对象有lambda函数 适度扩大范围
			//com.fengdai.activity.service.impl.ActivityUserGroupListServiceImpl$4的方法lambda$0:java.lang.Stringcom.fengdai.activity.model.ActivityInfo--
			//-com.fengdai.activity.service.impl.ActivityUserGroupListServiceImpl$4的方法process:
//			if(var2[0].contains("lambda")){
//				throw new RuntimeException("被调用方有lambda，代码还没有做处理");
//			}
			pattern = ".*\\$[0-9]+的方法lambda\\$[0-9]+.*";
			if(Pattern.matches(pattern, var2[1])){
				var2[1]=var2[1].split("的方法")[0]+"的方法process:";
			}
			
			
			//处理内部对象
			//默认发现process/run对应的是init process(LogPrepare)/run(new Runnable(){})都没有参数 init参数有 我们做去参处理
//			for(int i=0;i<2;i++){
//				if (var2[i].contains("$") && var2[i].contains("<init>")) {
//					var2[i]=var2[i].substring(0, var2[i].indexOf(":")).replace("<init>", "process")+":";
//				}
//			}
			pattern = ".*\\$[0-9]+的方法<init>.*[^lambda].*";
			for(int i=0;i<2;i++){
				if (Pattern.matches(pattern, var2[i])) {
					String processTemp=var2[i].substring(0, var2[i].indexOf(":")).replace("<init>","process")+":";
					String runTemp=var2[i].substring(0, var2[i].indexOf(":")).replace("<init>","run")+":";
					if(lefteles.contains(processTemp)||righteles.contains(processTemp))
						var2[i]=var2[i].substring(0, var2[i].indexOf(":")).replace("<init>","process")+":";
					else if (lefteles.contains(runTemp)||righteles.contains(runTemp)) {
						var2[i]=var2[i].substring(0, var2[i].indexOf(":")).replace("<init>","run")+":";
					}
				}
			}
			//处理映射关系
			//api层在左侧(调用api等价于调用实现类，故api和实现类要对应起来)
			if(var2[0].contains(".service.")&& !var2[0].contains(".service.impl.") && !var2[0].contains("Impl")){
				var2[0]=var2[0].replace(".service.", ".service.impl.").replace("Service", "ServiceImpl");
			}
			//			if(var2[1].contains(".service.")&& !var2[1].contains(".service.impl.") && !var2[1].contains("Impl")){
			//				System.out.println(var1);
			//				throw new RuntimeException("api层有调用其他代码，api不是接口，无法处理");
			//			}
			lefteles.add(var2[0]);
			righteles.add(var2[1]);
			element ele1 = new element(var2[0], var2[1]);
			varlist.add(ele1);
		}
		StringBuffer tempDealed = new StringBuffer();
		for(element var:varlist) {
			tempDealed.append(var.getleft()+"被调用于"+var.getright()+"\n");
		}
		WriteToFile.clearWriteFile(tempDealed.toString(), "output/处理后的调用关系.txt");
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
			logger.logDebug("根节点为:");
			FileWriter fw = new FileWriter("output/rootnodes.txt");
			for (String root : rootelestemp){
				rooteles.add(root);
				logger.logDebug(root);
				rootTemp+=root;
				rootTemp+="\n";
				//				if(root.contains("api")||root.contains("resource")){
				//					rooteles.add(root);
				//					rootTemp+=root;
				//					logger.logDebug(root);
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

			int checkNum=0;
			while (!checklist.isEmpty()) {
				//循环调用挪到最前面，先去重,根节点要判断一下 不要去掉跟节点
				//比如A调用B B调用A 那么要check总是A B 
				//去重操作不理想 是树根又是树枝的没法玩 暴力分析 循环15次以上 直接跳出不做处理
				//com.fengdai.base.utils.GsonUtil的方法toList:com.google.gson.JsonArray && com.fengdai.base.utils.GsonUtil的方法toMap:com.google.gson.JsonObject
				if(checkNum>=10) {
					logger.logDebug("第"+checkNum+"次check");
					break;
				}
				
				logger.logDebug("真正要check的数目"+checklist.size());
				for(String var:checklist){
					logger.logDebug("真正要check的节点为："+var);
				}
				if(checklist.size() >varlist.size()){
					throw new RuntimeException("有个坑");
				}
				
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
					logger.logDebug("要check的节点为："+var);
				}
				//如果要check的节点不在右边list节点列表，即表示check的节点是叶子节点，没有子节点
				checklist = checklistB;
				logger.logDebug("总check的节点数目"+checklist.size());
				for (Iterator<String> it = checklist.iterator(); it.hasNext();) {
					String var = it.next();
					if (!righteles.contains(var)) {
						it.remove();
						logger.logDebug("因为节点属于叶子节点，check中去除"+var);
					}
				}
				checkNum++;
				
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
		ArrayList<String>[] lists = new ArrayList[3];
		List<String>[] temp = new List[3];
		temp[0] = new ArrayList<String>();



	}

}
