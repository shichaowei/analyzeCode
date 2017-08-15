<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>首页</title>
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
			<a href="/showroots"> 显示所有的根节点</a>
		</div>
		<div>
			<h2>扫描class文件</h2>
			<form action="/api/parseSrc" method="post">
	  			<p>源码根目录路径: <input type="text" name="srcDir" placeholder="D:\\jenkins\\workspace\\fengdai\\fengdai-core-shop-test"/></p>
	  			<input type="submit" value="Submit" />
			</form>
		</div>
		



	</body>
</html>