package com.app;

public class app {

    /**
     * @param args
*/
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        /*��ʵ��һ�����Ľṹ���������ƽ���xml             */
        /*д�����õģ���������һЩ��������                2012-3-12    */
        //����
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