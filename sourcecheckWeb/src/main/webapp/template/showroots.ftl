<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>根节点们</title>
		<style type="text/css">
      body{
     font-size:14px;
      margin:0;}
     div{
     width:auto;
     height:auto;
     line-height:150%;}
     ul{
     list-style:none;
     margin-left:-20px;}
    ul li:hover{
     background-color:#DDDDDD;
     color:#FF0000;
     cursor:pointer;}
		</style>
	</head>
	<body>
	
		<div>
		<#if !roots?has_content>
		    <div class="n-result">
		        <p>暂无内容！</p>
		        <a href="/" class="link">返回首页</a>
		    </div>
    	<#else>
			<#list roots as x>
					<li id="p-${x}">
						<a href="/showTreeDetail?root=${x}" class="link">${x}</a>
					</li>
					
			</#list>
		</div>
		</#if>

	
	</body>
</html>