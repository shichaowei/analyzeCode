package com.fengdai.qa.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fengdai.qa.service.LinkToTreeService;
import com.fengdai.qa.service.ParseSrcService;

@Controller
public class HomeController {

	@Resource
	public LinkToTreeService linkToTreeServiceImpl;
	@Resource
	public ParseSrcService parseSrcServiceImpl;

	@RequestMapping({"/"})
	public String getIndex(HttpServletRequest request,HttpServletResponse response, ModelMap map){

		return "index";
	}

	@RequestMapping({"/showroots"})
	public String showroots(HttpServletRequest request,HttpServletResponse response, ModelMap map){
		map.put("roots", parseSrcServiceImpl.getroots());
		return "showroots";
	}

	@RequestMapping({"/showTreeDetail"})
	public String showrootDetail(@RequestParam("root") String root,HttpServletRequest request,HttpServletResponse response, ModelMap map){
		map.put("jsonData", parseSrcServiceImpl.getTreeJson(root));


		return "showdetail";
	}

	@RequestMapping({"/api/parseSrc"})
	public String parseSrc(@RequestParam("srcDir") String srcDir,HttpServletRequest request,HttpServletResponse response, ModelMap map){
		parseSrcServiceImpl.buildToLinkTrees(srcDir);
		map.put("roots", parseSrcServiceImpl.getroots());
		System.out.println( parseSrcServiceImpl.getroots());
		return "showroots";
	}





}
