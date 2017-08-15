package com.fengdai.qa.model;

import java.util.ArrayList;
import java.util.List;

public class treeNode<T> {
    public T t;
    private treeNode<T> parent;
    
    public List<treeNode<T>> nodelist;
    
    public treeNode(T stype){
        t      = stype;
        parent = null;
        nodelist = new ArrayList<treeNode<T>>();
    }

    public treeNode<T> getParent() {
        return parent;
    }    
}