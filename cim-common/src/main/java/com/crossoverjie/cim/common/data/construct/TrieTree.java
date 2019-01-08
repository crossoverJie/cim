package com.crossoverjie.cim.common.data.construct;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2019/1/7 18:58
 * @since JDK 1.8
 */
public class TrieTree {

    private Node root ;

    public TrieTree() {
        root = new Node(null);
    }


    public void insert(String data){
        char[] chars = data.toCharArray();
        for (char aChar : chars) {

        }
    }

    private class Node{
        private Node left ;
        private Node right ;
        private Character data ;

        public Node(Character data) {
            this.data = data;
        }

        public Node(Node left, Node right, Character data) {
            this.left = left;
            this.right = right;
            this.data = data;
        }
    }
}
