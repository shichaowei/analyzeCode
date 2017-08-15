package com.fengdai.qa.service;

import java.util.ArrayList;
import java.util.HashSet;

import com.fengdai.qa.model.tree;

public interface ParseSrcService {
	
	public  ArrayList<String> getroots() ;
	
	public String getTreeJson(String rootNode);
	
	public  void parseSources(String classesDir,HashSet<String> varchange);
	
	public  void buildToLinkTrees(String classesDir);
	
	public ArrayList<tree<String>> getLinkTrees();

}
