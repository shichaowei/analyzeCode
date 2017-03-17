package com.app;

public class app {

    /**
     * @param args
*/
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        /*简单实现一个树的结构，后续完善解析xml             */
        /*写得满烂的，后续查阅一些其他代码                2012-3-12    */
        //测试
        /*
         * string
         *         hello
         *             sinny
         *             fredric
         *         world
         *           Hi
         *           York
         * */
        
        tree<String> tree = new tree();
        tree.addNode(null, "string");
        tree.addNode(tree.getNode("string"), "hello");
        tree.addNode(tree.getNode("string"), "world");
        tree.addNode(tree.getNode("hello"), "sinny");
        tree.addNode(tree.getNode("hello"), "fredric");
        tree.addNode(tree.getNode("world"), "Hi");
        tree.addNode(tree.getNode("world"), "York");
        tree.showNode(tree.root);
        
        System.out.println("end of the test");
    }

}