# analyzeCode
使用ckjm分析代码后，生成一系列父子节点：如A被B调用，B被C调用；根据这些父子关系组装成一棵树
用法  ：
1.ckjmlearn是最主要的代码 ckjm 主要用于生成A调用于B B调用于C 这样的管理
2.Linklearn主要用来生成树
3.入参是修改的class，出来的是影响的接口范围
