package com.app;

public class tree<T> {
    
    public treeNode<T> root;
    
    public tree(){}
        
    public void addNode(treeNode<T> node, T newNode){
        //���Ӹ��ڵ�
        if(null == node){
            if(null == root){
                root = new treeNode(newNode);
            }
        }else{
                treeNode<T> temp = new treeNode(newNode);
                node.nodelist.add(temp);
        }
    }
    
    /*    ����newNode����ڵ� */
    public treeNode<T> search(treeNode<T> input, T newNode){
    
        treeNode<T> temp = null;
        
        if(input.t.equals(newNode)){
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
    
    public void showNode(treeNode<T> node){
        if(null != node){
            //ѭ������node�Ľڵ�
            System.out.println(node.t.toString());
            
            for(int i = 0; i < node.nodelist.size(); i++){
                showNode(node.nodelist.get(i));
            }            
        }
    }
}