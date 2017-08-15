package com.fengdai.qa.model;

import java.util.ArrayList;

import org.json.JSONObject;

import com.fengdai.qa.util.LogUtil;

public class tree<T> {
	final  LogUtil logger  =  new LogUtil(this.getClass());

	public treeNode<T> root;

	public tree(){}

	public void addNode(treeNode<T> node, T newNode){
		//增加根节点
		if(null == node){
			if(null == root){
				root = new treeNode(newNode);
			}
		}else{
			treeNode<T> temp = new treeNode(newNode);
			node.nodelist.add(temp);
		}
	}

	/*    查找newNode这个节点 */
	public treeNode<T> search(treeNode<T> input, T newNode){

		treeNode<T> temp = null;

		if(input.t.equals(newNode)){
			return input;
		}
		if(newNode.toString().split("的方法").length==1){
			String classvar=input.t.toString().split("的方法")[0];
			if(classvar.contains(newNode.toString())){
				return input;
			}
		}else{
			if(input.t.toString().contains(newNode.toString())){
				return input;
			}
		}

		for(int i = 0; i < input.nodelist.size(); i++){

			temp = search(input.nodelist.get(i), newNode);

			if(null != temp){
				break;
			}
		}

		return temp;
	}

	public treeNode<T> getNode(T newNode){
		return search(root, newNode);
	}

	public T getrootNode(){
		return this.root.t;
	}
	
	public void getTreeString(treeNode<T> node,int cengshu,ArrayList<JSONObject> treeJsonObject){
		JSONObject itemNode= new JSONObject();
		itemNode.put("id", cengshu);
		itemNode.put("pid",cengshu-1<0?0:cengshu-1);
		itemNode.put("name", node.t.toString());
		treeJsonObject.add(itemNode);
		if(null != node){
			//循环遍历node的节点
//			logger.logInfo(node.t.toString());
			for(int i = 0; i < node.nodelist.size(); i++){
				getTreeString(node.nodelist.get(i),cengshu+1,treeJsonObject);
			}

		}
	}
	

	public void showNode(treeNode<T> node,int cengshu){

		if(null != node){
			//循环遍历node的节点
			logger.logInfo("cengshu:"+cengshu+"content:"+node.t.toString());


			for(int i = 0; i < node.nodelist.size(); i++){
				showNode(node.nodelist.get(i),cengshu+1);
			}

		}
	}

}