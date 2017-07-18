package com.fengdai.qa.link;

import java.util.ArrayList;

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
        if(input.t.toString().contains(newNode.toString())){
        	return input;
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
    
    public void showNode(treeNode<T> node){
        
    	if(null != node){
            //循环遍历node的节点
            logger.logInfo(node.t.toString());
            

            for(int i = 0; i < node.nodelist.size(); i++){
            	showNode(node.nodelist.get(i));
            }            

        }
    }
    
}