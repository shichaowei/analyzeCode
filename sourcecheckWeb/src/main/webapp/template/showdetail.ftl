<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>节点详情</title>
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

		<div id="continer"></div>
	</body>
</html>
<script language="javascript" src="js/jquery.js"></script>
<script language="javascript">
     //主方法，运用递归实现
     function createTree(jsons,pid){
         if(jsons != null){
             var ul = '<ul class="">' ;
             for(var i=0;i<jsons.length;i++){
                 if(jsons[i].pid == pid){
                     ul += '<li>' + jsons[i].name + "</li>" ;
                     ul += createTree(jsons,jsons[i].id) ;
                 }
             }
             ul += "</ul>" ;
        }
         return ul ;
     }

     $(function(){
         var ul = createTree(${jsonData},0) ;

         $("#continer").append(ul) ;

         //控制菜单的隐藏显示
         $("ul[class] li").each(function(){
             $(this).click(function(){
                 $(this).next().toggle() ;
             }) ;
         }) ;

     }) ;
</script>